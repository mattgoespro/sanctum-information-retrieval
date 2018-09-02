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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TweetLoader class that loads only a part of a data file.
 *
 * @author Matt
 */
public class PartialTweetLoader extends TweetLoader {
    
    private final int id;
    private final int numTweets;

    /**
     * Constructor
     *
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
        BufferedReader scFile = new BufferedReader(new FileReader(this.file));
        int startLine = this.id * this.numTweets;
        int endLine = startLine + this.numTweets - 1;
        int currLine = 0;
        int count = 0;

        String line = scFile.readLine();

        while (line != null) {
            if (currLine >= startLine) {
                String docName = writeTweetDocument(line);
                this.tweets[count] = new Tweet(docName, line, super.filter);
                this.tweets[count].filter();
                
                ++count;

                if (currLine == endLine) {
                    break;
                }
            }
            ++currLine;
            line = scFile.readLine();
        }
    }
    
    /**
     * Writes a text document containing a single tweet, and returns the file name.
     * @param line
     * @return String
     */
    private String writeTweetDocument(String line) throws IOException {
        String docName = "tweet_" + line.hashCode();
        File f = new File("tweet_documents/" + docName);
        
        try (FileWriter docWriter = new FileWriter(f)) {
            docWriter.write(line);
            ThreadedDataLoader.pathStore.put(f.getAbsolutePath());
        }
        return f.getAbsolutePath();
    }

}
