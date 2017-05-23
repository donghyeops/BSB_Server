package server.bus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import server.app.function.Log;

public class BusConnection {
	private Socket bus_socket = null; // 버스 아두이노와 연결된 소켓
	private BufferedWriter bus_output = null; // 버스 아두이노 출력 버퍼
	private BufferedReader bus_intput = null; // 버스 아두이노 입력 버퍼
	
	public BusConnection(Socket socket) {
		this.bus_socket = socket;

		try {
			bus_output = new BufferedWriter(new OutputStreamWriter(bus_socket.getOutputStream()));
			bus_intput = new BufferedReader(new InputStreamReader(bus_socket.getInputStream()));
			} catch (IOException e) {
			Log.err("버스", "CO", bus_socket, "스트림 생성 오류");
		}
	}
	
	public BufferedWriter getWriter() {
		return bus_output;
	}
	
	public BufferedReader getReader() {
		return bus_intput;
	}
	
	public Socket getSocket() {
		return bus_socket;
	}
	
	public void endConnection() {
		try {
			bus_output.close();
			bus_intput.close();
			bus_socket.close();
		} catch (IOException e) {
			Log.err("버스", "CO", bus_socket, "endConnection 오류");
		}
	}
}
