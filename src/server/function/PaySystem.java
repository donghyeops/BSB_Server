package server.function;

public class PaySystem {	
	/** 0: 직접결제, 1:결제성공, 2:결제실패 */
	public static int pay(Reservation reservation) {
		if (!reservation.auto_pay)
			return 0;
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
