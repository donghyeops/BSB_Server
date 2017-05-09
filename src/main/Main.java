package main;
import java.util.Scanner;

import server.ServerThread;

/*
 * 서버 관리 프로그램.
 * 서버를 열거나 닫을 수 있음.
 * */
public class Main {
	public static ServerThread serverThread = null;
	
	public static void main(String[] args) {
		int choice = 0;
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		
		MenuLoop:
		while (true) {
			System.out.println("************");
			System.out.println("1. 서버 열기");
			System.out.println("2. 서버 닫기");
			System.out.println("3. 프로그램 종료");
			System.out.println("4. 클라이언트 연결 수 확인");
			System.out.println("************");

			System.out.print("선택 : ");
			do
				choice = scan.nextInt();
			while (choice <= 0 && choice >= 5);
			
			switch (choice) {
			// 1. 서버 열기1
			case 1:
				if (serverThread == null) {
					serverThread = ServerThread.getInstance();
					serverThread.start();
				}
				else
					System.out.println("이미 작동중.");
				break;
			// 2. 서버 닫기
			case 2:
				if (serverThread != null) {
					serverThread.exitServer();
					serverThread = null;
				}
				else
					System.out.println("가동중인 server 없음.");
				break;
			// 3. 프로그램 종료
			case 3:
				if (serverThread != null)
					serverThread.exitServer();
				System.out.println("프로그램 종료");
				break MenuLoop;
			// 4. 연결된 클라이언트 수 확인 (미구현)
			case 4:
				serverThread.printClientCount();
				break;
			}
		}
	}
}
