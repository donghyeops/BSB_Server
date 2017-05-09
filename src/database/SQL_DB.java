package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SQL_DB implements DB {
	private String jdbc_driver = "com.mysql.jdbc.Driver";
	private String jdbc_url = "jdbc:mysql://localhost:3307/bsb?characterEncoding=utf8&autoReconnect=true&useSSL=false";
	private Connection conn;
	private Statement stmt;

	private void connect() {
		try {
			Class.forName(jdbc_driver);
			conn = DriverManager.getConnection(jdbc_url, "root", "0000");
			// sjb378
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			System.out.println("jdbc_driver 로드 실패");
		} catch (Exception e) {
			System.out.println("db 연결 실패");
		}

	}

	private void disconnect() {
		try {
			stmt.close();
			conn.close();
		} catch (Exception e) {
		}
	}

	public void test() {
		connect();
		disconnect();
	}

	// 입금 1 (샘플1 - select문은 executeQuery, update/insert into 문은 executeUpdate)
	public long addmoney(String account_id, long in_money) {
		String sql = "select account_money from account where account_id='" + account_id + "'";
		System.out.println(sql);
		long money = 0;
		try {
			connect();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				money = rs.getLong("account_money");
			}
			money = money + in_money;
			String sql2 = "update account set account_money=" + money + " where account_id='" + account_id + "'";
			stmt.executeUpdate(sql2);
			rs.close();
			disconnect();
		} catch (Exception e) {
			System.out.println("입금실패");
		}
		return money;
	}

	// 입금 + 내역 1 (샘플2 - select문은 executeQuery)
	public long addmoney_his(String account_id, long in_money) {
		String sql1 = "select custom_n from account where account_id='" + account_id + "'";
		long balance = 0;
		int custom_n = -1;
		System.out.println(sql1);
		try {
			connect();
			Statement stmt2 = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql1);
			if (rs.next()) {
				custom_n = rs.getInt("custom_n");
			}
			System.out.println("입금금액" + in_money);
			balance = addmoney(account_id, in_money);
			System.out.println("총금액" + balance);
			String sql2 = "insert into history (history_custom_n, history_account_id, history_type, history_custom_name, history_money, history_time, history_balance) "
					+ "values (" + custom_n + ",'" + account_id + "',0,'0'," + in_money + ",now()," + balance + ")";
			System.out.println(sql2);
			stmt2.executeUpdate(sql2);
			rs.close();
			stmt2.close();
			disconnect();
		} catch (Exception e) {
			System.out.println(e.getMessage() + "  입금내역저장실패");
		}
		return balance;
	}
}