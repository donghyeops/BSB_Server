package test;

import database.DB;
import database.SQL_DB;

public class test {
	public static void main(String[] args) {
		DB db =  new SQL_DB();
		String address = db.getBusAddress(0);
		String[] beaconInfo = db.getBusBeacon(0);
		
		System.out.println("address : " + address);
		System.out.println("beaconInfo : " + beaconInfo[0] + " " + beaconInfo[1] + " " + beaconInfo[2] + " ");
	}
}
