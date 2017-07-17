/*
 * Copyright (c) 2013-2017 NPO Nynask
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.nynask.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip
{
  private static final int BUFFER = 2048;
  private static List<String> fileList = new ArrayList<>();

  public static void compress(String outputFile, String sourceFolder)
  {
    fileList = new ArrayList<>();
    String zipFile = outputFile;
    generateFileList(new File(sourceFolder), sourceFolder);
    byte[] buffer = new byte[BUFFER];
    FileOutputStream fos = null;
    ZipOutputStream zos = null;

    try
    {
      fos = new FileOutputStream(zipFile);
      zos = new ZipOutputStream(fos);
      FileInputStream in = null;

      for (String file : fileList)
      {
        ZipEntry ze = new ZipEntry(file);

        // odt specific
        if (file.equals("mimetype"))
        {
          zos.setLevel(ZipOutputStream.STORED);
        }
        else
        {
          zos.setLevel(ZipOutputStream.DEFLATED);
        }

        zos.putNextEntry(ze);

        try
        {
          in = new FileInputStream(sourceFolder + File.separator + file);
          int len;

          while ((len = in.read(buffer)) > 0)
          {
            zos.write(buffer, 0, len);
          }
        }
        finally
        {
          in.close();
        }
      }

      zos.closeEntry();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      try
      {
        zos.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  public static void decompress(String zipFile, String outputFolder)
  {

    byte[] buffer = new byte[BUFFER];
    ZipInputStream zis = null;

    try
    {
      File folder = new File(outputFolder);

      if (!folder.exists())
      {
        folder.mkdir();
      }

      zis = new ZipInputStream(new FileInputStream(zipFile));
      ZipEntry ze = zis.getNextEntry();

      while (ze != null)
      {
        String fileName = ze.getName();
        File newFile = new File(outputFolder + File.separator + fileName);

        if (ze.isDirectory())
        {
          new File(newFile.getParent()).mkdirs();
        }
        else
        {
          FileOutputStream fos;
          new File(newFile.getParent()).mkdirs();
          fos = new FileOutputStream(newFile);
          int len;

          while ((len = zis.read(buffer)) > 0)
          {
           fos.write(buffer, 0, len);
          }

          fos.close();
        }

        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();
    }
    catch(IOException ex)
    {
       ex.printStackTrace();
    }
    finally
    {
      try
      {
        zis.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  private static void generateFileList(File node, String sourceFolder)
  {
    if (node.isFile())
    {
      fileList.add(generateZipEntry(node.toString(), sourceFolder));
    }

    if (node.isDirectory())
    {
      String[] subNote = node.list();

      for (String filename : subNote)
      {
        generateFileList(new File(node, filename), sourceFolder);
      }
    }
  }

  private static String generateZipEntry(String file, String sourceFolder)
  {
    return file.substring(sourceFolder.length() + 1, file.length());
  }
}
