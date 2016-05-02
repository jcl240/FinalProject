package project4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.google.common.base.Charsets;

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
	
	public void runMapReduce(Socket querySocket) throws IOException, ClassNotFoundException
	{
		//Test code please help guys!
		String name=null;
		String location=null;
		StringWriter writer=new StringWriter();
		Scanner choice=new Scanner(System.in);
		System.out.println("Plase enter the name");
		name=choice.next();
		System.out.println("Please enter the location");
		location=choice.next();
		querySocket.getOutputStream().write(name.getBytes());
		querySocket.getOutputStream().write(location.getBytes());
		querySocket.getOutputStream().flush();
		name=IOUtils.toString(querySocket.getInputStream(), Charsets.UTF_8);
			
		//SetName and location will be the search queries from the client's socket outputstream ot the server
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
			FileInputFormat.addInputPath(job, new Path("C:/Users/Rachid/Desktop/hotel-data-set-small.txt"));
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
		
		FileOutputFormat.setOutputPath(job, new Path(querySocket.getInputStream().toString()));
		//The results will go to the client's socket input stream where the info will go to the appropriate B+ trees
		
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
	}
	
	public void runServer()
	{
		try 
		{
			this.setServerSocket(new ServerSocket(9990));
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
	
	public class HandleConnection extends Thread
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
		//server.runServer();
		Socket mySocket=new Socket(InetAddress.getByName("Rachid-PC"), 135);
		server.runMapReduce(mySocket);
		mySocket.close();
	}
}
