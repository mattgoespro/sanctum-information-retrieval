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
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * Loads the text from a file into its respective Tweets.
 *
 * @author Matt
 */
public class TweetLoader {

    protected File file;
    protected String fileName;
    protected Tweet[] tweets;

    /**
     * Constructor
     *
     * @param fileName
     */
    public TweetLoader(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
    }

    /**
     * Reads the Tweets from the file specified.
     * @throws java.io.IOException
     */
    public void readTweets() throws IOException {
        this.tweets = new Tweet[fileSize(this.fileName)];
        BufferedReader fileReader = new BufferedReader(new FileReader(this.fileName));
        int count = 0;
        String line = fileReader.readLine();
        
        POSTaggerME posTagger = new POSTaggerME(new POSModel(new File("pos_learning_models/en-pos-maxent.bin")));
        TokenizerME tokenizer = new TokenizerME(new TokenizerModel(new File("pos_learning_models/en-token.bin")));
        
        while (line != null) {
            if (!line.equals("")) {
                this.tweets[count] = new Tweet(this.fileName, count, line);
                this.tweets[count].tagText(posTagger, tokenizer);
                ++count;
            }
            line = fileReader.readLine();
        }
    }

    /**
     * Returns the number of lines in a File.
     *
     * @param fileName
     * @return Integer
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int fileSize(String fileName) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        int lines = 0;

        while (reader.readLine() != null) {
            lines++;
        }

        reader.close();
        return lines;
    }

    /**
     * Returns the file that is being read.
     *
     * @return File
     */
    public File getFile() {
        return this.file;
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
