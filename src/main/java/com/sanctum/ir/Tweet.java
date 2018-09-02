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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tweet class represents the raw tweet data and its tagged parts.
 *
 * @author Matt
 */
public class Tweet {

    private final TagFilter filter;
    private final String containedFile;
    private final String rawText;
    private final ArrayList<String> words;
    private String[] tokenizedRawText;

    /**
     * Constructor
     *
     * @param containedFile
     * @param rawText
     * @param filter
     */
    public Tweet(String containedFile, String rawText, TagFilter filter) {
        this.containedFile = containedFile;
        this.rawText = rawText;
        this.words = new ArrayList();
        this.filter = filter;
    }

    /**
     * Tags the text of the tweet with its parts of speech.
     */
    public void filter() {
        tokenizedRawText = rawText.split(" ");

        for (String word : tokenizedRawText) {
            if (!word.startsWith("http")) {
                word = word.replaceAll("\\p{Punct}", " ");
                String[] process = word.split(" ");

                for (String s : process) {
                    if (!s.equals("")) {
                        words.add(s);
                    }
                }
            } else {
                words.add(word);
            }
        }
        filter.filterText(words);
    }

    /**
     * Returns the directory of the file containing this Tweet.
     *
     * @return Strings
     */
    public String getContainingFileName() {
        return this.containedFile;
    }
    
    /**
     * Returns a list of filtered words in the Tweet.
     * @return ArrayList
     */
    public ArrayList<String> getWords() {
        return this.words;
    }
    
    @Override
    public String toString() {
        String str = "";
        for(String word : words) {
            str += word + " ";
        }
        
        return str;
    }
}
