package server.bus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 버스용 서버 스레드
 * 버스의 연결에 따라 여러 자식 스레드를 생성함.
 * busList를 통해 연결 관리
 */
public class BusServer extends Thread {
	private static BusServer busServer = null;
	private final static int PORT = 7770;
	private static boolean STOP_FLAG = false;
	private ExecutorService pool; // 자식 스레드 풀
	private ServerSocket server;
	private static HashMap<Integer, BusConnection> busList = new HashMap<Integer, BusConnection>(); // 자식 스레드 연결 관리
	
	// 싱글톤
	public static BusServer getInstance() {
		if (busServer == null)
			busServer = new BusServer();
		return busServer;
	}
	
	// 서버 스레드 초기화
	private BusServer() {
		// 1000 사이즈 풀 정의
		pool = Executors.newFixedThreadPool(1000);
		
		// 서버 소켓 생성
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("서버 생성 오류");
		}
	}
	
	// 서버 스레드의 run 메소드.
	public void run() {
		System.out.println("\n[" + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort() + "] 버스용 서버 열림");
		STOP_FLAG = false;
		while (!STOP_FLAG) {
			try {
				Socket connection = server.accept();
				
				BusChild child = new BusChild(connection, busList); // 자석 스레드 생성
				
				pool.submit(child); // 풀에 등록
			} catch (IOException e) { }
		}
	}
	
	// 서버 스레드를 종료하기위한 메소드
	public void exitServer() {
		try {
			STOP_FLAG = true; // run 메소드 종료
			sleep(500);
			pool.shutdown(); // 풀 가동 종료
			sleep(500);
			for (Entry<Integer, BusConnection> entry : busList.entrySet()) { // 연결된 소켓 모두 종료
				entry.getValue().endConnection();
			}
			
			server.close(); // 서버 소켓 닫기
			busServer = null; // 새로운 서버 스레드를 생성할 떄를 대비해 null로 초기화해줌.
			System.out.println("서버 닫힘");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printBusCount() {
		if (busList == null)
			System.out.println("연결된 버스 수 : 0");
		else
			System.out.println("연결된 버스 수 : " + busList.size());
	}
	
	/**
	 * 입력된 bus_id와 같은 BusConnection를 찾아줌
	 * @param bus_id
	 * @return BusConnection
	 */
	public static BusConnection findBus(int bus_id) {
		for (int key : busList.keySet()) {
			if (key == bus_id)
				return busList.get(key);
		}
		return null;
	}

}
