package test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class Client {
	private Socket connection = null; // 서버와 연결된 소켓
	BufferedWriter output_stream = null; // 출력 버퍼
	BufferedReader input_stream = null; // 입력 버퍼
	
	public Client() {
		try {
			connection = new Socket("localhost", 9446);
			connection.setSoTimeout(15000);
			try {
				output_stream = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				input_stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} catch (IOException e) {
				System.out.println("[스트림 생성 오류 : "+new String(connection.getInetAddress().getAddress())+"]");
			}
			System.out.println("서버와 연결 성공");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dodo();
	}
	public void sendMsg(String msg) {
		try {
			output_stream.write(msg);
			output_stream.newLine();
			output_stream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("메시지 송신 실패");
			e.printStackTrace();
		}
	}
	
	public String receiveMsg() {
		String msg = null;
		
		try {
			msg = input_stream.readLine(); // 메시지 수신
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("메시지 수신 실패");
		}
		return msg;
	}
	
	
	public void dodo() {
		sendMsg("1");
		System.out.println("1보냄");
		//s.is
		String msg;
		msg = receiveMsg();
		System.out.println(msg);
		
		try {
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
	}
}
