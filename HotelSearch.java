package project4;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HotelSearch 
{
	public static class HotelMapper extends Mapper<Object, Text, Text, IntWritable>
	{
		private static final IntWritable ONE=new IntWritable(1);
		private Text hotels=new Text();
		private String name="Marriot";
		private String location="NewYork";
		
		public void map(Object key, Text value, Context context)throws IOException, InterruptedException
		{
			StringTokenizer hotelTokenizer=new StringTokenizer(value.toString());
			while(hotelTokenizer.hasMoreTokens())
			{
				if(this.name==null&&this.location!=null)
				{
					if(value.toString().contains(location))
					{
						hotels.set(hotelTokenizer.nextToken());
						context.write(hotels, ONE);
					}
					
					else
					{
						hotels.set(hotelTokenizer.nextToken());
					}
				}
				
				else if(this.name!=null&&this.location==null)
				{
					if(value.toString().contains(name))
					{
						hotels.set(hotelTokenizer.nextToken());
						context.write(hotels, ONE);
					}
					
					else
					{
						hotels.set(hotelTokenizer.nextToken());
					}
				}
				
				else
				{
					if(value.toString().contains(name)||value.toString().contains(location))
					{
						hotels.set(hotelTokenizer.nextToken());
						context.write(hotels, ONE);
					}
					
					else
					{
						hotels.set(hotelTokenizer.nextToken());
					}
				}
			}
		}
	}
	
	public static class HotelReducer extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		public void reduce(Text key, Context context)throws IOException, InterruptedException
		{
			context.write(key, new IntWritable(1));	
		}
	}
	
	public static void main(String[] args)throws Exception
	{
		JobConf conf=new JobConf(HotelSearch.class);
		Job job=Job.getInstance(conf, "HotelSearch");
		job.setJarByClass(HotelSearch.class);
		job.setMapperClass(HotelMapper.class);
		job.setCombinerClass(HotelReducer.class);
		job.setReducerClass(HotelReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)? 0 : 1);
	}
}