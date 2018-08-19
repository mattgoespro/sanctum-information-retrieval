/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HadoopSearch {

    public static class SearchMapper extends Mapper<Object, Text, Text, Text> {

        private static final Text DIRECTORY = new Text();
        private final Text word = new Text();

        /**
         *
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] words = value.toString().split("; ");
            Configuration conf = context.getConfiguration();

            for (int i = 0; i < context.getConfiguration().getInt("query length", 0); i++) {
                if (words[0].equalsIgnoreCase(conf.get("term " + i))) {
                    String val = "";
                    
                    for (int j = 0; j < words.length; j++) {
                        String uri = "/sanctum/data/" + words[j];
                        FileSystem sys = FileSystem.get(URI.create(uri), new Configuration());
                        FSDataInputStream fs = sys.open(new Path(uri));
                        LineIterator lineIterator = IOUtils.lineIterator(fs, "UTF-8");
                        String line;
                        
                        while (lineIterator.hasNext()) {
                            line = lineIterator.nextLine();

                            if (line.contains(words[0])) {
                                val += line + "; ";
                            }
                        }
                        lineIterator.close();
                    }
                    
                    word.set(val);
                    context.write(word, null);
                }
            }
        }
    }

    public static class SearchReducer extends Reducer<Text, Text, Text, Text> {

        private final Text result = new Text();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            result.set(key);
            context.write(result, null);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "query search");
        job.setJarByClass(HadoopMain.class);
        job.setMapperClass(SearchMapper.class);
        job.setCombinerClass(SearchReducer.class);
        job.setReducerClass(SearchReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path("/sanctum/output"));
        FileOutputFormat.setOutputPath(job, new Path("/sanctum/output/searchresults"));

        // set arguments in config
        conf.setInt("query length", args.length);
        for (int i = 0; i < args.length; i++) {
            conf.set("term " + i, args[i]);
        }

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
