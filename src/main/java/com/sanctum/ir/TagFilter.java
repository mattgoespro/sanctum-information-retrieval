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
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class that is used to filter and process tags.
 * @author Matt
 */
public class TagFilter {
    
    public static String TAG_BLACKLIST = "lib/indexing_token_blacklist.txt";
    
    private ArrayList<String> tagValueBlacklist;
    private ArrayList<String> tagPosBlacklist;
    
    /**
     * Loads the list of words from a file to exclude from indexing.
     * @param blacklistFile
     * @throws FileNotFoundException 
     */
    public void loadBlacklist(String blacklistFile) throws FileNotFoundException {
        this.tagValueBlacklist = new ArrayList();
        this.tagPosBlacklist = new ArrayList();
        Scanner scFile = new Scanner(new File(blacklistFile));
        String line = scFile.nextLine();
        
        while(line != null) {
            if(!line.startsWith("#")) {
                if(!line.startsWith("!")) {
                    this.tagValueBlacklist.add(line);
                } else {
                    this.tagPosBlacklist.add(line.substring(1));
                }
            }
            line = scFile.nextLine();
        }
        
        scFile.close();
    }
}
