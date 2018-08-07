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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Class that is used to filter and process tags.
 *
 * @author Matt
 */
public class TagFilter {

    private ArrayList<String> tagValueBlacklist;
    private ArrayList<String> tagPosBlacklist;
    private boolean inclMentions, inclHashtags, inclLinks;
    private final List<String> punctuation = Arrays.asList("!", "[", "]", "{", "}", ".", ",", "?", "/", "\\", "|", "(", ")", "@", "#", "$", "%", "^", "&", "*", "_", "-", "+", "=", "`", "~", "<", ">", ":", ";", "'", "\"");
    
    /**
     * Constructor
     */
    public TagFilter() {
        this.inclMentions = Boolean.parseBoolean(Configuration.INDEXING_INCLUDE_MENTIONS);
        this.inclHashtags = Boolean.parseBoolean(Configuration.INDEXING_INCLUDE_HASHTAGS);
        this.inclLinks = Boolean.parseBoolean(Configuration.INDEXING_INCLUDE_LINKS);
    }

    /**
     * Loads the list of words from a file to exclude from indexing.
     *
     * @param blacklistFile
     * @throws FileNotFoundException
     */
    public void loadBlacklist(String blacklistFile) throws FileNotFoundException {
        this.tagValueBlacklist = new ArrayList();
        this.tagPosBlacklist = new ArrayList();
        Scanner scFile = new Scanner(new File(blacklistFile));
        String line;

        while (scFile.hasNext()) {
            line = scFile.nextLine();

            if (!line.startsWith("#")) {
                if (!line.startsWith("!")) {
                    this.tagValueBlacklist.add(line);
                } else {
                    this.tagPosBlacklist.add(line.substring(1));
                }
            }
        }
        scFile.close();
    }

    /**
     * Choose whether to include mentions in the indexing.
     *
     * @param inclMentions
     */
    public void includeMentions(boolean inclMentions) {
        this.inclMentions = inclMentions;
    }
    
    /**
     * Returns whether the filter should include mentions in indexing.
     * @return boolean
     */
    public boolean includesMentions() {
        return this.inclMentions;
    }

    /**
     * Choose whether to include hashtags in the indexing.
     *
     * @param inclHashtags
     */
    public void includeHashtags(boolean inclHashtags) {
        this.inclHashtags = inclHashtags;
    }
    
    /**
     * Returns whether the filter should include hashtags in indexing.
     * @return boolean
     */
    public boolean includesHashtags() {
        return this.inclHashtags;
    }

    /**
     * Choose whether to include links in the indexing.
     *
     * @param inclLinks
     */
    public void includeLinks(boolean inclLinks) {
        this.inclLinks = inclLinks;
    }
    
    /**
     * Returns whether the filter should include links in indexing.
     * @return boolean
     */
    public boolean includesLinks() {
        return this.inclLinks;
    }
    
    /**
     * Filters the text for indexing.
     * @param words
     * @param tags
     * @return HashMap<String, String>
     */
    public HashMap<String, String> filterText(String[] words, String[] tags) {
        HashMap<String, String> wordTags = new HashMap();

        // store word and tags in hashmap
        for (int i = 0; i < words.length; i++) {
            // ignore words with a specific part-of-speech
            if(this.tagPosBlacklist.contains(tags[i])) continue;
            
            // ignore specific words
            if(this.tagValueBlacklist.contains(words[i])) continue;
            
            // ignore punctuation marks
            if(this.punctuation.contains(words[i])) continue;
            
            if ((words[i].startsWith("#") && this.inclHashtags) || (words[i].startsWith("@") && this.inclMentions) || (words[i].startsWith("http://") && this.inclLinks)) {
                wordTags.put(words[i], tags[i]);
            } else {
                wordTags.put(words[i],tags[i]);
            }
        }

        return wordTags;
    }
}
