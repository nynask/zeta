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

import java.util.ArrayList;
import java.util.HashMap;

public class OptionHandler
{
  private static ArrayList<String> mandatoryAttributeList = new ArrayList<>();
  private static HashMap<String, String> optionList = new HashMap<>();

  public static void parse(String[] args)
  {
    String option;
    option = null;

    for (String arg : args)
    {
      if (arg.charAt(0) == '-')
      {
        option = null;
        
        if (arg.length() >= 2)
        {
          for (int j = 1; j < arg.length(); j++)
          {
            optionList.put(String.valueOf(arg.charAt(j)), null);
            
            if (j == 1)
            {
              option = String.valueOf(arg.charAt(j));
            }
          }
        }
      }
      else
      {
        if (optionList.containsKey(option))
        {
          optionList.put(option, arg);
        }
      }
    }
  }

  public static ArrayList<String> getMandatoryAttributeList()
  {
    return mandatoryAttributeList;
  }

  public static void setMandatoryAttributeList(ArrayList<String> list)
  {
    mandatoryAttributeList = (ArrayList<String>) list;
  }

  public static void addMandatoryAttribute(String attribute)
  {
    mandatoryAttributeList.add(attribute);
  }

  public static String optionValue(String option)
  {
    return optionList.get(option);
  }

  public static Boolean isOptionDefined(String option)
  {
    if (optionList.containsKey(option))
    {
      return Boolean.TRUE;
    }
    else
    {
      return Boolean.FALSE;
    }
  }
}
