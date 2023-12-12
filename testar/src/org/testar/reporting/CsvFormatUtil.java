/***************************************************************************************************
 *
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.reporting;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class CsvFormatUtil extends BaseFormatUtil
{
    private String               delimiter;
    private Map<Integer, Field>  fields; //the fields and their data
    private Map<String, Integer> fieldsIndex; //the lookup that insures a fixed order
    private boolean              fieldsLocked = false;
    
    public CsvFormatUtil(String filePath, String delimiter)
    {
        super(filePath, "csv");
        this.delimiter = delimiter;
        fields         = new HashMap<>();
        fieldsIndex    = new HashMap<>();
    }
    
    public void addNewField(String shortName, String name, String value)
    {
        if(!fieldsLocked)
        {
            Field newField = new Field(shortName, name, value);
            int   index    = fieldsIndex.size();
            fields.put(index, newField);
            fieldsIndex.put(shortName, index);
        }
    }
    
    public void addNewField(String shortName, String name)
    {
        if(!fieldsLocked)
        {
            Field newField = new Field(shortName, name, null);
            int   index    = fieldsIndex.size();
            fields.put(index, newField);
            fieldsIndex.put(shortName, index);
        }
    }
    
    public void setFieldValue(String shortName, String newValue)
    {
        Integer index = fieldsIndex.get(shortName);
        if(index != null) //no warning if field doesn't exist
        {
            Field field = fields.get(index);
            field.setValue(newValue);
        }
    }
    
    public void resetValue(String shortName)
    {
        Integer index = fieldsIndex.get(shortName);
        if(index != null) //no warning if field doesn't exist
        {
            Field field = fields.get(index);
            field.resetValue();
        }
    }
    
    public void resetValues()
    {
        for(Field field : fields.values())
            field.resetValue();
    }
    
    private void startNextRow()
    {
        if(!fieldsLocked)
        {
            for(int i = 0; i < fields.size(); i++)
                content.add(fields.get(i).getName());
            fieldsLocked = true;
        }
        
        StringJoiner joiner = new StringJoiner(this.delimiter);
        for(int i = 0; i < fields.size(); i++)
            joiner.add(fields.get(i).getValue());
        content.add(joiner.toString());
        resetValues();
        
        writeToFile();
    }

    private class Field
    {
        private final String shortName;
        private final String name;
        private       String value;
        
        public Field(String shortName, String name, String value)
        {
            this.shortName = shortName;
            this.name = name;
            this.value = value;
        }
        
        public String getShortName() {return shortName;}
        public String getName() {return name;}
        public String getValue() {return value;}
        public void setValue(String newValue) {value = newValue;}
        public void resetValue() {value = null;}
    }
}