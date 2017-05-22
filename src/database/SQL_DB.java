package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// synchronize 걸어야함
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
	
	public String getBusAddress(int bus_id) {
		String sql = "select ip, port from bus where bus_id='" + bus_id + "'";
		String address = null;
		try {
			connect();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				address = rs.getString("ip") + ":" + rs.getInt("port");
			}
			rs.close();
			disconnect();
		} catch (Exception e) {
			return null;
		}
		return address;
	}
	
	public String[] getBusBeacon(int bus_id) {
		String[] beaconInfo = new String[3];
		String sql = "select f_beacon, b_beacon, distance from bus where bus_id='" + bus_id + "'";
		try {
			connect();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				beaconInfo[0] = rs.getString("f_beacon");
				beaconInfo[1] = rs.getString("b_beacon");
				beaconInfo[2] = Float.toString(rs.getFloat("distance"));
			} else
				return null;
			rs.close();
			disconnect();
		} catch (Exception e) {
			return null;
		}
		return beaconInfo;
	}
}