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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.fs.FSDataInputStream;
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
        ArrayList<Collection<String>> results = new ArrayList();
        ArrayList<String> documents = documents(fs, termArr);
        rankDocuments(fs, documents, termArr);

        if (!documents.isEmpty()) {
            System.out.println(documents.size() + " documents found.");

            if (termArr.length > 1) {
                for (String document : documents) {
                    Collection<String> result = new TreeSet<>();
                    String doc = ThreadedDataLoader.pathStore.get(document);

                    try (Scanner tweetDocScanner = new Scanner(new File(doc))) {
                        result.add(tweetDocScanner.nextLine());
                    }

                    results.add(result);
                }

                for (int i = 1; i < results.size(); i++) {
                    results.get(0).retainAll(results.get(i));
                }

                return results.get(0);
            } else if (termArr.length == 1) {
                Collection<String> result = new TreeSet<>();

                for (String document : documents) {
                    String doc = ThreadedDataLoader.pathStore.get(document);

                    try (Scanner tweetDocScanner = new Scanner(new File(doc))) {
                        result.add(tweetDocScanner.nextLine());
                    }
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
            System.out.println("Checking " + Configuration.INDEX_SAVE_DIRECTORY + term.charAt(0) + "/" + term + ".index");

            if (fs == null) {
                File ind = new File(Configuration.INDEX_SAVE_DIRECTORY + term.charAt(0) + "/" + term + ".index");

                try {
                    try (Scanner sc = new Scanner(ind)) {
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();

                            if (!line.equals("")) {
                                docs.add(line);
                            }
                        }
                    }
                } catch (FileNotFoundException ex) {
                }
            } else {
                try {
                    FSDataInputStream indexStream = fs.open(new Path("/sanctum/index/" + term.charAt(0) + "/" + term + ".index"));
                    Scanner sc = new Scanner(indexStream);

                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();

                        if (!line.equals("")) {
                            docs.add(line);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SearchIndex.class.getName()).log(Level.SEVERE, null, ex);
                }
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
}
