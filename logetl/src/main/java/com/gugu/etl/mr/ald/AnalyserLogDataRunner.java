package com.gugu.etl.mr.ald;

import com.gugu.common.EventLogConstants;
import com.gugu.common.GlobalConstants;
import com.gugu.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapred.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.YARNRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author gugu
 * @Classname AnalyserLogDataRunner
 * @Description 编写mapreduce的runner类
 * @Date 2020/1/3 21:42
 */
public class AnalyserLogDataRunner implements Tool {
    private static final Logger logger = Logger
            .getLogger(AnalyserLogDataRunner.class);
    private Configuration conf = null;

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(),new AnalyserLogDataRunner(),args);
        } catch (Exception e) {
            logger.error("执行日志解析job异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        this.processArgs(conf,args);
        Job job = Job.getInstance(conf, "analyser_logdata");
        // 设置本地提交job，集群运行，需要代码
        // File jarFile = EJob.createTempJar("target/classes");
        // ((JobConf) job.getConfiguration()).setJar(jarFile.toString());
        // 设置本地提交job，集群运行，需要代码结束

        job.setJarByClass(AnalyserLogDataRunner.class);
        job.setMapperClass(AnalyserLogDataMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Put.class);
        // 设置reducer配置
        // 1. 集群上运行，打成jar运行(要求addDependencyJars参数为true，默认就是true)
        // TableMapReduceUtil.initTableReducerJob(EventLogConstants.HBASE_NAME_EVENT_LOGS,
        // null, job);
        // 2. 本地运行，要求参数addDependencyJars为false
        JobConf jobConf = new JobConf(conf, job.getClass());
        TableMapReduceUtil.initTableReduceJob(EventLogConstants.HBASE_NAME_EVENT_LOGS,null, jobConf);

        job.setNumReduceTasks(0);
        // 设置输入路径
        this.setJobInputPaths(job);
        return job.waitForCompletion(true) ? 0 : -1;
    }

    @Override
    public void setConf(Configuration conf) {
        conf.set("fs.defaultFS", "hdfs://master:50070");
        conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        this.conf = HBaseConfiguration.create(conf);
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }
    /**
     * @Description 处理参数
     * @params 
     * @param conf
     * @param args
     * @return void
     * @auther gugu
     */
    
    private void processArgs(Configuration conf,String[] args){
        String date = null;
        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i])) {
                if (i + 1 < args.length) {
                    date = args[++i];
                    break;
                }
            }
        }

        System.out.println("-----" + date);// 要求date格式为: yyyy-MM-dd
        if (StringUtils.isBlank(date) || !TimeUtil.isValidateRunningDate(date)) {
            // date是一个无效时间数据
            date = TimeUtil.getYesterday(); // 默认时间是昨天
            System.out.println(date);
        }
        conf.set(GlobalConstants.RUNNING_DATE_PARAMES, date);
    }
    /**
     * @Description 设置job的输入路径
     * @params
     * @param job
     * @return void
     * @auther gugu
     */

    private void setJobInputPaths(Job job){
        Configuration conf = job.getConfiguration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            String date = conf.get(GlobalConstants.RUNNING_DATE_PARAMES);
            // Path inputPath = new Path("/flume/" +
            // TimeUtil.parseLong2String(TimeUtil.parseString2Long(date),
            // "MM/dd/"));
            Path inputPath = new Path("/log/"
                    + TimeUtil.parseLong2String(
                    TimeUtil.parseString2Long(date), "yyyyMMdd")
                    + "/");

            if (fs.exists(inputPath)) {
                FileInputFormat.addInputPath(job, inputPath);
            } else {
                throw new RuntimeException("文件不存在:" + inputPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("设置job的mapreduce输入路径出现异常", e);
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // nothing
                }
            }
        }
    }
    
}
