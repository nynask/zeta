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

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.nynask.general.Os;
import org.nynask.general.Zip;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Zta
{
  private static final String OUTPUT_TXT = "txt";
  private static final String OUTPUT_ODT = "odt";
  private static final String OUTPUT_DOCX = "docx";
  private static final String OUTPUT_PDF = "pdf";
  private static String DOCX_TEMPLATE_DIR = null;
  private static String ODT_TEMPLATE_DIR = null;
  private static String TXT_TEMPLATE_DIR = null;
  private static String DOCX_TEMPLATE_FILE = null;
  private static String ODT_TEMPLATE_FILE = null;
  private static HashMap<String, String> templates = new HashMap<>();
  private static Boolean GENERATE_TXT = Boolean.FALSE;
  private static Boolean GENERATE_ODT = Boolean.FALSE;
  private static Boolean GENERATE_DOCX = Boolean.FALSE;
  private static Boolean GENERATE_PDF = Boolean.FALSE;
  private static String RESULT_DIRECTORY = "." + File.separator;

  public static void generate(
    ZipFile zta,
    FileReader data,
    Information information,
    String templateDir
  )
  {
    TXT_TEMPLATE_DIR = templateDir + "txt" + File.separator + "template";
    ODT_TEMPLATE_DIR = templateDir + "odt" + File.separator + "template";
    DOCX_TEMPLATE_DIR = templateDir + "docx" + File.separator + "template";
    ODT_TEMPLATE_FILE = ODT_TEMPLATE_DIR + File.separator + "content.xml";
    DOCX_TEMPLATE_FILE = DOCX_TEMPLATE_DIR + File.separator + "word" +
                         File.separator + File.separator + "document.xml";

    try
    {
      char separator = information.getDataSeparator().charAt(0);
      CSVReader reader = new CSVReader(data, separator);
      String [] nextLine;
      int line = 0;
      HashMap<Integer, String> mapping;
      mapping = new HashMap<>();
      HashMap<String, String> row;
      row = new HashMap<>();

      templates.put(
        OUTPUT_TXT,
        loadFile(zta, "/templates/txt/template.txt")
      );
      templates.put(
        OUTPUT_ODT,
        loadFile(zta, "/templates/odt/template/content.xml")
      );
      templates.put(
        OUTPUT_DOCX,
        loadFile(zta, "/templates/docx/template/word/document.xml")
      );

      while ((nextLine = reader.readNext()) != null)
      {
        line++;

        for (int i = 0; i < nextLine.length; i++)
        {
          if (line == 1)
          {
            //TODO mapping data / template
            mapping.put(i, nextLine[i]);
          }
          else
          {
            row.put(mapping.get(i), nextLine[i]);
          }
        }

        if (line > 1)
        {
          createDocument(row, information);
        }
      }

      try
      {
        Files.createTempDirectory("zta");
      }
      catch (IOException ex)
      {
        System.out.println("ERROR-1");
        Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    catch (IOException ex)
    {
      System.out.println("ERROR-2");
      Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static Information loadInformation(ZipFile zta)
  {
    Information information = new Information();
    Enumeration<? extends ZipEntry> entries = zta.entries();
    StringBuilder xml = new StringBuilder();
    int i;

    while (entries.hasMoreElements())
    {
      ZipEntry entry = entries.nextElement();

      if (entry.getName().equals("zta.xml"))
      {
        try
        {
          InputStream stream = zta.getInputStream(entry);

          while ((i = stream.read()) != -1)
          {
            xml.append((char)i);
          }

          information = parseXml(xml.toString());
        }
        catch (IOException ex)
        {
          System.out.println("ERROR-3");
          Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    return information;
  }

  private static void createDocument(
    HashMap<String, String> data,
    Information information
  )
  {
    String txtOutput;
    String odtOutput;
    String docxOutput;
    String tag;
    String filename = information.getDocumentFilename();
    String preTagDelimiter = information.getDocumentPreTagDelimiter();
    String postTagDelimiter = information.getDocumentPostTagDelimiter();

    // Reset templates
    txtOutput = templates.get(OUTPUT_TXT);
    odtOutput = templates.get(OUTPUT_ODT);
    docxOutput = templates.get(OUTPUT_DOCX);

    for (String key : data.keySet())
    {
      tag = preTagDelimiter + key + postTagDelimiter;
      txtOutput = txtOutput.replace(tag, data.get(key));
      odtOutput = odtOutput.replace(tag, data.get(key));
      docxOutput = docxOutput.replace(tag, data.get(key));
      filename = filename.replace(tag, data.get(key));
    }

    filename = filename.replace(" ", "_");
    String txtFilename = filename + "." + OUTPUT_TXT;
    String odtFilename = filename + "." + OUTPUT_ODT;
    String docxFilename = filename + "." + OUTPUT_DOCX;

    PrintWriter writer;

    // TXT output
    if (GENERATE_TXT.equals(Boolean.TRUE))
    {
      try {
        writer = new PrintWriter(
          new OutputStreamWriter(
            new FileOutputStream(RESULT_DIRECTORY + txtFilename),
            "UTF-8"
          )
        );
        writer.print(txtOutput);
        writer.close();
      }
      catch (Exception ex)
      {
        System.out.println("ERROR-4");
        Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    // ODT output
    writeCompressedFile(
      GENERATE_ODT,
      odtOutput,
      ODT_TEMPLATE_FILE,
      RESULT_DIRECTORY + odtFilename,
      ODT_TEMPLATE_DIR
    );

    // DOCX output
    writeCompressedFile(
      GENERATE_DOCX,
      docxOutput,
      DOCX_TEMPLATE_FILE,
      RESULT_DIRECTORY + docxFilename,
      DOCX_TEMPLATE_DIR
    );

    // Generate PDF
    generatePdf(
      odtFilename,
      docxFilename,
      txtFilename
    );
  }

  private static void generatePdf(
    String odtFilename,
    String docxFilename,
    String txtFilename
  )
  {
    Process pr;
    String command = "";

    if (Os.isUnix())
    {
      command = "libreoffice --headless --convert-to pdf ";
    }
    else if (Os.isWindows())
    {
      command = "soffice.exe --headless --convert-to pdf ";
    }
    else if (Os.isMac())
    {
      //command = "libreoffice --headless --convert-to pdf ";
    }
    else
    {
      // Unknown OS
    }

    Boolean isSourceFileDefined = Boolean.TRUE;
    String pdfFilename = "";

    if (GENERATE_ODT.equals(Boolean.TRUE))
    {
      command = command + RESULT_DIRECTORY + odtFilename;
      pdfFilename = odtFilename.substring(0, odtFilename.length() - 3);
    }
    else if (GENERATE_DOCX.equals(Boolean.TRUE))
    {
      command = command + RESULT_DIRECTORY + docxFilename;
      pdfFilename = odtFilename.substring(0, docxFilename.length() - 4);
    }
    else if (GENERATE_TXT.equals(Boolean.TRUE))
    {
      command = command + RESULT_DIRECTORY + txtFilename;
      pdfFilename = odtFilename.substring(0, txtFilename.length() - 3);
    }
    else
    {
      isSourceFileDefined = Boolean.FALSE;
    }

    pdfFilename = pdfFilename + "pdf";

    if (isSourceFileDefined.equals(Boolean.TRUE))
    {
      try
      {
        Runtime runtime = Runtime.getRuntime();
        pr = runtime.exec(command);
        pr.waitFor();
        
        if (!RESULT_DIRECTORY.equals("." + File.separator))
        {
          FileUtils.moveFileToDirectory(
            new File(pdfFilename),
            new File(RESULT_DIRECTORY),
            true
          );
        }
      }
      catch (Exception ex)
      {
        System.out.println("ERROR-5");
        Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private static String loadFile(ZipFile zta, String entryName)
  {
    Enumeration<? extends ZipEntry> entries = zta.entries();
    StringBuilder output = new StringBuilder();
    String ztaEntryName = null;

    while (entries.hasMoreElements())
    {
      ZipEntry entry = entries.nextElement();
      
      if (entry.getName().substring(0, 1).equals("/"))
      {
        ztaEntryName = entry.getName();
      }
      else
      {
        ztaEntryName = "/" + entry.getName();
      }

      if (ztaEntryName.equals(entryName))
      {
        try
        {
          InputStream stream = zta.getInputStream(entry);
          InputStreamReader isr = new InputStreamReader(stream, "UTF-8");

          try (Reader in = new BufferedReader(isr))
          {
            int ch;

            while ((ch = in.read()) > -1)
            {
              output.append((char)ch);
            }
          }
          catch (Exception ex)
          {
            System.out.println("ERROR-6");
          }
        }
        catch (IOException ex)
        {
          System.out.println("ERROR-7");
          Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    return output.toString();
  }

  private static LinkedList<String> parseKeywords(String keywords)
  {
    LinkedList<String> result = new LinkedList<>();
    String[] items = keywords.split(",");

    for (String item : items)
    {
      result.add(item.trim());
    }

    return result;
  }

  private static Information parseXml(String xml)
  {
    Information information = new Information();

    try
    {
      InputSource source = new InputSource(new StringReader(xml));
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = (Document) db.parse(source);
      XPathFactory xpathFactory = XPathFactory.newInstance();
      XPath xpath = xpathFactory.newXPath();

      HashMap<String, String> mapping = new HashMap<>();
      mapping.put("AuthorName", "/zta/information/author");
      mapping.put("AuthorOrganization", "/zta/information/organization");
      mapping.put("AuthorEpost", "/zta/information/epost");
      mapping.put("AuthorWebsite", "/zta/information/website");
      mapping.put("DocumentCreated", "/zta/document/created");
      mapping.put(
        "DocumentDescription", "/zta/document/description"
      );
      mapping.put("DocumentLanguage", "/zta/document/language");
      mapping.put(
        "DocumentPreTagDelimiter", "/zta/document/preTagDelimiter"
      );
      mapping.put(
        "DocumentPostTagDelimiter", "/zta/document/postTagDelimiter"
      );
      mapping.put("DocumentFilename", "/zta/document/filename");
      mapping.put("DocumentTitle", "/zta/document/title");

      String methodName;
      String attributeValue;
      Method method;

      for (String key : mapping.keySet())
      {
        methodName = "set" + key;
        attributeValue = xpath.evaluate(mapping.get(key), document);
        try
        {
          method = information.getClass().getMethod(
            methodName,
            new Class[]{String.class}
          );
          method.invoke(information, new Object[]{attributeValue});
        }
        catch (
          NoSuchMethodException |
          SecurityException |
          IllegalAccessException |
          IllegalArgumentException |
          InvocationTargetException ex
        )
        {
          System.out.println("ERROR-8");
          Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
        }
      }

      information.setVersion(
        new Float(xpath.evaluate("/zta/@version", document))
      );

      information.setDocumentKeywords(
        parseKeywords(xpath.evaluate("/zta/document/keywords", document))
      );

      return information;
    }
    catch (
      ParserConfigurationException |
      SAXException |
      IOException |
      XPathExpressionException ex
    )
    {
      System.out.println("ERROR-9");
      Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private static void writeCompressedFile(
    Boolean generate,
    String output,
    String templateFile,
    String filename,
    String templateDir
  )
  {
    PrintWriter writer;

    if (generate.equals(Boolean.TRUE))
    {
      try {
        writer = new PrintWriter(
          new OutputStreamWriter(
            new FileOutputStream(templateFile),
            "UTF-8"
          )
        );  
        writer.print(output);
        writer.close();
        Zip.compress(filename, templateDir);
      } catch (Exception ex) {
        System.out.println("ERROR-10");
        Logger.getLogger(Zta.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static Boolean validateZtaXml(String ztaXmlPath)
  {
    return Boolean.TRUE;
  }

  public static void setGenerateTxt(Boolean generateTxt)
  {
    GENERATE_TXT = generateTxt;
  }

  public static void setGenerateOdt(Boolean generateOdt)
  {
    GENERATE_ODT = generateOdt;
  }

  public static void setGenerateDocx(Boolean generateDocx)
  {
    GENERATE_DOCX = generateDocx;
  }

  public static void setGeneratePdf(Boolean generatePdf)
  {
    GENERATE_PDF = generatePdf;
  }

  public static void setResultDirectory(String resultDirectory)
  {
    int len = resultDirectory.length();
    
    if (resultDirectory.substring(len - 2, len - 1).equals(File.separator))
    {
      RESULT_DIRECTORY = resultDirectory;
    }
    else
    {
      RESULT_DIRECTORY = resultDirectory + File.separator;
    }
  }
}
