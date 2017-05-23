package test;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.*;
import java.lang.Thread;     //스레드를 사용한다.
import java.lang.InterruptedException;     // implement this


public class Server {
	
	public static void main(String []args) {
		 
		try {
			ServerSocket server = new ServerSocket(9446);   // 서버소켓을 생성하는데 포튼넘버는 1234다.
			int id = 0;
			System.out.println("서버 시작");
			while(true) {
		    
				Socket client = server.accept();     // 클라이언트를 기다린다.
				System.out.println("Spawning client " + id);   //클라이언트가 접속한 것을 화면에 출력한다.
				//접속한 클라이언트에 대해 스레드를 생성하는데, EchoServerHandler 클래스 객체에 대해 생성한다.
				Thread clientThread = new Thread(new EchoServerHandler(client, id));   
				clientThread.start();    //위에서 생성한 스레드를 시작한다.
				id ++; 
			}
		  
		}
		  
		catch(IOException e) {
			e.printStackTrace();
		  	}
	}
}


class EchoServerHandler extends Thread {
	 private int id;
	 private Socket client;
	 
	 public EchoServerHandler(Socket socket, int i) {
	  client = socket;
	  id = i;
	 }
	 
	/* 스레드가 start()되면 run()함수를 실행시키게 되는데
	    클라이언트에게 스트림을 전송 할 스트림 핸들러를 생성한 뒤,
	    0~9999를 0.1초마다 클라이언트에게 전송한다. */
	 public void run() {
		  try {
			  System.out.println(client.getLocalSocketAddress());
			  System.out.println(client.getInetAddress());
			  System.out.println(client.getLocalPort());
			  String str;
			  PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
			  BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			   
			  int count = 0;
			  while (true) {
				  
				  pw.println("hello world");
		
				  this.sleep(10000);
				  if(count>10){
					  break;
				  }
				  count++;
			  }
			  pw.close();
			  br.close();
				    
				    
				    /* 받을떄
					receive = client.getInputStream();
					BufferedInputStream inputS = new BufferedInputStream(receive);
					byte[] contents = new byte[1024];
					
					 int bytesRead=0;
			         String strFileContents;
			        
			         while( (bytesRead = inputS.read(contents)) != -1){
			                
			                 strFileContents = new String(contents, 0, bytesRead);
			                 System.out.print(strFileContents);
			         }
			         
					System.out.println(inputS);
					*/
			  
				    
				
		  
		  }
		  catch(IOException e) {
		   e.printStackTrace();
		  }
		  catch(InterruptedException ie) {
		   ie.printStackTrace();
		  }
	 }
}

	