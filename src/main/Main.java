package main;

import java.util.Scanner;

import server.app.AppServer;
import server.bus.BusServer;

/*
 * 서버 관리 프로그램.
 * 서버를 열거나 닫을 수 있음.
 * */
public class Main {
	public static BusServer busServer = null;
	public static AppServer appServer = null;

	public static void main(String[] args) {
		int choice = 0;
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		
		MenuLoop:
		while (true) {
			System.out.println("************");
			System.out.println("1. 버스 서버 열기");
			System.out.println("2. 어플 서버 열기");
			System.out.println("3. 프로그램 종료");
			System.out.println("4. 버스 연결 수 확인");
			System.out.println("5. 클라이언트 연결 수 확인");
			System.out.println("************");

			System.out.print("선택 : ");
			do
				choice = scan.nextInt();
			while (choice <= 0 && choice >= 5);
			
			switch (choice) {
			// 1. 버스 서버 열기
			case 1:
				if (busServer == null) {
					busServer = BusServer.getInstance();
					busServer.start();
				}
				else
					System.out.println("이미 작동중");
				break;
			// 2. 어플 서버 열기
			case 2:
				if (appServer == null) {
					appServer = AppServer.getInstance();
					appServer.start();
				}
				else
					System.out.println("가동중인 server 없음");
				break;
			// 3. 프로그램 종료
			case 3:
				if (busServer != null)
					busServer.exitServer();
				if (appServer != null)
					appServer.exitServer();
				System.out.println("프로그램 종료");
				break MenuLoop;
			// 44. 버스 연결 수 확인
			case 4:
				if (busServer != null)
					busServer.printBusCount();
				else
					System.out.println("가동중인 server 없음");
				break;
			// 4. 연결된 클라이언트 수 확인 (미구현)
			case 5:
				if (appServer != null)
					appServer.printClientCount();
				else
					System.out.println("가동중인 server 없음");
				break;
			}
		}
}}
