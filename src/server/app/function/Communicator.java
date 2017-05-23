package server.app.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import database.DB;
import server.bus.BusConnection;
import server.bus.BusServer;

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
			Log.err("어플", "CO", app_socket, "스트림 생성 오류");
		}
	}
	
	/** 버스 아두이노와 연결 설정 */
	public boolean connectToBus(int bus_id) {
		/*
		String address = db.getBusAddress(bus_id);
		if (address == null) {
			Log.err("어플", "IN", app_socket, "없는 버스 아이디 (입력된 아이디 = " + bus_id + ")");
			return false;
		}
		String[] BusAddress = address.split(":"); // 0: IP, 1: PORT
		String Bus_IP = BusAddress[0];
		int Bus_PORT = Integer.parseInt(BusAddress[1]);
		
		try {
			bus_socket = new Socket(Bus_IP, Bus_PORT);
			try {
				bus_output = new BufferedWriter(new OutputStreamWriter(bus_socket.getOutputStream()));
				bus_intput = new BufferedReader(new InputStreamReader(bus_socket.getInputStream()));
			} catch (IOException e) {
				Log.err("버스", "CO", bus_socket, "스트림 생성 오류");
			}
			Log.out("버스", "CO", bus_socket, "버스 아두이노와 연결 성공");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
		*/
		BusConnection BC = BusServer.findBus(bus_id);
		if (BC == null)
			return false;
		bus_socket = BC.getSocket();
		bus_output = BC.getWriter();
		bus_intput = BC.getReader();
		return true;
	}
	
	public boolean sendToApp(String msg) {
		try {
			app_output.write(msg);
			app_output.newLine();
			app_output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.err("어플", "OUT", app_socket, "메시지 전송 실패");
			return false;
		}
		return true;
	}

	public boolean sendToApp(int msg) {
		return sendToApp(Integer.toString(msg));
	}


	public String recvFromApp() {
		String msg = null;
		
		try {
			msg = app_intput.readLine(); // 메시지 수신
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.err("어플", "IN", app_socket, "메시지 수신 실패");
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
			Log.err("버스", "OUT", bus_socket, "메시지 전송 실패");
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
			Log.err("버스", "IN", bus_socket, "메시지 수신 실패");
		}
		return msg;
	}

	/** 소켓 연결 해제 */
	public void endConnect() throws IOException {
		// 어플과의 연결 종료
		app_output.close();
		app_intput.close();
		app_socket.close();
	}
}
