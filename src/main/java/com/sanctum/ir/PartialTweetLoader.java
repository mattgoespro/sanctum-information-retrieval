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
import java.io.IOException;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * TweetLoader class that loads only a part of a data file.
 *
 * @author Matt
 */
public class PartialTweetLoader extends TweetLoader {

    private int id, numTweets;

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
        if (this.numTweets == 0) {
            return;
        }

        this.tweets = new Tweet[this.numTweets];
        BufferedReader scFile = new BufferedReader(new FileReader(this.file));
        int startLine = this.id * this.numTweets;
        int endLine = startLine + this.numTweets - 1;
        int currLine = 0;
        int count = 0;
        POSTaggerME posTagger = new POSTaggerME(new POSModel(new File("pos_learning_models/en-pos-maxent.bin")));
        TokenizerME tokenizer = new TokenizerME(new TokenizerModel(new File("pos_learning_models/en-token.bin")));
        
        String line = scFile.readLine();
        
        while (line != null) {
            if (currLine >= startLine) {
                this.tweets[count] = new Tweet(this.fileName, currLine, line);
                try {
                    this.tweets[count].tagText(posTagger, tokenizer);
                    //System.out.println(this.tweets[count]);
                } catch (Exception e) {}

                ++count;
                
                if (currLine == endLine) {
                    break;
                }
            }
            ++currLine;
            line = scFile.readLine();
        }
    }

}
