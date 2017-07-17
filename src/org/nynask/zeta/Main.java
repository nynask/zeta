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
package org.nynask.zeta;

import org.nynask.general.Zip;
import org.nynask.general.OptionHandler;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;

public class Main
{
  private static final String VERSION = "0.2.1";

  public static void main(String[] args)
  {
    OptionHandler.parse(args);

    if (OptionHandler.isOptionDefined("h") ||
        OptionHandler.isOptionDefined("?"))
    {
      printHelp();
      System.exit(0);
    }

    if (OptionHandler.isOptionDefined("v"))
    {
      printVersion();
      System.exit(0);
    }

    ArrayList<String> mandatoryAttributeList = new ArrayList<>();
    mandatoryAttributeList.add("z"); // zta
    mandatoryAttributeList.add("d"); // data

    OptionHandler.setMandatoryAttributeList(mandatoryAttributeList);

    int tempDirKey = generateKey();
    String tempDir = "." + File.separator + "temp_" + tempDirKey;

    try
    {
      ZipFile zta = new ZipFile(OptionHandler.optionValue("z"));
      FileReader data = new FileReader(OptionHandler.optionValue("d"));
      Zip.decompress(OptionHandler.optionValue("z"), tempDir);
      Boolean isZtaXmlValid = Zta.validateZtaXml(
        tempDir + File.separator + "zta.xml"
      );
      String templateDir = tempDir + File.separator + "templates" +
                           File.separator;
      Information information = Zta.loadInformation(zta);

      if (isZtaXmlValid.equals(Boolean.TRUE))
      {
        Zta.setGenerateOdt(OptionHandler.isOptionDefined("o"));
        Zta.setGenerateDocx(OptionHandler.isOptionDefined("w"));
        Zta.setGenerateTxt(OptionHandler.isOptionDefined("t"));
        Zta.setGeneratePdf(OptionHandler.isOptionDefined("p"));

        if (OptionHandler.isOptionDefined("r"))
        {
          Zta.setResultDirectory(OptionHandler.optionValue("r"));
        }

        Zta.generate(zta, data, information, templateDir);
      }
      else
      {
        System.out.println("Invalid zta.xml");
      }
    }
    catch (IOException ex)
    {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
    finally
    {
      try
      {
        FileUtils.deleteDirectory(new File(tempDir));
      }
      catch (IOException ex)
      {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private static void printHelp()
  {
    System.out.println("");
    System.out.println(
      "java -jar zeta.jar -otwp -z /path/to/archive.zta -d /path/to/data.csv"
    );
    System.out.println("");
    System.out.println("-h    Help");
    //System.out.println("-c    Configuration file");
    System.out.println("-d    Location of data file");
    System.out.println("-o    Generate .odt file(s)");
    System.out.println("-p    Generate .pdf file(s)");
    System.out.println("-r    Directory for result");
    System.out.println("-t    Generate .txt file(s)");
    System.out.println("-v    Version number");
    System.out.println("-w    Generate .docx file(s)");
    System.out.println("-z    Location of ZTA archive");
    System.out.println("");
  }

  private static void printVersion()
  {
    System.out.println("Zeta " + VERSION);
    System.out.println("Copyright (c) 2013-2017 NPO Nynask");
  }

  private static int generateKey()
  {
    Random rand = new Random();
    int key = rand.nextInt();

    if (key < 0)
    {
      key = key * (-1);
    }

    return key;
  }
}
