package server.app;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import database.DB;
import server.MSG;
import server.app.function.Communicator;
import server.app.function.Log;
import server.app.function.PaySystem;
import server.app.function.Reservation;
import server.app.function.SenderToBus;

/**
 * 각 클라이언트와 1:1 연결을 이루는 서버의 자식 스레드
 */
// http://blog.naver.com/ksch2004/220540914491
public class AppChild implements Callable<Void> {
	private DB db = null; // Server Database
	
	private Socket app_socket = null; // 어플리케이션과 연결된 소켓
	private Communicator CC = null;
	private Reservation reservation = null;
	private SenderToBus sender_GetOn = null;
	private SenderToBus sender_GetOff = null;
	
	private boolean END = false; // 서비스가 끝났는 지 기록 (하차 완료 시 끝남)
	
	
	// 자식 스레드 생성자 : 초기화 역할
	public AppChild(Socket connection, DB db) {
		this.app_socket = connection;
		this.db = db;
		CC = new Communicator(connection, db);
	}
		
	// 콜 메소드. 풀에 등록되어 자동으로 호출되고 return하면 종료됨.
	@Override
	public Void call() throws Exception {
		System.out.println();
		Log.out("어플", "CO", app_socket, "새 클라이언트 접속");
		
		reservation = new Reservation(); // 예약 정보 클래스
		
		SERVICE:
		while (app_socket.isConnected()) {
			String msg = CC.recvFromApp(); // 메시지 수신
			if (msg == null) {
				Log.err("어플", "CO", app_socket, "연결 끊김");
				break SERVICE;
			}
			StringTokenizer s_msg = new StringTokenizer(msg, "-"); // 받은 메시지 쪼개기.
			int type;
			try {
				type = Integer.parseInt(s_msg.nextToken()); // 타입 추출
			} catch (NumberFormatException e) {
				Log.err("어플", "IN", app_socket, "잘못된 메시지 형식");
				continue;
			}
			
			/* 받은 메시지의 타입에 따른 작업 수행*/
			switch (type) {
			/* 예약 메시지 */ // 테스트 완료
			case MSG.Query_AtoS_2:
				if (reservateGetOn(s_msg))
					Log.out("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 버스 예약");
				else {
					Log.err("어플", "IN", app_socket, "예약 실패");
					CC.sendToApp(MSG.Failure_StoA_0);
					break SERVICE;
				}
				break;
				
			/* 예약 취소 메시지 */ // 테스트 완료
			case MSG.CancelReservation_AtoS_6:
				if (cancelReservation()) {
					Log.out("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 예약 취소");
					break SERVICE;
				}
				else {
					Log.err("어플", "IN", app_socket, "예약 취소 실패");
					CC.sendToApp(MSG.Failure_StoA_0);
				}
				break;
				
			/* 탑승 메시지 */ // 테스트 완료
			case MSG.GetOn_AtoS_8:
				if (processGetOn(s_msg))
					Log.out("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 탑승 처리");
				else
					Log.err("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 탑승 처리 실패");
				break;
			
			/* 하차 메시지 */ // 테스트 완료
			case MSG.GetOffNext_AtoS_10:
				if (reservateGetOff())
					Log.out("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 하차 예약");
				else
					Log.err("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 하차 예약 실패");
				break;
				
			/* 하차 결과 메시지 */ // 테스트 완료
			case MSG.GetOffResult_AtoS_12:
				if (!processGetOff(s_msg))
					Log.err("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 하차 처리 실패");
				if (END) {
					Log.out("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 하차 완료");
					break SERVICE;
				}
				break;
				
			/* 정의된 메시지 타입이 아닐 때 */
			default:
				CC.sendToApp(MSG.Failure_StoA_0);
				Log.err("어플", "IN", app_socket, "정의되지 않은 메시지 타입");
				continue;
			}
		}
		Log.out("어플", "CO", app_socket, "서비스 종료");
		
		CC.endConnect();
		return null;
	}
	
	/** 버스 예약 함수
	 * <br>1. 버스의 정보를 DB로부터 읽고, 버스와 연결
	 * <br>2. 버스에게 탑승 알림 메시지 전송(GetOnNext_StoB_5)
	 * <br>3. 어플에게 버스 비콘 정보 전송(NotifyOfBusBeacon_StoA_3)
	 *  */
	private boolean reservateGetOn(StringTokenizer s_msg) {
		reservation.inputReservation(s_msg); // 예약 정보 받아서 기록
		
		String[] beaconInfo = db.getBusBeacon(reservation.bus_id); // DB로부터 버스의 비콘 정보 부르기

		if (beaconInfo == null)
			return false;
		
		if (!CC.connectToBus(reservation.bus_id)) // 버스 아두이노와 연결
			return false;
		
		if (!CC.sendToBus(MSG.GetOnNext_StoB_5)) // 버스에게 탑승 알람 메시지 전송
			return false;
		reservation.isReservated = true; // 예약 처리
		
		String msg = MSG.NotifyOfBusBeacon_StoA_3 + "-" + beaconInfo[0] + "-" + beaconInfo[1] + "-" + beaconInfo[2];
		return CC.sendToApp(msg); // 성공 메시지 응답
	}
	
	/** 예약 취소 함수
	 * <br>1. 버스에게 예약 취소 메시지 전송(CancelReservation_StoB_7)
	 * <br>2. 어플에게 성공 메시지 응답(Success_StoA_1)
	 *  */
	private boolean cancelReservation() {
		if (!reservation.isReservated) // 예약이 안되있을 경우
			return false;
		
		if (!CC.sendToBus(MSG.CancelReservation_StoB_7)) // 취소 메시지 전송
			return false;
		
		END = true;
		return CC.sendToApp(MSG.Success_StoA_1); // 성공 메시지 응답
	}
	
	/** 탑승 처리 함수 
	 * <br>1. 버스에게 결제 결과 메시지 전송(PayResult_StoB_9)
	 * <br>2. 어플에게 성공 메시지 전송(Success_StoA_1)
	 * */
	private boolean processGetOn(StringTokenizer s_msg) {
		if (!reservation.isReservated) // 예약이 안되있을 경우
			return false;
		
		reservation.inputGetOn(s_msg); // 결제 정보를 입력
		
		int result = 0;
		if (reservation.auto_pay == true) // 자동 결제 선택 시
			result = PaySystem.pay(reservation); // 결제
		
		String msg = MSG.PayResult_StoB_9 + "-" + Integer.toString(result);
		if (!CC.sendToBus(msg)) // 결제 결과 메시지를 버스 아두이노에게 보냄
			return false;
		return CC.sendToApp(MSG.Success_StoA_1); // 성공 메시지 응답
	}
	
	/** 하차 예약 함수
	 * <br>1. 버스에게 하차 예약 메시지 전송(GetOffNext_StoB_11)
	 * <br>2. 어플에게 성공 메시지 전송(Success_StoA_1)
	 *  */
	private boolean reservateGetOff() {
		if (!reservation.isReservated) // 예약이 안되있을 경우
			return false;
		
		if (!CC.sendToBus(MSG.GetOffNext_StoB_11)) // 버스에게 하차 예약 메시지 전달
			return false;
		return CC.sendToApp(MSG.Success_StoA_1); // 성공 메시지 응답
	}
	
	/** 하차 처리 함수
	 * <br>1. 하차 실패 시 reservateGetOff 함수 호출/리턴
	 * <br>2. 하차 성공 시 어플에게 성공 메시지 전송 (Success_StoA_1)
	 *  */
	private boolean processGetOff(StringTokenizer s_msg) {
		if (!reservation.isReservated) // 예약이 안되있을 경우
			return false;
		
		String result = s_msg.nextToken();
		if (result.equals("F")) { // 하차 실패 시
			Log.out("어플", "IN", app_socket, "BUS_ID:" + reservation.bus_id + " 하차 실패 및 재예약");
			return reservateGetOff(); // 하차 재예약
		}
		// 성공 시 종료
		END = true;
		return CC.sendToApp(MSG.Success_StoA_1); // 성공 메시지 응답
	}
	
	public void endThread() {
		try {
			CC.endConnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
