package com.gh.test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.gh.dao.GuestHouseDAO;
import com.gh.dao.impl.CustomerDAOImpl;
import com.gh.dao.impl.GuestHouseDAOImpl;
import com.gh.exception.DMLException;
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
                case 1 -> assignMembership();
                case 2 -> registerGuestHouse();
                case 3 -> updateGuestHouse();
                case 4 -> deleteGuestHouse();
                case 5 -> reservationStats();
                case 6 -> salesStats();
                case 0 -> { return; }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // ===================== 고객 기능 구현 =====================
    public static void signUp() {
        System.out.print("회원 ID: ");
        String id = scanner.nextLine();
        System.out.print("비밀번호: ");
        String pw = scanner.nextLine();
        users.put(id, pw);
        System.out.println("회원가입 완료.");
    }

    public static void updateUserInfo() {
        System.out.print("변경할 회원 ID: ");
        String id = scanner.nextLine();
        if (!users.containsKey(id)) {
            System.out.println("존재하지 않는 ID입니다.");
            return;
        }
        System.out.print("새 비밀번호: ");
        String newPw = scanner.nextLine();
        users.put(id, newPw);
        System.out.println("비밀번호 변경 완료.");
    }

    public static void deleteUser() {
        System.out.print("탈퇴할 회원 ID: ");
        String id = scanner.nextLine();
        users.remove(id);
        System.out.println("회원 탈퇴 완료.");
    }

    public static void makeReservation() {
        System.out.print("예약자 이름: ");
        String name = scanner.nextLine();
        System.out.print("숙소명: ");
        String house = scanner.nextLine();
        String res = name + "님의 예약 - " + house;
        reservations.add(res);
        System.out.println("예약 완료: " + res);
    }

    public static void viewReservation() {
        System.out.println("예약 목록:");
        for (String res : reservations) {
            System.out.println("- " + res);
        }
    }

    public static void cancelReservation() {
        System.out.print("취소할 예약자 이름: ");
        String name = scanner.nextLine();
        reservations.removeIf(r -> r.startsWith(name));
        System.out.println("예약 취소 완료.");
    }

    public static void modifyReservation() {
        System.out.print("변경할 예약자 이름: ");
        String name = scanner.nextLine();
        reservations.removeIf(r -> r.startsWith(name));
        System.out.print("새 숙소명: ");
        String newHouse = scanner.nextLine();
        reservations.add(name + "님의 예약 - " + newHouse);
        System.out.println("예약 변경 완료.");
    }

    public static void writeReview() {
        System.out.print("후기를 작성할 숙소명: ");
        String house = scanner.nextLine();
        System.out.print("후기 내용: ");
        String review = scanner.nextLine();
        System.out.println("후기 저장 완료: [" + house + "] " + review);
    }

    public static void viewPromotion() {
        System.out.println("등급별 할인 안내:");
        System.out.println("Silver: 5%, Gold: 10%, VIP: 15%");
    }

    public static void userSearchMenu() {
        System.out.println("\n--- 조회 기능 ---");
        System.out.println("1. 지역별 조회");
        System.out.println("2. 남은 인원수 조회");
        System.out.println("3. 특별룸 조회");
        System.out.println("4. 전체 조회");
        System.out.println("5. 요일/가격 기준 조회");

        int input = scanner.nextInt();
        scanner.nextLine();
        System.out.println("해당 조건에 맞는 숙소 조회 결과 (모의 출력)");
    }

    // ===================== 관리자 기능 구현 =====================
    public static void assignMembership() {
        System.out.print("회원 ID: ");
        String id = scanner.nextLine();
        System.out.print("등급 입력 (Silver/Gold/VIP): ");
        String grade = scanner.nextLine();
        userGrades.put(id, grade);
        System.out.println("등급 부여 완료.");
    }

    public static void registerGuestHouse() {
        System.out.print("등록할 숙소명: ");
        String name = scanner.nextLine();
        guestHouses.add(name);
        System.out.println("등록 완료.");
    }

    public static void updateGuestHouse() {
    	try {
    		System.out.println("수정할 방의 아이디를 입력하십시오.");
    		int guestHouseId = scanner.nextInt();
    		
    		System.out.println("방을 수정할 데이터를 입력하십시오.");
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
    		System.out.println("삭제할 방의 아이디를 입력하십시오.");
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