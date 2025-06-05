package com.gh.test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.gh.dao.GuestHouseDAO;
import com.gh.dao.impl.CustomerDAOImpl;
import com.gh.dao.impl.GuestHouseDAOImpl;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateException;
import com.gh.exception.RecordNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;


public class GuestHouseTest implements Runnable {
	static CustomerDAOImpl cdao = CustomerDAOImpl.getInstance();
	static GuestHouseDAOImpl gdao = GuestHouseDAOImpl.getInstance();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== 게스트하우스 예약 시스템 ===");
            System.out.println("1. 고객으로 접속");
            System.out.println("2. 관리자로 접속");
            System.out.println("0. 종료");

            int input = scanner.nextInt();
            scanner.nextLine();

            switch (input) {
                case 1:
                	userMenu();
                	break;
                case 2:
                	adminMenu();
                	break;
                case 0: 
                    System.out.println("시스템을 종료합니다.");
                    return;
                default:
                	System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // ===================== 고객 메뉴 =====================
    public static void userMenu() {
        while (true) {
            System.out.println("\n--- 고객 메뉴 ---");
            System.out.println("1. 예약하기");
            System.out.println("2. 예약 조회");
            System.out.println("3. 예약 취소");
            System.out.println("4. 예약 변경");
            System.out.println("5. 예약 후기 작성");
            System.out.println("6. 회원가입");
            System.out.println("7. 회원정보 수정");
            System.out.println("8. 회원 탈퇴");
            System.out.println("9. 프로모션(등급별 할인)");
            System.out.println("10. 조회 기능");
            System.out.println("0. 뒤로가기");

            int input = scanner.nextInt();
            scanner.nextLine();

            switch (input) {
                case 1:
                	makeReservation();
                	break;
                case 2:
                	viewReservation();
                	break;
                case 3:
                	cancelReservation();
                	break;
                case 4:
                	modifyReservation();
                	break;
                case 5:
                	writeReview();
                	break;
                case 6:
                	signUp();
                	break;
                case 7:
                	updateUserInfo();
                	break;
                case 8:
                	deleteUser();
                	break;
                case 9:
                	viewPromotion();
                	break;
                case 10:
                	userSearchMenu();
                	break;
                case 0:
                	return;
                default:
                	System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // ===================== 관리자 메뉴 =====================
    public static void adminMenu() {
        while (true) {
            System.out.println("\n--- 관리자 메뉴 ---");
            System.out.println("1. 회원 등급 부여");
            System.out.println("2. 게스트하우스 등록");
            System.out.println("3. 게스트하우스 수정");
            System.out.println("4. 게스트하우스 삭제");
            System.out.println("5. 예약 통계 기능");
            System.out.println("6. 매출 통계 기능");
            System.out.println("0. 뒤로가기");

            int input = scanner.nextInt();
            scanner.nextLine();

            switch (input) {
                case 1:
                	assignMembership();
                	break;
                case 2:
                	registerGuestHouse();
                	break;
                case 3:
                	updateGuestHouse();
                	break;
                case 4:
                	deleteGuestHouse();
                	break;
                case 5:
                	reservationStats();
                	break;
                case 6:
                	salesStats();
                	break;
                case 0:
                	return;
                default:
                	System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // ===================== 고객 기능 구현 =====================
    public static void signUp() {
        try {
        	System.out.println("가입정보 입력");
        	
        	int num = scanner.nextInt();
    		String name = scanner.next();
    		String address = scanner.next();
    		String ssn = scanner.next();
    		char gender = (char) scanner.nextInt();
    		String phone = scanner.next();
    		String grade = scanner.next();
        	
        	Customer customer = new Customer(num, name, address, ssn, gender, phone, grade, new ArrayList<Reservation>());
        	
            cdao.registerCustomer(customer);
            System.out.println("회원가입 완료.");
		}
    	 catch (DuplicateException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void updateUserInfo() {
    	try {
        	System.out.println("수정할 정보 입력");
        	
        	int num = scanner.nextInt();
    		String name = scanner.next();
    		String address = scanner.next();
    		String ssn = scanner.next();
    		char gender = (char) scanner.nextInt();
    		String phone = scanner.next();
    		String grade = scanner.next();
        	
        	Customer customer = new Customer(num, name, address, ssn, gender, phone, grade, new ArrayList<Reservation>());
        	
            cdao.updateCustomer(customer);
            System.out.println("회원 수정 완료");
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void deleteUser() {
    	try {
        	System.out.println("삭제할 회원번호 입력");
        	
        	int num = scanner.nextInt();
            cdao.deleteCustomer(num);
            
            System.out.println("회원가입 완료.");
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void makeReservation() {
    	try {
        	System.out.println("가입정보 입력");
        	
        	// public Reservation(int num, int gusNum, int cusNum, LocalDate checkInDate, LocalDate checkOutDate, int totalPrice, int totalPeople)
        	
        	int num = scanner.nextInt();
    		int gusNum = scanner.nextInt();
    		int cusNum = scanner.nextInt();
    		LocalDate checkInDate = LocalDate.of(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
    		LocalDate checkOutDate = LocalDate.of(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
    		//int cusNum = scanner.nextInt();
    		int people = scanner.nextInt();
        	
        	Reservation reservation = new Reservation(num, gusNum, cusNum, checkInDate, checkOutDate, 0, people);
        	
            cdao.addReservation(reservation);
            System.out.println("예약 추가 완료");
		}
    	 catch (RecordNotFoundException | DuplicateException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void viewReservation() {
    	try {
        	System.out.println("가입정보 입력");
        	
        	int num = scanner.nextInt();
    		String name = scanner.next();
    		String address = scanner.next();
    		String ssn = scanner.next();
    		char gender = (char) scanner.nextInt();
    		String phone = scanner.next();
        	
        	Customer customer = new Customer(num, name, address, ssn, gender, phone, "Bronze", new ArrayList<Reservation>());
        	
            cdao.registerCustomer(customer);
            System.out.println("회원가입 완료.");
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void cancelReservation() {
    	try {
        	System.out.println("가입정보 입력");
        	
        	int num = scanner.nextInt();
    		String name = scanner.next();
    		String address = scanner.next();
    		String ssn = scanner.next();
    		char gender = (char) scanner.nextInt();
    		String phone = scanner.next();
        	
        	Customer customer = new Customer(num, name, address, ssn, gender, phone, "Bronze", new ArrayList<Reservation>());
        	
            cdao.registerCustomer(customer);
            System.out.println("회원가입 완료.");
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void modifyReservation() {
    	try {
        	System.out.println("가입정보 입력");
        	
        	int num = scanner.nextInt();
    		String name = scanner.next();
    		String address = scanner.next();
    		String ssn = scanner.next();
    		char gender = (char) scanner.nextInt();
    		String phone = scanner.next();
        	
        	Customer customer = new Customer(num, name, address, ssn, gender, phone, "Bronze", new ArrayList<Reservation>());
        	
            cdao.registerCustomer(customer);
            System.out.println("회원가입 완료.");
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void writeReview() {
    	try {
        	System.out.println("가입정보 입력");
        	
        	int num = scanner.nextInt();
    		String name = scanner.next();
    		String address = scanner.next();
    		String ssn = scanner.next();
    		char gender = (char) scanner.nextInt();
    		String phone = scanner.next();
        	
        	Customer customer = new Customer(num, name, address, ssn, gender, phone, "Bronze", new ArrayList<Reservation>());
        	
            cdao.registerCustomer(customer);
            System.out.println("회원가입 완료.");
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void viewPromotion() {
    	try {
        	System.out.println("가입정보 입력");
        	
        	int num = scanner.nextInt();
    		String name = scanner.next();
    		String address = scanner.next();
    		String ssn = scanner.next();
    		char gender = (char) scanner.nextInt();
    		String phone = scanner.next();
        	
        	Customer customer = new Customer(num, name, address, ssn, gender, phone, "Bronze", new ArrayList<Reservation>());
        	
            cdao.registerCustomer(customer);
            System.out.println("회원가입 완료.");
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void userSearchMenu() {
        while (true) {
            System.out.println("\n--- 조회 기능 ---");
            System.out.println("1. 지역별 조회");
            System.out.println("2. 남은 인원수 조회");
            System.out.println("3. 특별룸 조회");
            System.out.println("4. 전체 조회");
            System.out.println("5. 요일/가격 기준 조회");
            System.out.println("0. 뒤로가기");

            int input = scanner.nextInt();
            scanner.nextLine();

            switch (input) {
                case 1 -> {
                    System.out.print("조회할 지역을 입력하세요 (예: 서울): ");
                    String region = scanner.nextLine();
                    guestHouses.stream()
                        .filter(s -> s.startsWith(region + "/"))
                        .forEach(System.out::println);
                }

                case 2 -> {
                    System.out.print("최소 남은 인원수를 입력하세요: ");
                    int min = scanner.nextInt();
                    scanner.nextLine();
                    guestHouses.stream()
                        .filter(s -> {
                            String[] tokens = s.split("/");
                            int remaining = Integer.parseInt(tokens[2]);
                            return remaining >= min;
                        })
                        .forEach(System.out::println);
                }

                case 3 -> {
                    guestHouses.stream()
                        .filter(s -> s.contains("/true/"))
                        .forEach(System.out::println);
                }

                case 4 -> {
                    guestHouses.forEach(System.out::println);
                }

                case 5 -> {
                    System.out.print("최대 가격을 입력하세요: ");
                    int maxPrice = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("요일을 입력하세요 (예: 금): ");
                    String day = scanner.nextLine();
                    guestHouses.stream()
                        .filter(s -> {
                            String[] tokens = s.split("/");
                            int price = Integer.parseInt(tokens[4]);
                            String dow = tokens[5];
                            return price <= maxPrice && dow.equals(day);
                        })
                        .forEach(System.out::println);
                }

                case 0 -> { return; }

                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }
    

    // ===================== 관리자 기능 구현 =====================
    public static void assignMembership() {
        try {
            gdao.assignCustomerGrades();
            System.out.println("등급 부여 완료.");
        } catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		} 
    }

    public static void registerGuestHouse() {
    	try {
    		System.out.println("추가할 게스트 하우스의 데이터를 입력하십시오.");
    		int guestHouseId = scanner.nextInt();    		
    		String guestHouseName = scanner.next();
    		String guestHouseAddress = scanner.next();
    		int guestHousePrice = scanner.nextInt();
    		int guestHouseCapacity = scanner.nextInt();
    		String guestHouseService = scanner.next();
    		
    		GuestHouse updateGH = new GuestHouse(guestHouseId, guestHouseName, guestHouseAddress, guestHousePrice, guestHouseCapacity, guestHouseService);
    		
    		gdao.registerGuestHouse(updateGH);
		}
    	 catch (DuplicateException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void updateGuestHouse() {
    	try {
    		System.out.println("수정할 게스트하우스의 아이디를 입력하십시오.");
    		int guestHouseId = scanner.nextInt();
    		
    		System.out.println("게스트하우스를 수정할 데이터를 입력하십시오.");
    		String guestHouseName = scanner.next();
    		String guestHouseAddress = scanner.next();
    		int guestHousePrice = scanner.nextInt();
    		int guestHouseCapacity = scanner.nextInt();
    		
    		GuestHouse updateGH = new GuestHouse(guestHouseId, guestHouseName, guestHouseAddress, guestHousePrice, guestHouseCapacity, null);
    		
    		gdao.updateGuestHouse(updateGH);
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void deleteGuestHouse() {
    	try {
    		System.out.println("삭제할 게스트하우스의 아이디를 입력하십시오.");
    		int guestHouseId = scanner.nextInt();
    		gdao.deleteGuestHouse(guestHouseId);
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void reservationStats() {
    	try {
			System.out.println(gdao.getAllGHReservations().toString());
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }

    public static void salesStats() {
    	try {
			System.out.println(gdao.getTotalSalesPerGuestHouse().toString());
	    	System.out.println(gdao.getTop5GHByRevenue().toString());
		}
    	 catch (RecordNotFoundException | DMLException e) {
 			System.out.println(e.getMessage());
 		}
    }
	
	/**
	 * 예약테이블을 5초마다 가져오는 작업을 수행
	 */
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