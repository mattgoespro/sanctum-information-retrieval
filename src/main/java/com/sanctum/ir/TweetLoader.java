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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads the text from a file into its respective Tweets.
 *
 * @author Matt
 */
public class TweetLoader {

    protected File file;
    protected String fileName;
    protected Tweet[] tweets;
    protected TagFilter filter;

    /**
     * Constructor
     *
     * @param fileName
     */
    public TweetLoader(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
        this.filter = new TagFilter();

        try {
            this.filter.loadBlacklist(null);
        } catch (FileNotFoundException ex) {}
        catch (IOException ex) {}
    }

    /**
     * Reads the Tweets from the file specified.
     *
     * @throws java.io.IOException
     */
    public void readTweets() throws IOException {
        this.tweets = new Tweet[60000];
        BufferedReader fileReader = new BufferedReader(new FileReader(this.fileName));
        int count = 0;
        String line = fileReader.readLine();

        while (line != null) {
            if (!line.equals("")) {
                this.tweets[count] = new Tweet(this.fileName, line, filter);
                this.tweets[count].filter();
                ++count;
            }
            line = fileReader.readLine();
        }
    }

    /**
     * Returns the array of Tweet objects read from the file.
     *
     * @return Tweet[]
     */
    public Tweet[] getTweets() {
        return this.tweets;
    }

}
