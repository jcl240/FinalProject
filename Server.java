package project4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import project4.HotelSearch.HotelMapper;
import project4.HotelSearch.HotelReducer;

public class Server 
{
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	public void setServerSocket(ServerSocket serverSocket) 
	{
		this.serverSocket=serverSocket;
	}
	
	public ServerSocket getServerSocket() 
	{
		return this.serverSocket;
	}
	
	public void setClientSocket(Socket clientSocket)
	{
		this.clientSocket=clientSocket;
	}

	public Socket getClientSocket() 
	{
		return this.clientSocket;
	}
	
	public void runMapReduce(Socket socket) throws IOException, ClassNotFoundException
	{	
		String name=null;
		String location=null;
		
		BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String getNameLocation[]=br.readLine().split("\\s+");
		name=getNameLocation[0];
		location=getNameLocation[1];
		
		HotelSearch.HotelMapper.setName(name);
		HotelSearch.HotelMapper.setLocation(location);
		JobConf conf=new JobConf(HotelSearch.class);
		Job job=null;
		
		try 
		{
			job=Job.getInstance(conf, "HotelSearch");
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		job.setJarByClass(HotelSearch.class);
		job.setMapperClass(HotelMapper.class);
		job.setCombinerClass(HotelReducer.class);
		job.setReducerClass(HotelReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		try 
		{
			FileInputFormat.addInputPath(job, new Path("/home/ubuntu/HotelApp"));
			//Input path will be the text file that we will store in the cloud
		} 
		
		catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		FileOutputFormat.setOutputPath(job, new Path("/home/ubuntu/HotelApp/output-hotels"));
		
		try 
		{
			System.exit(job.waitForCompletion(true)? 0 : 1);
		} 
		
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		OutputFile(socket.getOutputStream());
	}
	
	public static void OutputFile(OutputStream out)
	{
		Path path=new Path("/home/ubuntu/HotelApp/output-hotelss");
		Configuration conf=new Configuration();
		FileSystem fs=null;
		
		try 
		{
			fs=FileSystem.get(conf);
		} 
		
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(path)));
			String hotels=null;
			hotels=br.readLine();
			DataOutputStream output=new DataOutputStream(out);
			while(hotels!=null)
			{
				output.write(hotels.getBytes());
				output.flush();
			}
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void runServer() throws ClassNotFoundException, IOException
	{
		try 
		{
			this.setServerSocket(new ServerSocket(7777));
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		while(true)
		{
			try 
			{
				this.setClientSocket(getServerSocket().accept());
			} 
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			new HandleConnection(getClientSocket()).start();
		}
	}
	
	public class HandleConnection extends Thread//Resets connection for some reason
	{
		private Socket connectionSocket;
		
		HandleConnection(Socket connectionSocket)
		{
			this.setConnectionSocket(connectionSocket);
		}
		
		public void setConnectionSocket(Socket connectionSocket) 
		{
			this.connectionSocket=connectionSocket;
		}
		
		public Socket getConnectionSocket() 
		{
			return connectionSocket;
		}
		
		public void run()
		{
			try 
			{
				runMapReduce(this.getConnectionSocket());
			} 
			
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			} 
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)throws Exception
	{
		Server server=new Server();
		server.runServer();
	}
}