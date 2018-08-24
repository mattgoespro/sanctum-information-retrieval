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
import java.io.IOException;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Loads the IR configuration.
 *
 * @author Matt
 */
public class Configuration {

    private static final String KEY_DATA_DIRECTORY = "Twitter data directory",
            KEY_INDEXING_INCLUDE_HASHTAGS = "Include hashtags",
            KEY_INDEXING_INCLUDE_MENTIONS = "Include mentions",
            KEY_INDEXING_INCLUDE_LINKS = "Include links",
            KEY_INDEX_SAVE_DIRECTORY = "Inverted file save directory";
    public static String DATA_DIRECTORY,
            INDEXING_INCLUDE_HASHTAGS,
            INDEXING_INCLUDE_MENTIONS,
            INDEXING_INCLUDE_LINKS,
            INDEX_SAVE_DIRECTORY;

    /**
     * Loads the configuration. Returns true if successful.
     *
     * @param fs
     * @return boolean
     * @throws java.io.IOException
     */
    public static boolean loadConfiguration(FileSystem fs) throws IOException {
        if (fs != null) {
            FSDataInputStream cfgStream = fs.open(new Path("/sanctum/config.cfg"));
            LineIterator lineIterator = IOUtils.lineIterator(cfgStream, "UTF-8");
            String line;

            while (lineIterator.hasNext()) {
                line = lineIterator.nextLine();
                setConfigValue(line);
            }
            
            return true;
        } else {
            Scanner scFile = new Scanner(new File("config.cfg"));
            String line;

            while (scFile.hasNext()) {
                line = scFile.nextLine();
                setConfigValue(line);
            }

            return true;
        }
    }

    private static void setConfigValue(String line) {
        if (line.startsWith("#") || line.startsWith(" ") || line.equals("") || line.startsWith("\n")) {
            return;
        }
        
        String value = line.replaceAll("\\s+", "");
        value = value.substring(value.indexOf(":") + 1);

        if (line.startsWith(com.sanctum.ir.Configuration.KEY_DATA_DIRECTORY)) {
            com.sanctum.ir.Configuration.DATA_DIRECTORY = value;
        } else if (line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_HASHTAGS)) {
            com.sanctum.ir.Configuration.INDEXING_INCLUDE_HASHTAGS = value;
        } else if (line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_MENTIONS)) {
            com.sanctum.ir.Configuration.INDEXING_INCLUDE_MENTIONS = value;
        } else if (line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_LINKS)) {
            com.sanctum.ir.Configuration.INDEXING_INCLUDE_LINKS = value;
        } else if (line.startsWith(com.sanctum.ir.Configuration.KEY_INDEX_SAVE_DIRECTORY)) {
            com.sanctum.ir.Configuration.INDEX_SAVE_DIRECTORY = value;
        }
    }
}
