package server.bus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Callable;

import server.app.function.Communicator;
import server.app.function.Log;
import server.app.function.Reservation;
import server.app.function.SenderToBus;

public class BusChild implements Callable<Void> {
	private HashMap<Integer, BusConnection> busList = null; // Server Database
	
	private Socket bus_socket = null; // 버스와 연결된 소켓
	
	
	private boolean END = false; // 서비스가 끝났는 지 기록
	private BusConnection BC = null;
	
	// 자식 스레드 생성자 : 초기화 역할
	public BusChild(Socket connection, HashMap<Integer, BusConnection> busList) {
		this.bus_socket = connection;
		this.busList = busList;
	}
		
	// 콜 메소드. 풀에 등록되어 자동으로 호출되고 return하면 종료됨.
	@Override
	public Void call() throws Exception {
		BC = new BusConnection(bus_socket);
		
		String msg = null;
		try {
			msg = BC.getReader().readLine();
		}
		catch (IOException e) {
		}
		if (msg == null) {
			Log.err("버스", "CO", bus_socket, "초기 메시지 수신 실패");
			return null;
		}
		
		int bus_id;
		try {
			bus_id = Integer.parseInt(msg);
		}
		catch (NumberFormatException e) {
			Log.err("버스", "IN", bus_socket, "잘못된 아이디 형식");
			return null;
		}
		
		busList.put(bus_id, BC);
		Log.out("버스", "CO", bus_socket, "BusID:" + bus_id + " 연결 성공");
		return null;
	}
}