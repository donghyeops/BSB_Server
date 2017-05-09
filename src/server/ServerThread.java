package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 서버 스레드
 * 클라이언트의 연결에 따라 여러 자식 스레드를 생성함.
 */
public class ServerThread extends Thread {
	private static ServerThread serverThread = null;
	private final static int PORT = 9446;
	private static boolean STOP_FLAG = false;
	private ExecutorService pool;
	private ServerSocket server;
	
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
		STOP_FLAG = false;
		while (!STOP_FLAG) {
			try {
				System.out.println("새 클라이언트 접속");
				Socket connection = server.accept();
				Callable<Void> task = new ChildThread(connection); // 자석 스레드 생성
				
				pool.submit(task); // 풀에 등록
			} catch (IOException e) { }
		}
	}
	
	// 서버 스레드를 종료하기위한 메소드
	public void exitServer() {
		try {
			STOP_FLAG = true; // run 메소드 종료
			sleep(500);
			pool.shutdown(); // 풀에 들어있는 자식 스레드들 종료
			sleep(500);
			server.close(); // 서버 소켓 닫기
			serverThread = null; // 새로운 서버 스레드를 생성할 떄를 대비해 null로 초기화해줌.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 미구현
	public void printClientCount() {
		System.out.println("연결된 클라이언트 수 : ");
	}
}
