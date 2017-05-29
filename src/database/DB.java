package database;

public interface DB {
	public void test();
	
	/** 버스 아이디로부터 버스의  IP/PORT 정보를 얻어옴
	 * <p> return : "???.???.???.???:????" */
	public String getBusAddress(String bus_ID);
	
	/** 버스 아이디로부터 버스의  비콘 정보 3가지(앞문ID,뒷문ID,거리)를 얻어옴
	 * <p> return : "15815F", "15815B", "2.70" */
	public String[] getBusBeacon(String bus_ID);
}
