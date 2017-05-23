package server.app.function;

import java.util.StringTokenizer;

/** 예약 정보 클래스 */
public class Reservation {
	public boolean isReservated = false; // 예약 됬는지 확인
	public int bus_id; // 탑승하려는 버스 ID
	public String de_stop; // 출발 정류장 ID
	public String ap_stop; // 도착 정류장 ID
	
	public boolean auto_pay; // 자동 결제 선택 유무
	String card_number; // 카드 번호
	int card_life_year; // 카드 유효기간 연
	int card_life_month; // 카드 유효기간 월
	int cost; // 계산된 요금
	
	/** Reservation_AtoS_1 */
	public void inputReservation(StringTokenizer s_msg) {
		bus_id = Integer.parseInt(s_msg.nextToken());
		de_stop = s_msg.nextToken();
	}
	
	/** GetOn_AtoS_1 */
	public void inputGetOn(StringTokenizer s_msg) {
		if (s_msg.nextToken().equals("T")) {
			auto_pay = true;
			card_number = s_msg.nextToken();
			String card_life = s_msg.nextToken();
			card_life_month = Integer.parseInt(card_life.substring(0, 1));
			card_life_year = Integer.parseInt(card_life.substring(3, 4));
			ap_stop = s_msg.nextToken();
		}
		else
			auto_pay = false;
	}
}