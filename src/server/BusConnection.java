package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import server.function.Log;

public class BusConnection {
	private int bus_id;
	private Socket bus_socket = null; // 버스 아두이노와 연결된 소켓
	private BufferedWriter bus_output = null; // 버스 아두이노 출력 버퍼
	
	public BusConnection(Socket socket) {
		this.bus_socket = socket;

		try {
			bus_output = new BufferedWriter(new OutputStreamWriter(bus_socket.getOutputStream()));
			} catch (IOException e) {
			Log.err("버스", "CO", bus_socket, "스트림 생성 오류");
		}
	}
	
	public BufferedWriter getWriter() {
		return bus_output;
	}
}
