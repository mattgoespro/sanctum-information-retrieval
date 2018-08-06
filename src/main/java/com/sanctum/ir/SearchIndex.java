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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to retrieve Tweets from the index.
 * @author Matt
 */
public class SearchIndex {
    
    /**
     * Returns all Tweets containing a specific term.
     * @param term
     * @return ArrayList
     */
    public static ArrayList<String> search(String term) {
        ArrayList<String> result = new ArrayList();
        String[] documents = documents(term).split("; ");
        System.out.println(documents.length-1 + " documents found.");
        
        for (int i = 1; i < documents.length; i++) {
            String doc = documents[i].substring(0, documents[i].indexOf("("));
            int line = Integer.parseInt(documents[i].substring(documents[i].indexOf("(") + 1, documents[i].indexOf(")")));
            System.out.println("Looking in document " + doc + " at line " + line);
            result.add(getTweet(doc, line));
        }
        return result;
    }
    
    /**
     * Returns the Tweet text for a document on a specific line.
     * @param doc
     * @param line
     * @return String
     */
    private static String getTweet(String doc, int line) {
        try {
            Scanner scFile = new Scanner(new File(doc));
            int currLine = 0;
            
            while(scFile.hasNextLine()) {
                if(currLine == line) {
                    return scFile.nextLine();
                }
                scFile.nextLine();
                ++currLine;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("none");
        return null;
    }
    
    /**
     * Returns a list of documents for a term as a string.
     * @param term
     * @return String
     */
    private static String documents(String term) {
        String result = null;
        System.out.println("Checking index/index_" + term.charAt(0) + ".txt");
        File ind = new File("index/index_" + term.charAt(0) + ".txt");
        
        if(!ind.exists()) {
             System.out.println("No index found for term '" + term + "'");
             return null;
        }
        
        try {
            Scanner scFile = new Scanner(ind);
            
            while(scFile.hasNextLine()) {
                String line = scFile.nextLine();
                
                if(line.split("; ")[0].equalsIgnoreCase(term)) {
                    return line;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
