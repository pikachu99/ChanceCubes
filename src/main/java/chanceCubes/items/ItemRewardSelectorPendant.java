package chanceCubes.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chanceCubes.CCubesCore;
import chanceCubes.blocks.CCubesBlocks;
import chanceCubes.client.gui.RewardSelectorPendantGui;
import chanceCubes.registry.ChanceCubeRegistry;

public class ItemRewardSelectorPendant  extends Item
{
	public String itemNameID = "reward_Selector_Pendant";
	
	public ItemRewardSelectorPendant()
	{
		this.setUnlocalizedName(itemNameID);
		this.setMaxStackSize(1);
		this.setCreativeTab(CCubesCore.modTab);
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(player.isSneaking() && world.isRemote)
			FMLCommonHandler.instance().showGuiScreen(new RewardSelectorPendantGui(player, stack));
		return stack;
	}

	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return false;
		if(player.isSneaking())
			return false;
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Reward"))
		{
			BlockPos position = new BlockPos(x, y, z);
			if(world.getBlockState(position).getBlock().equals(CCubesBlocks.chanceCube))
			{
				world.setBlockToAir(position);
				ChanceCubeRegistry.INSTANCE.getRewardByName(stack.getTagCompound().getString("Reward")).trigger(world, position, player);
			}
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) 
	{
		list.add("Shift right click to change the reward.");
		list.add("Right click a Chance Cube to summon the reward.");
	}
}
