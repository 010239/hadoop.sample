package kkk.hadoop.sample;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 *     hadoop jar WordCount.jar kkk.hadoop.sample.WordCount /input/wordcount/file1 /output/
 */
public class WordCount {
    /**
     * Object      : 输入文件的内容
     * Text        : 输入的每一行的数据
     * Text        : 输出的key的类型
     * IntWritable : 输出value的类型
     */
    private static class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                context.write(new Text(itr.nextToken()), new IntWritable(1));
            }
        }
    }

    /**
     * Text         :  Mapper输入的key
     * IntWritable  :  Mapper输入的value
     * Text         :  Reducer输出的key
     * IntWritable  :  Reducer输出的value
     */
    private static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable item : values) {
                count += item.get();
            }
            context.write(key, new IntWritable(count));
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        // 创建配置
        Configuration configuration = new Configuration();
        // 设置hadoop的作业  jobName是WordCount
        Job job = Job.getInstance(configuration, "WordCount");
        // 设置jar
        job.setJarByClass(WordCount.class);
        // 设置Mapper的class
        job.setMapperClass(WordCountMapper.class);
        // 设置Reducer的class
        job.setReducerClass(WordCountReducer.class);
        // 设置输出的key和value类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 设置输入输出路径
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // 待job执行完  程序退出
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
