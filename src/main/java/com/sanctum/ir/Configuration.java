/*
 * Copyright (C) 2018 Matt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.sanctum.ir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Loads the IR configuration.
 * @author Matt
 */
public class Configuration {
    
    public static final String POS_LEARNING_MODEL = "Parts-of-speech learning model", INDEXING_TOKEN_BLACKLIST = "Indexing token blacklist", 
            DATA_DIRECTORY = "Twitter data directory", INDEXING_INCLUDE_HASHTAGS = "Include hashtags", INDEXING_INCLUDE_MENTIONS = "Include mentions",
            INDEXING_INCLUDE_LINKS = "Include links", INDEX_SAVE_DIRECTORY = "Inverted file save directory";
    private static final HashMap<String, String> CONF_STRINGS = new HashMap(); 
    
    /**
     * Loads the configuration. Returns true if successful.
     * @param fileName
     * @return boolean
     */
    public static boolean loadConfiguration(String fileName) {
        File conf = new File(fileName);
        
        if(conf.exists()) {
            try {
                Scanner scFile = new Scanner(conf);
                String line;
                
                while(scFile.hasNext()) {
                    line = scFile.nextLine();
                    if(line.startsWith("#") || line.startsWith(" ") || line.equals("") || line.startsWith("\n")) continue;
                    
                    String value = line.replaceAll("\\s+", "");
                    value = value.substring(value.indexOf(":") + 1);
                    
                    if(line.startsWith(Configuration.POS_LEARNING_MODEL)) Configuration.CONF_STRINGS.put(Configuration.POS_LEARNING_MODEL, value);
                    else if(line.startsWith(Configuration.INDEXING_TOKEN_BLACKLIST)) Configuration.CONF_STRINGS.put(Configuration.INDEXING_TOKEN_BLACKLIST, value);
                    else if(line.startsWith(Configuration.DATA_DIRECTORY)) Configuration.CONF_STRINGS.put(Configuration.DATA_DIRECTORY, value);
                    else if(line.startsWith(Configuration.INDEXING_INCLUDE_HASHTAGS)) Configuration.CONF_STRINGS.put(Configuration.INDEXING_INCLUDE_HASHTAGS, value);
                    else if(line.startsWith(Configuration.INDEXING_INCLUDE_MENTIONS)) Configuration.CONF_STRINGS.put(Configuration.INDEXING_INCLUDE_LINKS, value);
                    else if(line.startsWith(Configuration.INDEXING_INCLUDE_LINKS)) Configuration.CONF_STRINGS.put(Configuration.INDEXING_INCLUDE_LINKS, value);
                    else if(line.startsWith(Configuration.INDEX_SAVE_DIRECTORY)) Configuration.CONF_STRINGS.put(Configuration.INDEX_SAVE_DIRECTORY, value);
                    else return false;
                }
                
                return true;
            } catch (FileNotFoundException ex) {
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Returns the configuration value specific
     * @param confValue
     * @return String
     */
    public static final String get(String confValue) {
        return Configuration.CONF_STRINGS.get(confValue);
    }
    
}
