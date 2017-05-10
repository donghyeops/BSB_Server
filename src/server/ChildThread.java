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
	
	/** 예약 정보 클래스 */
	class Reservation {
		int bus_id; // 탑승하려는 버스 ID
		String de_stop; // 출발 정류장 ID
		String ap_stop; // 도착 정류장 ID
		
		boolean auto_pay; // 자동 결제 선택 유무
		String card_number; // 카드 번호
		int card_life_year; // 카드 유효기간 연
		int card_life_month; // 카드 유효기간 월
		int cost; // 계산된 요금
		
		/** Reservation_AtoS_1 */
		void inputReservation(StringTokenizer s_msg) {
			bus_id = Integer.parseInt(s_msg.nextToken());
			de_stop = s_msg.nextToken();
		}
		
		/** GetOn_AtoS_1 
		 *  0: 직접결제, 1:결제성공, 2:결제실패 */
		int inputGetOn(StringTokenizer s_msg) {
			auto_pay = Boolean.parseBoolean(s_msg.nextToken());
			if (!auto_pay)
				return 0;
			card_number = s_msg.nextToken();
			String card_life = s_msg.nextToken();
			card_life_month = Integer.parseInt(card_life.substring(0, 1));
			card_life_year = Integer.parseInt(card_life.substring(3, 4));
			ap_stop = s_msg.nextToken();
			// 요금 계산
			// cost = cost(de_stop, ap_stop);
			// 결제
			// if (boolean result = pay(card_number, card_life_month, card_life_year, cost))
			//	return 성공;
			// else
			//  return 실패;
			return 1;
		}
	}
	
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
		System.out.println("새 클라이언트 접속");
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
				System.out.println("Reservation_AtoS_1");
				sendMsg("Reservation_AtoS_1");
				break;
			case MSG.CancelReservation_AtoS_3:
				System.out.println("CancelReservation_AtoS_3");
				sendMsg("CancelReservation_AtoS_3");
				break;
			case MSG.GetOn_AtoS_4:
				System.out.println("GetOn_AtoS_4");
				sendMsg("GetOn_AtoS_4");
				break;
			case MSG.GetOffNext_AtoS_6:
				System.out.println("GetOffNext_AtoS_6");
				sendMsg("GetOffNext_AtoS_6");
				break;
			case MSG.GetOffResult_AtoS_8:
				System.out.println("GetOffResult_AtoS_8");
				sendMsg("GetOffResult_AtoS_8");
				break;
			default:
				sendMsg("해당 메시지 없음 !");
				System.err.println("정의되지 않은 메시지 타입입니다.");
				continue;
			}
		}
		
		connection.close();
		return null;
	}
}
