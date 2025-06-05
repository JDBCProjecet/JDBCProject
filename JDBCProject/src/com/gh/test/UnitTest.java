package com.gh.test;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.gh.dao.GuestHouseDAO;
import com.gh.dao.impl.CustomerDAOImpl;
import com.gh.dao.impl.GuestHouseDAOImpl;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;


public class UnitTest implements Runnable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		UnitTest test = new UnitTest();
//		Thread t = new Thread(test);
//		t.start();
		CustomerDAOImpl cdao = CustomerDAOImpl.getInstance();
		GuestHouseDAOImpl gdao = GuestHouseDAOImpl.getInstance();
		Scanner scan = new Scanner(System.in);
		
		boolean flag = true;
		//Customer(int num, String name, String address, String ssn, char gender, String phone, String grade) {
		//public GuestHouse(int num, String serviceName, String name, String address, int price, int capacity)
		//Reservation(int num, String serviceName, int cusNum, Date checkInDate, Date checkOutDate, int totalPrice,	int totalPeople
	
		try {
//			cdao.registerCustomer(new Customer(2,"이름","주소","999",'남',"010","bronze"));
//			cdao.updateCustomer(new Customer(2,"수정","주소","999",'남',"010","bronze"));
//			cdao.deleteCustomer(2);
//			gdao.registerGuestHouse(new GuestHouse(1,"파티","게하1","서울시 강남구 논현동",100000,10));
//			gdao.updateGuestHouse(new GuestHouse(1,"파티","게하2","서울시 강남구 논현동",100000,10));
//			gdao.deleteGuestHouse(1);
//			cdao.addReservation(new Reservation(1,"파티",1,new Date(2025,05,24),new Date(2025,05,24),10,5));
//			cdao.updateReservation(new Reservation(1,"파티",1,new Date(2025,05,24),new Date(2025,05,24),10,6));
//			cdao.cancelReservation(1);
//			cdao.getReservation(1).stream().forEach(System.out::println);

//			System.out.println(gdao.getUsageStatsByDate());
			System.out.println(gdao.getSalesStatsByDate());

		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	@Override
	public void run() {
		while (true) {// 무한 루핑을 돌면서 작업을 하도록...
			
			// 쓰레드가 작업하는 코드를 작성....실시간으로 예약테이블의 정보를 가져와서
			try {
				
				Thread.sleep(5000); //5초 마다
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}