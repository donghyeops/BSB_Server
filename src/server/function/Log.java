package server.function;

import java.net.Socket;

public class Log {
	public static void out(String subject, String type, Socket socket, String msg) {
		System.out.println("[(" + subject + "/" + type +")" + socket.getInetAddress().getHostAddress() + "] " + msg);
	}
	public static void err(String subject, String type, Socket socket, String msg) {
		System.err.println("[(" + subject + "/" + type +")" + socket.getInetAddress().getHostAddress() + "] " + msg);
	}
}
