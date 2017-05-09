package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

/**
 * 각 클라이언트와 1:1 연결을 이루는 서버의 자식 스레드
 */
// http://blog.naver.com/ksch2004/220540914491
public class ChildThread implements Callable<Void> {
	private Socket connection = null; // 클라이언트와 연결된 소켓
	private BufferedWriter output_stream = null; // 출력 버퍼
	private BufferedReader input_stream = null; // 입력 버퍼
	
	// 자식 스레드 초기화
	public ChildThread(Socket connection) {
		this.connection = connection;
		try {
			output_stream = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			input_stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			System.out.println("[스트림 생성 오류 : "+new String(connection.getInetAddress().getAddress())+"]");
		}
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
	
	// 콜 메소드. 풀에 등록되어 자동으로 호출되고 return하면 종료됨.
	@Override
	public Void call() throws Exception {
		while (connection.isConnected()) {
			String msg = receiveMsg(); // 메시지 수신
			StringTokenizer s_msg = new StringTokenizer(msg, "-"); // 받은 메시지 쪼개기.
			int type;
			try {
				type = Integer.parseInt(s_msg.nextToken()); // 타입 추출
			} catch (NumberFormatException e) {
				System.err.println("잘못된 메시지 형식입니다.");
				continue;
			}
			
			/* 타입에 따른 작업 수행*/
			switch (type) {
			case MSG.Reservation_AtoS_1:
				break;
			case MSG.GetOnNext_StoB_2:
				break;
			case MSG.CancelReservation_AtoS_3:
				break;
			case MSG.GetOn_AtoS_4:
				break;
			case MSG.PayResult_StoB_5:
				break;
			case MSG.GetOffNext_AtoS_6:
				break;
			case MSG.GetOffNext_StoB_7:
				break;
			case MSG.GetOffResult_AtoS_8:
				break;
			default:
				System.err.println("정의되지 않은 메시지 타입입니다.");
				continue;
			}
		}
		
		connection.close();
		return null;
	}
}
