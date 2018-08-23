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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to sort documents using tf-idf weightings.
 *
 * @author Matt
 */
class DocumentComparator implements Comparator {

    private String[] queryTerms;

    /**
     * Constructor
     *
     * @param queryTerms
     */
    public DocumentComparator(String[] queryTerms) {
        this.queryTerms = queryTerms;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof String && o2 instanceof String) {
            String doc1 = (String) o1, doc2 = (String) o2;
            
            double score1 = 0, score2 = 0;
            
            for (String term : queryTerms) {
                score1 += getTfIdf(doc1, term);
                score2 += getTfIdf(doc2, term);
            }
            
            if(score1 == score2) {
                System.out.println("docs equal");
                return 0;
            } else if (score1 > score2) {
                System.out.println("doc1 more");
                return 1;
            } else {
                System.out.println("doc2 more");
                return -1;
            }
        }
        
        return -1;
    }
    
    /**
     * Returns the tf-idf score for a term in a document.
     * @param doc
     * @param term
     * @return double
     */
    private double getTfIdf(String doc, String term) {
        try {
            File f = new File(ThreadedDataLoader.filePathStore.get(Integer.parseInt(doc)));
            Scanner scFile = new Scanner(f);
            Tweet t = new Tweet("", 0, scFile.nextLine());
            t.filter();
            
            int tf = 0;
            
            for (String word : t.getWords()) {
                if (word.equalsIgnoreCase(term)) {
                    tf++;
                }
            }
            
            double idf = Math.log(((double) ThreadedDataLoader.COLLECTION_SIZE) / (double)tf);
            return tf * idf;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocumentComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}

/**
 * Class used to retrieve Tweets from the index.
 *
 * @author Matt
 */
public class SearchIndex {

    public static HashMap<Integer, Tweet> tweetHash = new HashMap();

    /**
     * Returns all Tweets containing a specific term.
     *
     * @param terms
     * @return ArrayList
     * @throws java.io.IOException
     */
    public static Collection<String> search(String terms) throws IOException {
        ArrayList<Collection<String>> results = new ArrayList();
        String[] termArr = terms.split(" ");
        ArrayList<String> documents = documents(termArr);
        rankDocuments(documents, termArr);

        if (!documents.isEmpty()) {
            System.out.println(documents.size() + " documents found.");

            if (termArr.length > 1) {
                for (String document : documents) {
                    Collection<String> result = new TreeSet<>();
                    String doc = ThreadedDataLoader.filePathStore.get(Integer.parseInt(document));

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
                    String doc = ThreadedDataLoader.filePathStore.get(Integer.parseInt(document));

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
    private static ArrayList<String> documents(String[] termsArr) {
        ArrayList<String> docs = new ArrayList();

        for (String term : termsArr) {
            System.out.println("Checking " + Configuration.INDEX_SAVE_DIRECTORY + term.charAt(0) + "/" + term + ".index");
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
        }

        return docs;
    }

    /**
     * Updates the term frequencies for each document.
     *
     * @param finalMap
     * @param key
     */
    private static void rankDocuments(ArrayList<String> documents, String[] query) {
        String[] docs = new String[documents.size()];
        documents.toArray(docs);
        Arrays.sort(docs, new DocumentComparator(query));
    }
}
