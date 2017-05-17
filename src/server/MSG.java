package server;

/** [전체 메시지 정의] 
 * 포멧 : 명칭_XtoX_(value)
 * X : S(Server), A(Application), B(Bus Arduino)
 * value : 메시지 값
 * */
public interface MSG {
	/** 실패 메시지
	 *  <p> Example : "0"
	 *  */
	int Failure_StoA_0 = 0;
	
	/** 초기 예약 메시지(탑승하려는 버스ID, 출발정류장ID)
	 *  <p> Example : "1-4575-41571"
	 *  */
	int Reservation_AtoS_1 = 1;
	
	/** 탑승 예정 알람 메시지
	 *  <p> Example : "2"
	 *  */
	int GetOnNext_StoB_2 = 2;
	
	/** 버스의 비콘 정보 알림 (버스 앞문의 BeaconID, 버스 뒷문의 BeaconID, 비콘 사이의 거리(미터))
	 *  <p> Example : "3-15648F-15648B-13.40"
	 *  */
	int NotifyOfBusBeacon_StoA_3 = 3;
	
	/** 예약 취소 메시지
	 *  <p> Example : "4"
	 *  */
	int CancelReservation_AtoS_4 = 4;
	
	/** 예약 취소 메시지
	 *  <p> Example : "5"
	 *  */
	int CancelReservation_StoB_5 = 5;
	
	/** 탑승 완료 메시지(선결제T/F) T면, (T, 카드번호, 유효기간, 도착정류장ID)
	 *  <p> Example : "6-T-0101045445715584-05/21-56712" or "6-F"
	 *  */
	int GetOn_AtoS_6 = 6;
	
	/** 결제 결과 메시지(성공:0, 실패:1, 선결제F:2). 선결제F면 직접 찍게 해야함
	 *  <p> Example : "7-0" or "7-1" or "7-2"
	 *  */
	int PayResult_StoB_7 = 7;
	
	/** 하차 예정 알람 메시지 [사용자->서버]
	 *  <p> Example : "8"
	 *  */
	int GetOffNext_AtoS_8 = 8;
	
	/** 하차 예정 알람 메시지 [서버->버스]
	 *  <p> Example : "9"
	 *  */
	int GetOffNext_StoB_9 = 9;
	
	/** 하차 성공 여부(T/F)
	 *  <p> Example : "10-T" or "10-F"
	 *  */
	int GetOffResult_AtoS_10 = 10;
}
