package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BusArduino {
	static int PORT = 7770;

	public static void main(String[] args) {
		ServerSocket busServer = null;
		Socket connection = null;
		BufferedWriter output = null;
		BufferedReader intput = null;
		
		try {
			busServer = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("서버 생성 오류");
		}

		System.out.println(
				"\n[" + busServer.getInetAddress().getHostAddress() + ":" + busServer.getLocalPort() + "] 버스 서버 열림");

		try {
			connection = busServer.accept();
			output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			intput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
		}
		
		String msg = null;
		while (true) {
			try {
				msg = intput.readLine(); // 메시지 수신
				System.out.println(msg);
			} catch (IOException e1) {
				System.err.println("메시지 수신 실패");
			}
		}
	}
}
