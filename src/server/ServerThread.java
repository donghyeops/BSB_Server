package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.DB;
import database.SQL_DB;
import server.function.Log;

/**
 * 서버 스레드
 * 클라이언트의 연결에 따라 여러 자식 스레드를 생성함.
 */
public class ServerThread extends Thread {
	private static ServerThread serverThread = null;
	private final static int PORT = 9446;
	private static boolean STOP_FLAG = false;
	private ExecutorService pool; // 자식 스레드 풀
	private ServerSocket server;
	private Vector<ChildThread> childList = new Vector<ChildThread>(); // 자식 스레드 연결 관리
	private DB db = new SQL_DB();
	
	// 싱글톤
	public static ServerThread getInstance() {
		if (serverThread == null)
			serverThread = new ServerThread();
		return serverThread;
	}
	
	// 서버 스레드 초기화
	private ServerThread() {
		// 100 사이즈 풀 정의
		// 100개 이상의 클라이언트와 연결할 수 없음.
		// 서버 과부하 방지.
		pool = Executors.newFixedThreadPool(100);
		
		// 서버 소켓 생성
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("서버 생성 오류");
		}
	}
	
	// 서버 스레드의 run 메소드.
	public void run() {
		System.out.println("\n[" + server.getInetAddress().getHostAddress() + ":" + server.getLocalPort() + "] 서버 열림");
		STOP_FLAG = false;
		while (!STOP_FLAG) {
			try {
				Socket connection = server.accept();
				
				ChildThread child = new ChildThread(connection, db); // 자석 스레드 생성
				childList.add(child); // 자식 스레드 리스트에 등록
				
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
			for (ChildThread ct : childList) { // 연결된 소켓 모두 종료
				ct.endThread();
			}
			server.close(); // 서버 소켓 닫기
			serverThread = null; // 새로운 서버 스레드를 생성할 떄를 대비해 null로 초기화해줌.
			System.out.println("서버 닫힘");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printClientCount() {
		if (childList == null)
			System.out.println("연결된 클라이언트 수 : 0");
		else
			System.out.println("연결된 클라이언트 수 : " + childList.size());
	}
}
