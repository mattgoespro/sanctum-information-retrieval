/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HadoopMain {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

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
            String[] words = key.toString().split(" ");
            DIRECTORY.set(words[words.length - 1]);
            
            for (int i = 0; i < words.length - 1; i++) {
                word.set(words[i]);
                context.write(word, DIRECTORY);
            }
        }
    }

    public static class PathReducer extends Reducer<Text, Text, Text, Text> {

        private final Text result = new Text();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String path = "";
            
            for (Text val : values) {
                path += val.toString() + "; ";
            }
            
            result.set(path);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word paths");
        job.setJarByClass(HadoopMain.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(PathReducer.class);
        job.setReducerClass(PathReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path("/sanctum"));
        FileOutputFormat.setOutputPath(job, new Path("/sanctum/output"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
