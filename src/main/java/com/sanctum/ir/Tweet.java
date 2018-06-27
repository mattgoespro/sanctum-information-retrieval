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

import java.util.HashMap;

/**
 * Tweet class represents the raw tweet data and its tagged parts.
 * @author Matt
 */
public class Tweet {
    
    private final String containedFile;
    private final int tweetIndex;
    private final String rawText;
    private String[] mentions;
    private String[] hashtags;
    private final HashMap<String, String> wordTags;
    
    /**
     * Constructor
     * @param containedFile
     * @param tweetIndex
     * @param rawText 
     */
    public Tweet(String containedFile, int tweetIndex, String rawText) {
        this.containedFile = containedFile;
        this.tweetIndex = tweetIndex;
        this.rawText = rawText;
        this.wordTags = new HashMap();
        tagText();
    }
    
    /**
     * Tags the text of the tweet with its parts of speech.
     */
    private void tagText() {
        String[] words = rawText.split(" ");
        String[] tags = TweetTagger.POS_TAGGER.tag(words);
        
        for (int i = 0; i < words.length; i++) {
            this.wordTags.put(words[i], tags[i]);
        }
    }
    
    /**
     * Returns the mentions in the text if it has any.
     * @return String[]
     */
    public String[] getMentions() {
        return this.mentions;
    }
    
    /**
     * Returns the hashtags in the text if it has any.
     * @return 
     */
    public String[] getHashtags() {
        return this.hashtags;
    }
    
    /**
     * Returns the original text of the tweet.
     * @return String
     */
    public String getRawText() {
        return this.rawText;
    }
    
    /**
     * Returns the directory of the file containing this Tweet.
     * @return Strings
     */
    public String getContainingFileName() {
        return this.containedFile;
    }
    
    /**
     * Returns the line number of the Tweet in its containing file.
     * @return 
     */
    public int getTweetIndex() {
        return this.tweetIndex;
    }
}
