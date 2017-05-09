package server;

/** [전체 메시지 정의] 
 * 포멧 : 명칭_XtoX_(value)
 * X : S(Server), A(Application), B(Bus Arduino)
 * value : 메시지 값
 * */
public interface MSG {
	/** 초기 예약 메시지(탑승하려는 버스ID, 출발정류장ID) */
	int Reservation_AtoS_1 = 1;
	
	/** 탑승 예정 알람 메시지 */
	int GetOnNext_StoB_2 = 2;
	
	/** 예약 취소 메시지 */
	int CancelReservation_AtoS_3 = 3;
	
	/** 탑승 완료 메시지(선결제T/F) T면, (T, 카드번호, 유효기간, 도착정류장ID) */
	int GetOn_AtoS_4 = 4;
	
	/** 결제 결과 메시지(성공:0, 실패:1, 선결제F:2). 선결제F면 직접 찍게 해야함 */
	int PayResult_StoB_5 = 5; // 
	
	/** 하차 예정 알람 메시지 [사용자->서버] */
	int GetOffNext_AtoS_6 = 6; // 
	
	/** 하차 예정 알람 메시지 [서버->버스] */
	int GetOffNext_StoB_7 = 7; // 
	
	/** 하차 성공 여부(T/F) */
	int GetOffResult_AtoS_8 = 8;
}
