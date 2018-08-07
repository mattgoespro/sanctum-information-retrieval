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
import java.util.ArrayList;
import java.util.HashMap;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;

/**
 * Tweet class represents the raw tweet data and its tagged parts.
 *
 * @author Matt
 */
public class Tweet {
    
    private TagFilter filter;
    private final String containedFile;
    private final int tweetIndex;
    private final String rawText;
    private ArrayList<String> mentions;
    private ArrayList<String> hashtags;
    private ArrayList<String> links;
    private HashMap<String, String> wordTags;
    private String timeStamp;
    

    /**
     * Constructor
     *
     * @param containedFile
     * @param tweetIndex
     * @param rawText
     */
    public Tweet(String containedFile, int tweetIndex, String rawText) {
        this.containedFile = containedFile;
        this.tweetIndex = tweetIndex;
        this.rawText = rawText;
        this.mentions = new ArrayList();
        this.hashtags = new ArrayList();
        this.links = new ArrayList();
        this.filter = new TagFilter();

        try {
            this.filter.loadBlacklist("indexing_token_blacklist.cfg");
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to load blacklist file.");
        }
    }

    /**
     * Tags the text of the tweet with its parts of speech.
     * @param tagger
     * @param tokenizer
     */
    public void tagText(POSTaggerME tagger, TokenizerME tokenizer) {
        String[] words = tokenizer.tokenize(rawText);
        String[] tags = tagger.tag(words);

        // only if timestamp exists will it tag the words
        if (words[0].equals("Mon") || words[0].equals("Tue") || words[0].equals("Wed") || words[0].equals("Thu") || words[0].equals("Fri") || words[0].equals("Sat") || words[0].equals("Sun")) {
            this.timeStamp = words[0] + " " + words[1] + " " + words[2] + " " + words[3] + " " + words[4] + " " + words[5];
            this.wordTags = filter.filterText(words, tags);

            // retrieve hashtags
            if (filter.includesHashtags()) {
                for (int i = 7; i < words.length; i++) {
                    if (words[i].startsWith("#")) {
                        this.hashtags.add(words[i]);
                    }
                }
            }

            // retrieve mentions
            if (filter.includesMentions()) {
                for (int i = 7; i < words.length; i++) {
                    if (words[i].startsWith("@")) {
                        this.mentions.add(words[i]);
                    }
                }
            }

            // retrieve links
            if (filter.includesLinks()) {
                for (int i = 7; i < words.length; i++) {
                    if (words[i].startsWith("://")) {
                        this.links.add(words[i]);
                    }
                }
            }
        }
    }

    /**
     * Returns the mentions in the text if it has any.
     *
     * @return String[]
     */
    public ArrayList<String> getMentions() {
        return this.mentions;
    }

    /**
     * Returns the hashtags in the text if it has any.
     *
     * @return
     */
    public ArrayList<String> getHashtags() {
        return this.hashtags;
    }

    /**
     * Returns the links in the text if it has any.
     *
     * @return
     */
    public ArrayList<String> getLinks() {
        return this.links;
    }

    /**
     * Returns the original text of the tweet.
     *
     * @return String
     */
    public String getRawText() {
        return this.rawText;
    }

    /**
     * Returns the timestamp of the Tweet.
     *
     * @return String
     */
    public String getTimestamp() {
        return this.timeStamp;
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
     * Returns the line number of the Tweet in its containing file.
     *
     * @return
     */
    public int getTweetIndex() {
        return this.tweetIndex;
    }
    
    /**
     * Returns the list of filtered words and tags.
     * @return HashMap
     */
    public HashMap<String, String> getWords() {
        return this.wordTags;
    }

    @Override
    public String toString() {
        String words = "";
        
        for(Object w : wordTags.keySet()) {
            words += w.toString() + " ";
        }
        
        words += this.containedFile;
        return words;
    }
}
