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

import com.sanctum.drivers.DocumentComparator;
import com.sanctum.drivers.Search;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Class used to retrieve Tweets from the index.
 *
 * @author Matt
 */
public class SearchIndex {

    /**
     * Returns all Tweets containing a specific term.
     *
     * @param fs
     * @param termArr
     * @return ArrayList
     * @throws java.io.IOException
     */
    public static Collection<String> search(FileSystem fs, String[] termArr) throws IOException {
        if (termArr.length == 0) {
            return null;
        }

        ArrayList<Collection<String>> results = new ArrayList();
        ArrayList<String> documents = documents(fs, termArr);
        
        if(documents == null) {
            return null;
        }
        
        if(documents.size() > 1) {
            rankDocuments(fs, documents, termArr);
        }
        
        BufferedReader tweetDocScanner;

        if (!documents.isEmpty()) {
            System.out.println(documents.size() + " documents found.");

            if (termArr.length > 1) {
                for (String docID : documents) {
                    Collection<String> result = new TreeSet<>();
                    String doc = getDocWithID(fs, docID);
                    tweetDocScanner = getReader(fs, doc);
                    result.add(tweetDocScanner.readLine());
                    results.add(result);
                }

                for (int i = 1; i < results.size(); i++) {
                    results.get(0).retainAll(results.get(i));
                }

                return results.get(0);
            } else if (termArr.length == 1) {
                Collection<String> result = new TreeSet<>();

                for (String docID : documents) {
                    String doc = getDocWithID(fs, docID);
                    tweetDocScanner = getReader(fs, doc);
                    result.add(tweetDocScanner.readLine());
                }
                return result;
            }
        }
        return null;
    }

    /**
     * Returns a list of documents for a term as a string.
     *
     * @param terms
     * @return String
     */
    private static ArrayList<String> documents(FileSystem fs, String[] termsArr) {
        ArrayList<String> docs = new ArrayList();

        for (String term : termsArr) {
            if(term == null) continue;
            
            BufferedReader reader;
            
            try {
                if (fs == null) {
                    reader = getReader(fs, "index/" + term.charAt(0) + "/" + term + ".index");
                } else {
                    reader = getReader(fs, "sanctum/index/" + term + "-m-00000");
                }
            } catch (IOException ex) {
                return null;
            }

            String line;
            
            try {
                line = reader.readLine();

                while (line != null) {
                    if (StringUtils.isNotBlank(line) && StringUtils.isNotEmpty(line)) {
                        docs.add(line);
                    }
                    
                    line = reader.readLine();
                }
            } catch (IOException ex) {
                System.out.println("Something went wrong while trying to read the required file.");
                return null;
            }
        }

        return docs;
    }

    /**
     * Updates the term frequencies for each document.
     *
     * @param finalMap
     * @param key
     */
    private static void rankDocuments(FileSystem fs, ArrayList<String> documents, String[] query) {
        String[] docs = new String[documents.size()];
        documents.toArray(docs);
        Arrays.sort(docs, new DocumentComparator(fs, query));
    }

    /**
     * Returns the document with a specific ID from the specified filesystem.
     * The filesystem object determines which class to fetch the document from.
     *
     * @param fs
     * @param docID
     * @return String
     */
    public static String getDocWithID(FileSystem fs, String docID) {
        return fs == null ? DataLoader.pathStore.get(docID) : Search.pathStore.get(docID);
    }

    /**
     * Returns a reader for a document from the specified filesystem. The
     * filesystem object determines how the reader is created.
     *
     * @param fs
     * @param doc
     * @return BufferedReader
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static BufferedReader getReader(FileSystem fs, String doc) throws FileNotFoundException, IOException {
        return fs == null ? new BufferedReader(new FileReader(new File(doc))) : new BufferedReader(new InputStreamReader(fs.open(new Path(doc))));
    }
}
