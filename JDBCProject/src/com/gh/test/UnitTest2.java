package com.gh.test;

import java.util.Map;
import java.util.Scanner;

import com.gh.dao.impl.CustomerDAOImpl;
import com.gh.dao.impl.GuestHouseDAOImpl;


public class UnitTest2 implements Runnable {

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

//		private int num; // cus_num
//		private String name; // 원래 컬럼명cus_name
//		private String address; // cus_adress
//		private String ssn; //cus_ssn
//		private char gender;// cus_gender
//		private String phone ;// cus_phone
//		private String grade;// cus_grade
		
//		private int num;// res_num
//		private int gusNum ; // 외래키
//		private int cusNum;// 외래키
//		private LocalDate checkInDate; // res_cindate
//		private LocalDate checkOutDate;//res_coutdate
//		private int totalPrice; //res_tprice
//		private int totalPeople;// res_tpeople
		
		try {
			//CustomerDAOImpl
//		1.registerCustomer() 성공
//			cdao.registerCustomer(new Customer(11,"강민기","경기 안산시","920906",'M',"010-5711-1106","VIP"));
//		2.updateCustomer() 성공
//			cdao.updateCustomer(new Customer(11,"강민기","경기 안산시","920906",'M',"010-5555-7777","BRONZE"));
//		3.deleteCustomer() 성공
//			cdao.deleteCustomer(11);
//		4.addReservation() 성공(보수필요 - 해당날짜에 수용인원확인하고 예약 추가되는걸로)
//			cdao.addReservation(new Reservation(51,10,11,LocalDate.of(2025, 9, 6), LocalDate.of(2025, 9, 8),100000,2));
//		5.updateReservation() 보수필요 - 해당날짜에 수용인원확인하고 예약 수정되는걸로 및 최종가격수정
//		6.cancelReservation()
//			cdao.cancelReservation(51);
//		7.getReservation() 성공
//			cdao.getReservation(1).stream().forEach(System.out::println);
//		8.getRegionGHReservation() 성공
//			System.out.println(cdao.getRegionGHReservation());
//		9.getDiscountedPrice() 적용완료
//		10.getAllGuestHouses() 성공
//			cdao.getAllGuestHouses().stream().forEach(System.out::println);
//		11.getRemainingCapacity() 수정중
//			System.out.println(cdao.getRemainingCapacity(11, Date.valueOf(LocalDate.of(2025, 6, 8))));
//		12.getGuestHouses() 성공
//			cdao.getGuestHouses("파티").stream().forEach(System.out::println);
//		13.calculatePriceByDay() 적용완료
//		14.getRegionGuestHouse() 성공
//			cdao.getRegionGuestHouse("제주").stream().forEach(System.out::println);
			
			//GuestHouseDAOImpl
//		1.getTotalSalesPerGuestHouse()
//		Map<String, Integer> result = gdao.getTotalSalesPerGuestHouse();
//		    for (Map.Entry<String, Integer> entry : result.entrySet()) {
//		        System.out.println("게스트하우스: " + entry.getKey() + ", 매출등급: " + entry.getValue());
//		    }
//		2.
		
			
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