package server;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import database.DB;
import server.function.Communicator;
import server.function.Log;
import server.function.PaySystem;
import server.function.Reservation;
import server.function.SenderToBus;

/**
 * 각 클라이언트와 1:1 연결을 이루는 서버의 자식 스레드
 */
// http://blog.naver.com/ksch2004/220540914491
public class ChildThread implements Callable<Void> {
	private DB db = null; // Server Database
	
	private Socket app_socket = null; // 어플리케이션과 연결된 소켓
	private Communicator CC = null;
	private Reservation reservation = null;
	private SenderToBus sender_GetOn = null;
	private SenderToBus sender_GetOff = null;
	
	private boolean END = false; // 서비스가 끝났는 지 기록 (하차 완료 시 끝남)
	
	
	// 자식 스레드 생성자 : 초기화 역할
	public ChildThread(Socket connection, DB db) {
		this.app_socket = connection;
		CC = new Communicator(connection, db);
	}
		
	// 콜 메소드. 풀에 등록되어 자동으로 호출되고 return하면 종료됨.
	@Override
	public Void call() throws Exception {
		System.out.println("\n[" + app_socket.getInetAddress().getHostAddress() + ":" + app_socket.getLocalPort() + "] 새 클라이언트 접속");
		
		reservation = new Reservation(); // 예약 정보 클래스
		while (app_socket.isConnected()) {
			String msg = CC.recvFromApp(); // 메시지 수신
			StringTokenizer s_msg = new StringTokenizer(msg, "-"); // 받은 메시지 쪼개기.
			int type;
			try {
				type = Integer.parseInt(s_msg.nextToken()); // 타입 추출
			} catch (NumberFormatException e) {
				Log.err("어플", "IN", app_socket, "잘못된 메시지 형식");
				continue;
			}
			
			/* 받은 메시지의 타입에 따른 작업 수행*/
			SERVICE:
			switch (type) {
			/* 예약 메시지 */
			case MSG.Reservation_AtoS_1:
				if (reservateGetOn(s_msg))
					Log.out("어플", "IN", app_socket, "ID:" + reservation.bus_id + " 버스 예약");
				else
					Log.err("어플", "IN", app_socket, "예약 실패");
				break;
				
			/* 예약 취소 메시지 */
			case MSG.CancelReservation_AtoS_4:
				if (cancelReservation())
					System.out.println("[CancelReservation_AtoS_4] 처리 성공");
				else
					System.out.println("[CancelReservation_AtoS_4] 처리 실패");
				break;
				
			/* 탑승 메시지 */
			case MSG.GetOn_AtoS_6:
				if (processGetOn(s_msg))
					System.out.println("[GetOn_AtoS_6] 처리 성공");
				else
					System.out.println("[GetOn_AtoS_6] 처리 실패");
				break;
			
			/* 하차 메시지 */	
			case MSG.GetOffNext_StoB_9:
				if (reservateGetOff())
					System.out.println("[GetOffNext_StoB_9] 처리 성공");
				else
					System.out.println("[GetOffNext_StoB_9] 처리 실패");
				break;
				
			/* 하차 결과 메시지 */
			case MSG.GetOffResult_AtoS_10:
				if (processGetOff(s_msg))
					System.out.println("[GetOffResult_AtoS_10] 처리 성공");
				else
					System.out.println("[GetOffResult_AtoS_10] 처리 실패");
				if (END) {
					System.out.println("[" + app_socket.getLocalAddress().toString() + "] 하차 완료");
					break SERVICE;
				}
				break;
				
			/* 정의된 메시지 타입이 아닐 때 */
			default:
				CC.sendToApp(MSG.Failure_StoA_0);
				System.err.println("[(어플/IN)" + app_socket.getInetAddress().getHostAddress() + ":" + app_socket.getLocalPort() +"] 정의되지 않은 메시지 타입");
				continue;
			}
		}
		if (app_socket.isConnected())
			System.err.println("[(어플/CO)" + app_socket.getInetAddress().getHostAddress() + ":" + app_socket.getLocalPort() +"] 연결 끊김");
		
		System.out.println("[(어플/CO)" + app_socket.getInetAddress().getHostAddress() + ":" + app_socket.getLocalPort() +"] 서비스 종료");
		app_socket.close();
		return null;
	}
	
	/** 탑승 예약 함수 */
	private boolean reservateGetOn(StringTokenizer s_msg) {
		reservation.inputReservation(s_msg); // 예약 정보 받아서 기록
		if (!CC.connectToBus(reservation.bus_id)) // 버스 아두이노와 연결
			return false;
		
		if (!CC.sendToBus(MSG.GetOnNext_StoB_2)) // 버스에게 탑승 알람 메시지 전송
			return false;
		
		System.out.println("버스에게 메시지 전송함");
		String[] beaconInfo = db.getBusBeacon(reservation.bus_id); // DB로부터 버스의 비콘 정보 부르기
		System.out.println("beaconInfo 받아옴");
		if (beaconInfo == null)
			return false;
		
		String msg = MSG.NotifyOfBusBeacon_StoA_3 + "-" + beaconInfo[0] + "-" + beaconInfo[1] + "-" + beaconInfo[2];
		System.out.println(msg);
		return CC.sendToApp(msg); // 어플에게 비콘 정보 보냄
	}
	
	/** 예약 취소 함수 */
	private boolean cancelReservation() {
		return CC.sendToBus(MSG.CancelReservation_StoB_5); // 취소 메시지 전송
	}
	
	/** 탑승 처리 함수 */
	private boolean processGetOn(StringTokenizer s_msg) {
		reservation.inputGetOn(s_msg); // 결제 정보를 입력
		
		int result = 0;
		if (reservation.auto_pay == true) // 자동 결제 선택 시
			result = PaySystem.pay(reservation); // 결제
		
		String msg = MSG.PayResult_StoB_7 + "-" + Integer.toString(result);
		return CC.sendToBus(msg); // 결제 결과 메시지를 버스 아두이노에게 보냄
	}
	
	/** 하차 예약 함수 */
	private boolean reservateGetOff() {
		return CC.sendToBus(MSG.GetOffNext_StoB_9); // 버스에게 하차 예약 메시지 전달
	}
	
	/** 하차 처리 함수 */
	private boolean processGetOff(StringTokenizer s_msg) {
		String result = s_msg.nextToken();
		if (result.equals("F")) // 하차 실패 시
			return reservateGetOff(); // 하차 재예약
		// 성공 시 종료
		END = true;
		return true;
	}
}
