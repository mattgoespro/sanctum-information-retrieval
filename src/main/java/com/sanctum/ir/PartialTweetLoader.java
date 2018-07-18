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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * TweetLoader class that loads only a part of a data file.
 * @author Matt
 */
public class PartialTweetLoader extends TweetLoader {
    
    private int id, numTweets;
    
    /**
     * Constructor
     * @param fileName
     * @param id
     * @param numTweets 
     */
    public PartialTweetLoader(String fileName, int id, int numTweets) {
        super(fileName);
        this.id = id;
        this.numTweets = numTweets;
    }
    
    @Override
    public void readTweets() throws IOException {
        this.tweets = new Tweet[this.numTweets];
        BufferedReader fileReader = new BufferedReader(new FileReader(this.fileName));
        int startLine = this.id * this.numTweets;
        int endLine = startLine + this.numTweets - 1;
        int currLine = 0;
        int count = 0;
        String line = fileReader.readLine();
        
        while (line != null) {
            if (!line.replaceAll("\\s+", "").equals("")) {
                ++currLine;
                if(currLine < startLine) {
                    continue;
                }
                
                this.tweets[count] = new Tweet(this.fileName, count, line);
                //this.tweets[count].tagText();
                ++count;
                
                if(currLine == endLine) break;
            }
            line = fileReader.readLine();
        }
    }
    
}
