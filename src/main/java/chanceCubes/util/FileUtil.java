package chanceCubes.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;

import chanceCubes.CCubesCore;

public class FileUtil
{
	@Nonnull
	public static File writeToFile(String filepath, String json)
	{
		File file = new File(filepath);

		try
		{
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(json);
			fw.flush();
			fw.close();
			return file;
		} catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Nonnull
	public static void safeDelete(File file)
	{
		try
		{
			file.delete();
		} catch(Exception e)
		{
			CCubesCore.logger.error("Deleting file " + file.getAbsolutePath() + " failed.");
		}
	}

	@Nonnull
	public static void safeDeleteDirectory(File file)
	{
		try
		{
			FileUtils.deleteDirectory(file);
		} catch(Exception e)
		{
			CCubesCore.logger.error("Deleting directory " + file.getAbsolutePath() + " failed.");
		}
	}

	public static void copyFromJar(Class<?> jarClass, String filename, File to)
	{
		CCubesCore.logger.info("Copying file " + filename + " from jar");
		URL url = jarClass.getResource("/assets/" + filename);

		try
		{
			FileUtils.copyURLToFile(url, to);
		} catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("resource")
	public static void zipFolderContents(File directory, File zipfile) throws IOException
	{
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		OutputStream out = new FileOutputStream(zipfile);
		Closeable res = out;
		try
		{
			ZipOutputStream zout = new ZipOutputStream(out);
			res = zout;
			while(!queue.isEmpty())
			{
				directory = queue.pop();
				for(File child : directory.listFiles())
				{
					String name = base.relativize(child.toURI()).getPath();
					if(child.isDirectory())
					{
						queue.push(child);
						name = name.endsWith("/") ? name : name + "/";
						zout.putNextEntry(new ZipEntry(name));
					}
					else
					{
						zout.putNextEntry(new ZipEntry(name));
						copy(child, zout);
						zout.closeEntry();
					}
				}
			}
		} finally
		{
			res.close();
		}
	}

	private static void copy(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		while(true)
		{
			int readCount = in.read(buffer);
			if(readCount < 0)
			{
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private static void copy(File file, OutputStream out) throws IOException
	{
		InputStream in = new FileInputStream(file);
		try
		{
			copy(in, out);
		} finally
		{
			in.close();
		}
	}

}