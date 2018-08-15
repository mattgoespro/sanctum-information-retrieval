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
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Class that is used to filter and process tags.
 *
 * @author Matt
 */
public class TagFilter {

    private ArrayList<String> tagValueBlacklist;
    private boolean inclMentions, inclHashtags, inclLinks;

    /**
     * Constructor
     */
    public TagFilter() {
        this.inclMentions = Boolean.parseBoolean(com.sanctum.ir.Configuration.INDEXING_INCLUDE_MENTIONS);
        this.inclHashtags = Boolean.parseBoolean(com.sanctum.ir.Configuration.INDEXING_INCLUDE_HASHTAGS);
        this.inclLinks = Boolean.parseBoolean(com.sanctum.ir.Configuration.INDEXING_INCLUDE_LINKS);
        this.tagValueBlacklist = new ArrayList();
    }

    /**
     * Loads the list of words from a file to exclude from indexing.
     *
     * @param fileSystemRoot
     * @throws FileNotFoundException
     */
    public void loadBlacklist(String fileSystemRoot) throws FileNotFoundException, IOException {
        System.out.println(fileSystemRoot);
        if (!fileSystemRoot.startsWith("hdfs://")) {
            Scanner scFile = new Scanner(new File("indexing_token_blacklist.cfg"));
            String line;

            while (scFile.hasNext()) {
                line = scFile.nextLine();

                if (!line.startsWith("#")) {
                    this.tagValueBlacklist.add(line);
                }
            }
            scFile.close();
        } else {
            String uri = fileSystemRoot + "/sanctum/indexing_token_blacklist.cfg";
            FileSystem sys = FileSystem.get(URI.create(uri), new Configuration());
            FSDataInputStream fs = sys.open(new Path(uri));
            LineIterator lineIterator = IOUtils.lineIterator(fs, "UTF-8");
            String line;
            while(lineIterator.hasNext()) {
                line = lineIterator.nextLine();

                if (!line.startsWith("#")) {
                    this.tagValueBlacklist.add(line);
                }
            }
        }
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
     *
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
     *
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
     *
     * @return boolean
     */
    public boolean includesLinks() {
        return this.inclLinks;
    }

    /**
     * Filters the text for indexing.
     *
     * @param words
     */
    public void filterText(ArrayList<String> words) {
        // store word and tags in hashmap
        Iterator it = words.iterator();

        while (it.hasNext()) {
            String w = (String) it.next();

            if(w != null) {
                if ((w.startsWith("#") && !this.inclHashtags) || (w.startsWith("@") && !this.inclMentions) || (w.startsWith("http://") && !this.inclLinks)) {
                    it.remove();
                } else if (this.tagValueBlacklist.contains(w)) {
                    it.remove();
                }
            }
        }
    }
}
