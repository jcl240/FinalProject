package project4;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class HotelSearch 
{
	public static class HotelMapper extends Mapper<Object, Text, Text, IntWritable>
	{
		private static final IntWritable ONE=new IntWritable(1);
		private final Text hotels=new Text();
		private static String name;
		private static String location;
		
		public static void setName(String hotelName) 
		{
			name=hotelName;
		}

		public static void setLocation(String hotelLocation) 
		{
			location=hotelLocation;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getLocation()
		{
			return location;
		}
		
		public void map(Object key, Text value, Context context)throws IOException, InterruptedException
		{
			StringTokenizer hotelTokenizer=new StringTokenizer(value.toString());
			while(hotelTokenizer.hasMoreTokens())
			{
				if(getName()==null&&getLocation()!=null)
				{
					if(value.toString().contains(getLocation()))
					{
						hotels.set(hotelTokenizer.nextToken());
						context.write(hotels, ONE);
					}
					
					else
					{
						hotels.set(hotelTokenizer.nextToken());
					}
				}
				
				else if(getName()!=null&&getLocation()==null)
				{
					if(value.toString().contains(getName()))
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
					if(value.toString().contains(getName())&&value.toString().contains(getLocation()))
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
}