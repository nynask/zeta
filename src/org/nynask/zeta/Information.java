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

import java.util.LinkedList;

public class Information
{
  private Float version = null;
  private String authorName = null;
  private String authorOrganization = null;
  private String authorEpost = null;
  private String authorWebsite = null;
  private String documentTitle = null;
  private String documentCreated = null;
  private String documentPreTagDelimiter = "{";
  private String documentPostTagDelimiter = "}";
  private String documentDescription = null;
  private LinkedList<String> documentKeywords = new LinkedList<>();
  private String documentLanguage = null;
  private String documentFilename = null;
  private String dataSeparator = ";";
  private Boolean isValid = Boolean.FALSE;

  public void validate()
  {

  }

  public Float getVersion()
  {
    return version;
  }

  public void setVersion(Float version)
  {
    this.version = version;
  }

  public String getAuthorName()
  {
    return authorName;
  }

  public void setAuthorName(String authorName)
  {
    this.authorName = authorName;
  }

  public String getAuthorOrganization()
  {
    return authorOrganization;
  }

  public void setAuthorOrganization(String authorOrganization)
  {
    this.authorOrganization = authorOrganization;
  }

  public String getAuthorEpost()
  {
    return authorEpost;
  }

  public void setAuthorEpost(String authorEpost)
  {
    this.authorEpost = authorEpost;
  }

  public String getAuthorWebsite()
  {
    return authorWebsite;
  }

  public void setAuthorWebsite(String authorWebsite)
  {
    this.authorWebsite = authorWebsite;
  }

  public String getDocumentTitle()
  {
    return documentTitle;
  }

  public void setDocumentTitle(String documentTitle)
  {
    this.documentTitle = documentTitle;
  }

  public String getDocumentCreated()
  {
    return documentCreated;
  }

  public void setDocumentCreated(String documentCreated)
  {
    this.documentCreated = documentCreated;
  }

  public String getDocumentPreTagDelimiter()
  {
    return documentPreTagDelimiter;
  }

  public void setDocumentPreTagDelimiter(String documentPreTagDelimiter)
  {
    this.documentPreTagDelimiter = documentPreTagDelimiter;
  }

  public String getDocumentPostTagDelimiter()
  {
    return documentPostTagDelimiter;
  }

  public void setDocumentPostTagDelimiter(String documentPostTagDelimiter)
  {
    this.documentPostTagDelimiter = documentPostTagDelimiter;
  }

  public String getDocumentDescription()
  {
    return documentDescription;
  }

  public void setDocumentDescription(String documentDescription)
  {
    this.documentDescription = documentDescription;
  }

  public boolean addDocumentKeyword(String keyword)
  {
    return documentKeywords.add(keyword);
  }

  public LinkedList<String> getDocumentKeywords()
  {
    return documentKeywords;
  }

  public void setDocumentKeywords(LinkedList<String> documentKeywords)
  {
    this.documentKeywords = documentKeywords;
  }

  public String getDocumentLanguage()
  {
    return documentLanguage;
  }

  public void setDocumentLanguage(String documentLanguage)
  {
    this.documentLanguage = documentLanguage;
  }

  public String getDocumentFilename()
  {
    return documentFilename;
  }

  public void setDocumentFilename(String documentFilename)
  {
    this.documentFilename = documentFilename;
  }

  public Boolean isValid()
  {
    return isValid;
  }

  public String getDataSeparator()
  {
    return dataSeparator;
  }

  public void setDataSeparator(String dataSeparator)
  {
    this.dataSeparator = dataSeparator;
  }
}
