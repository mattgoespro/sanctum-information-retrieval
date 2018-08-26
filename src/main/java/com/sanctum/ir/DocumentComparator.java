
import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.Tweet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Class used to sort documents using tf-idf weightings.
 *
 * @author Matt
 */
public class DocumentComparator implements Comparator {

    private final String[] queryTerms;
    private FileSystem fs;

    /**
     * Constructor
     *
     * @param queryTerms
     */
    public DocumentComparator(FileSystem fs, String[] queryTerms) {
        this.fs = fs;
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

            if (score1 == score2) {
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
     *
     * @param doc
     * @param term
     * @return double
     */
    private double getTfIdf(String doc, String term) {
        if (fs == null) {
            try {
                File f = new File(ThreadedDataLoader.pathStore.get(Integer.parseInt(doc)));
                Scanner scFile = new Scanner(f);
                Tweet t = new Tweet("", 0, scFile.nextLine());
                scFile.close();
                t.filter();

                int tf = 0;

                for (String word : t.getWords()) {
                    if (word.equalsIgnoreCase(term)) {
                        tf++;
                    }
                }

                double idf = Math.log(((double) ThreadedDataLoader.COLLECTION_SIZE) / (double) tf);
                return tf * idf;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DocumentComparator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                FSDataInputStream docStream = fs.open(new Path(HadoopSearch.pathStore.get(Integer.parseInt(doc))));
                LineIterator lineIterator = IOUtils.lineIterator(docStream, "UTF-8");
                Tweet t = new Tweet("", 0, lineIterator.nextLine());
                lineIterator.close();
                t.filter();

                int tf = 0;

                for (String word : t.getWords()) {
                    if (word.equalsIgnoreCase(term)) {
                        tf++;
                    }
                }

                double idf = Math.log(((double) ThreadedDataLoader.COLLECTION_SIZE) / (double) tf);
                return tf * idf;
            } catch (IOException ex) {
                Logger.getLogger(DocumentComparator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return 0;
    }
}