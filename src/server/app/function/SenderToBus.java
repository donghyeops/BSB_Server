package server.app.function;

import java.io.BufferedWriter;

/**
 * 버스가 전 정류장에 도착하면 다음 탑승자가 있음을 알림
 * (GetOnNext_StoB_2/ 메시지를 전송)
 */
public class SenderToBus extends Thread {
	private int msg;
	private Communicator CC = null;
	private boolean stopFlag = false;
	private boolean isSended = false; // 보냈는 지 기록
	
	public SenderToBus(int msg, Communicator CC) {
		this.msg = msg;
		this.CC = CC;
	}
	
	public void run() {
		int distance;
		while (stopFlag) {
			// 버스 API로부터 버스 거리 읽기
			// 거리가 이전 정류장이라면 메시지 전송
			if (true) {
				CC.sendToBus(msg);
				isSended = true;
				break;
			}
		}
	}
	
	/** 예약 취소 */
	public void doStop() {
		stopFlag = true;
	}
	
	/** 보냈는지 확인 */
	public boolean isSended() {
		try {
			sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSended;
	}
}
