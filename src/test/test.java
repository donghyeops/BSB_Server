package test;

import database.DB;
import database.SQL_DB;

public class test {
	public static void main(String[] args) {
		DB db =  new SQL_DB();
		db.test();
		System.out.println("성공?");
	}
}
