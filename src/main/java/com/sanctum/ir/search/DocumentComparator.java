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
package com.sanctum.ir.search;

import com.sanctum.ir.TagFilter;
import com.sanctum.ir.ThreadedDataLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;

/**
 * Class used to sort documents using tf-idf weightings.
 *
 * @author Matt
 */
public class DocumentComparator implements Comparator {

    private final String[] queryTerms;
    private final FileSystem fs;
    private TagFilter filter;

    /**
     * Constructor
     *
     * @param fs
     * @param queryTerms
     */
    public DocumentComparator(FileSystem fs, String[] queryTerms) {
        this.fs = fs;
        this.queryTerms = queryTerms;
        this.filter = new TagFilter();

        try {
            filter.loadBlacklist(fs);
        } catch (IOException ex) {
            Logger.getLogger(DocumentComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 != null && o2 != null) {
            if (o1 instanceof String && o2 instanceof String) {
                String doc1 = (String) o1, doc2 = (String) o2;

                if (StringUtils.isNumeric(doc1) && StringUtils.isNumeric(doc2)) {
                    double score1 = 0, score2 = 0;

                    for (String term : queryTerms) {
                        try {
                            score1 += getTfIdf(term);
                            score2 += getTfIdf(term);
                        } catch (IOException ex) {
                            System.out.println("Unable to read tf-idf rank for one of the required documents.");
                        }
                    }

                    if (score1 == score2) {
                        return 0;
                    } else if (score1 > score2) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Returns the tf-idf score for a term in a document.
     *
     * @param doc
     * @param term
     * @return double
     */
    private double getTfIdf(String term) throws IOException {
        String path;

        if (fs == null) {
            if (term.startsWith("hashtag_")) {
                path = "index/hashtags/" + term + ".index";
            } else if (term.startsWith("mention_")) {
                path = "index/mentions/" + term + ".index";
            } else {
                path = "index/" + term.charAt(0) + "/" + term + ".index";
            }
        } else {
            if (term.startsWith("hashtag_")) {
                path = "sanctum/index/hashtags/" + term + ".index";
            } else if (term.startsWith("mention_")) {
                path = "sanctum/index/mentions/" + term + ".index";
            } else {
                path = "sanctum/index/" + term.charAt(0) + "/" + term + ".index";
            }
        }

        BufferedReader reader = SearchIndex.getReader(fs, path);
        int tf = 0;

        String line = reader.readLine();
        while (line != null) {
            tf++;
            line = reader.readLine();
        }

        assert (tf != 0);
        reader.close();

        double idf = Math.log(((double) ThreadedDataLoader.COLLECTION_SIZE) / (double) tf);
        return tf * idf;
    }
}
