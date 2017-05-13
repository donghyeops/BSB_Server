package server.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import database.DB;

public class Communicator {
	private DB db = null; // Server Database
	
	private Socket app_socket = null; // 어플리케이션과 연결된 소켓
	private BufferedWriter app_output = null; // 어플리케이션 출력 버퍼
	private BufferedReader app_intput = null; // 어플리케이션 입력 버퍼
	
	private Socket bus_socket = null; // 버스 아두이노와 연결된 소켓
	private BufferedWriter bus_output = null; // 버스 아두이노 출력 버퍼
	private BufferedReader bus_intput = null; // 버스 아두이노 입력 버퍼
	
	
	public Communicator(Socket connection, DB db) {
		this.app_socket = connection;
		this.db = db;
		try {
			app_output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			app_intput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			System.out.println("[스트림 생성 오류 : "+new String(connection.getInetAddress().getAddress())+"]");
		}
	}
	
	/** 버스 아두이노와 연결 설정 */
	public boolean connectToBus(int bus_ID) {
		String[] BusAddress = db.getBusAddress(bus_ID).split(":"); // 0: IP, 1: PORT
		String Bus_IP = BusAddress[0];
		int Bus_PORT = Integer.parseInt(BusAddress[1]);
		
		try {
			bus_socket = new Socket(Bus_IP, Bus_PORT);
			try {
				bus_output = new BufferedWriter(new OutputStreamWriter(bus_socket.getOutputStream()));
				bus_intput = new BufferedReader(new InputStreamReader(bus_socket.getInputStream()));
			} catch (IOException e) {
				System.out.println("[스트림 생성 오류 : "+new String(bus_socket.getInetAddress().getAddress())+"]");
			}
			System.out.println("버스 아두이노와 연결 성공 [" + BusAddress[0] + ":" + BusAddress[1] + "]");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendToApp(String msg) {
		try {
			app_output.write(msg);
			app_output.newLine();
			app_output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("메시지 송신 실패");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public String recvFromApp() {
		String msg = null;
		
		try {
			msg = app_intput.readLine(); // 메시지 수신
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("메시지 수신 실패");
		}
		return msg;
	}
	public boolean sendToBus(String msg) {
		try {
			bus_output.write(msg);
			bus_output.newLine();
			bus_output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("메시지 송신 실패");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean sendToBus(int msg) {
		return sendToBus(Integer.toString(msg));
	}
	public String recvFromBus() {
		String msg = null;
		
		try {
			msg = bus_intput.readLine(); // 메시지 수신
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("메시지 수신 실패");
		}
		return msg;
	}
}
