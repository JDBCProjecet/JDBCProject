package com.gh.test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
//    	GuestHouseTest test = new GuestHouseTest();
//		Thread t = new Thread(test);
//		t.start();
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
            System.out.println("5. 회원가입");
            System.out.println("6. 회원정보 수정");
            System.out.println("7. 회원 탈퇴");
            System.out.println("8. 프로모션(등급별 할인)");
            System.out.println("9. 조회 기능");
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
                	signUp();
                	break;
                case 6:
                	updateUserInfo();
                	break;
                case 7:
                	deleteUser();
                	break;
                case 8:
                	viewPromotion();
                	break;
                case 9:
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
            System.out.println("5. 회원 전체 조회");
            System.out.println("6. 월별 총 이용객 조회");
            System.out.println("7. 예약 통계 기능");
            System.out.println("8. 매출 통계 기능");
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
                	//회원 전체 조회
                	getAllCustomers();
                	break;
                case 6:
                	//월별 총 이용객 조회
                	getCustomerByMonth();
                	break;
                case 7:
                	reservationStats();
                	break;
                case 8:
                	salesStats();
                	break;
                case 0:
                	return;
                default:
                	System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private static void getCustomerByMonth() {
    	try {
    		Map<String, Integer> usages = gdao.getUsageStatsByDate();
    		
    		usages.forEach((str, num) -> System.out.println(str + ": " + num));
			
		} catch (RecordNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (DMLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void getAllCustomers() {
		try {
			gdao.getAllCustomers().stream()
								  .forEach((c) -> System.out.println(c));
		} catch (RecordNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (DMLException e) {
			System.out.println(e.getMessage());
		}
	}

	// ===================== 고객 기능 구현 =====================
    public static void signUp() {
        try {
            System.out.println("[회원가입]");

            System.out.print("회원번호: ");
            int num = Integer.parseInt(scanner.nextLine());

            System.out.print("이름: ");
            String name = scanner.nextLine();

            System.out.print("주소: ");
            String address = scanner.nextLine();

            System.out.print("주민번호(######-#######): ");
            String ssn = scanner.nextLine();

            System.out.print("성별(M/F): ");
            char gender = scanner.nextLine().toUpperCase().charAt(0);

            System.out.print("전화번호(010-xxxx-xxxx): ");
            String phone = scanner.nextLine();

            System.out.print("등급(BRONZE/GOLD/SILVER): ");
            String grade = scanner.nextLine();

            Customer c = new Customer(num, name, address, ssn, gender, phone, grade);
            cdao.registerCustomer(c);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void updateUserInfo() {
        try {
            System.out.println("[회원정보 수정]");
            System.out.print("회원번호: ");
            int num = Integer.parseInt(scanner.nextLine());

            System.out.print("이름: ");
            String name = scanner.nextLine();

            System.out.print("주소: ");
            String address = scanner.nextLine();

            System.out.print("주민번호: ");
            String ssn = scanner.nextLine();

            System.out.print("성별(M/F): ");
            char gender = scanner.nextLine().toUpperCase().charAt(0);

            System.out.print("전화번호: ");
            String phone = scanner.nextLine();

            System.out.print("등급: ");
            String grade = scanner.nextLine();

            Customer c = new Customer(num, name, address, ssn, gender, phone, grade);
            cdao.updateCustomer(c);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void deleteUser() {
        try {
            System.out.print("[회원 탈퇴] 회원번호 입력: ");
            int num = Integer.parseInt(scanner.nextLine());

            cdao.deleteCustomer(num);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }


    public static void makeReservation() {
        try {
            System.out.println("[예약 등록]");

            System.out.print("예약번호: ");
            int resNum = Integer.parseInt(scanner.nextLine());

            System.out.print("고객번호: ");
            int cusNum = Integer.parseInt(scanner.nextLine());

            System.out.print("게스트하우스번호: ");
            int gusNum = Integer.parseInt(scanner.nextLine());

            System.out.print("입실일 (YYYY-MM-DD): ");
            LocalDate checkIn = LocalDate.parse(scanner.nextLine());

            System.out.print("퇴실일 (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(scanner.nextLine());

            System.out.print("총 인원수: ");
            int people = Integer.parseInt(scanner.nextLine());

            Reservation r = new Reservation(resNum, gusNum, cusNum, checkIn, checkOut, 0, people);
            cdao.addReservation(r);

        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }


    public static void viewReservation() {
        try {
            System.out.print("고객 번호 입력: ");
            int num = Integer.parseInt(scanner.nextLine());

            for (Reservation r : cdao.getReservation(num)) {
                System.out.println(r);
            }
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void cancelReservation() {
        try {
            System.out.print("취소할 예약번호 입력: ");
            int resNum = Integer.parseInt(scanner.nextLine());

            cdao.cancelReservation(resNum);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void modifyReservation() {
        try {
            System.out.print("변경할 예약번호 입력: ");
            int resNum = Integer.parseInt(scanner.nextLine());

            System.out.print("게스트하우스번호: ");
            int gusNum = Integer.parseInt(scanner.nextLine());

            System.out.print("고객번호: ");
            int cusNum = Integer.parseInt(scanner.nextLine());

            System.out.print("입실일 (YYYY-MM-DD): ");
            LocalDate checkIn = LocalDate.parse(scanner.nextLine());

            System.out.print("퇴실일 (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(scanner.nextLine());

            System.out.print("인원수: ");
            int people = Integer.parseInt(scanner.nextLine());

            Reservation r = new Reservation(resNum, gusNum, cusNum, checkIn, checkOut, 0, people);
            cdao.updateReservation(r);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void viewPromotion() {
        try {
            System.out.print("고객번호 입력: ");
            int id = Integer.parseInt(scanner.nextLine());

            int discount = cdao.getDiscountedPrice(id);
            System.out.println("고객님의 할인율: " + discount + "%");
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void userSearchMenu() {
        while (true) {
            System.out.println("\n--- 조회 기능 ---");
            System.out.println("1. 지역별 조회");
            System.out.println("2. 남은 인원수 조회");
            System.out.println("3. 특별룸 조회 (service가 포함된 게스트하우스)");
            System.out.println("4. 전체 조회");
            System.out.println("5. 요일/가격 기준 조회 (금/토 추가요금 포함)");
            System.out.println("0. 뒤로가기");

            try {
                int input = Integer.parseInt(scanner.nextLine());

                switch (input) {
                    case 1 -> { // 지역별 조회
                        System.out.print("조회할 지역을 입력하세요 (예: 서울): ");
                        String region = scanner.nextLine();
                        List<GuestHouse> list = cdao.getRegionGuestHouse(region);
                        list.forEach(System.out::println);
                    }

                    case 2 -> { // 남은 인원수
                        System.out.print("게스트하우스 번호 입력: ");
                        int gusNum = Integer.parseInt(scanner.nextLine());

                        System.out.print("날짜 입력 (yyyy-MM-dd): ");
                        String date = scanner.nextLine();

                        int remain = cdao.getRemainingCapacity(gusNum, java.sql.Date.valueOf(date));
                        System.out.println("남은 인원/총 정원: " + remain);
                    }

                    case 3 -> { // 특별룸 조회: 서비스 컬럼 기준
                        List<GuestHouse> special = cdao.getGuestHouses("party");
                        special.addAll(cdao.getGuestHouses("breakfast"));
                        System.out.println("[특별 서비스 제공 게스트하우스]");
                        special.forEach(System.out::println);
                    }

                    case 4 -> { // 전체 조회
                        List<GuestHouse> all = cdao.getAllGuestHouses();
                        all.forEach(System.out::println);
                    }

                    case 5 -> { // 요일/가격 기준 조회
                        System.out.print("최대 가격 입력: ");
                        int maxPrice = Integer.parseInt(scanner.nextLine());

                        System.out.print("조회할 날짜 입력 (yyyy-MM-dd): ");
                        LocalDate date = LocalDate.parse(scanner.nextLine());

                        List<GuestHouse> all = cdao.getAllGuestHouses();
                        for (GuestHouse gh : all) {
                            int price = cdao.calculatePriceByDay(gh.getNum(), date);
                            if (price <= maxPrice) {
                                System.out.println(gh.getName() + " (" + price + "원)");
                            }
                        }
                    }

                    case 0 -> { return; }

                    default -> System.out.println("잘못된 입력입니다.");
                }

            } catch (Exception e) {
                System.out.println("❌ 오류: " + e.getMessage());
            }
        }
    }

    

    // ===================== 관리자 기능 구현 =====================
    public static void assignMembership() {
        try {
            gdao.assignCustomerGrades();
            System.out.println("✅ 회원 등급 부여 완료!");
        } catch (RecordNotFoundException | DMLException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void registerGuestHouse() {
        try {
            System.out.println("[게스트하우스 등록]");

            System.out.print("번호: ");
            int num = Integer.parseInt(scanner.nextLine());

            System.out.print("이름: ");
            String name = scanner.nextLine();

            System.out.print("주소: ");
            String address = scanner.nextLine();

            System.out.print("가격: ");
            int price = Integer.parseInt(scanner.nextLine());

            System.out.print("수용 인원: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("서비스 (party, breakfast 등): ");
            String service = scanner.nextLine();

            GuestHouse gh = new GuestHouse(num, name, address, price, capacity, service);
            gdao.registerGuestHouse(gh);

        } catch (DuplicateException | DMLException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void updateGuestHouse() {
        try {
            System.out.println("[게스트하우스 수정]");

            System.out.print("수정할 게스트하우스 번호: ");
            int num = Integer.parseInt(scanner.nextLine());

            System.out.print("이름: ");
            String name = scanner.nextLine();

            System.out.print("주소: ");
            String address = scanner.nextLine();

            System.out.print("가격: ");
            int price = Integer.parseInt(scanner.nextLine());

            System.out.print("수용 인원: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("서비스: ");
            String service = scanner.nextLine();

            GuestHouse gh = new GuestHouse(num, name, address, price, capacity, service);
            gdao.updateGuestHouse(gh);

        } catch (RecordNotFoundException | DMLException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void deleteGuestHouse() {
        try {
            System.out.print("삭제할 게스트하우스 번호: ");
            int num = Integer.parseInt(scanner.nextLine());

            gdao.deleteGuestHouse(num);
            System.out.println("✅ 삭제 완료");

        } catch (RecordNotFoundException | DMLException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public static void reservationStats() {
    	while (true) {
            System.out.println("\n--- 예약 통계 기능 ---");
            System.out.println("1. 게스트하우스 전체 예약 조회");
            System.out.println("2. 게스트하우스 지역별 예약 조회");
            System.out.println("0. 뒤로가기");

            try {
                int input = Integer.parseInt(scanner.nextLine());

                switch (input) {
                    case 1:
                    	Map<Integer, List<Reservation>> map = gdao.getAllGHReservations();
                        map.forEach((num, list) -> list.forEach((r) -> System.out.println(r)));
                        break;
                    case 2:
                    	System.out.print("조회할 지역을 입력하세요 (예: 서울): ");
                        String region = scanner.nextLine();
                        Map<String, List<Reservation>> mapRegion = gdao.getRegionGHReservation();
                        mapRegion.get(region).forEach((r) -> System.out.println(r));
                        break;
                    case 0:
                    	return;
                    default:
                    	System.out.println("잘못된 입력입니다.");
                }

            } catch (Exception e) {
                System.out.println("❌ 오류: " + e.getMessage());
            }
        }
    }

    public static void salesStats() {
    	while (true) {
            System.out.println("\n--- 매출 통계 기능 ---");
            System.out.println("1. 날짜 별 총 매출 조회");
            System.out.println("2. 게스트하우스 별 매출등급 조회");
            System.out.println("3. 상위 매출 Top 5 조회");
            System.out.println("0. 뒤로가기");

            try {
                int input = Integer.parseInt(scanner.nextLine());

                switch (input) {
                	case 1:
                		Map<String, Integer> mapSales = gdao.getSalesStatsByDate();
                		mapSales.forEach((name, gh) -> System.out.println(name + " : " + gh));
                        break;
                    case 2:
                    	Map<String, Integer> mapSalesTotal = gdao.getTotalSalesPerGuestHouse();
                    	mapSalesTotal.forEach((name, gh) -> System.out.println(name + " : " + gh));
                		break;
                    case 3:
                    	Map<String, String> mapTop5 = gdao.getTop5GHByRevenue();
                    	mapTop5.forEach((name, gh) -> System.out.println(name + " : " + gh));
                        break;
                    case 0:
                    	return;
                    default:
                    	System.out.println("잘못된 입력입니다.");
                }

            } catch (Exception e) {
                System.out.println("❌ 오류: " + e.getMessage());
            }
        }
    }
	
	/**
	 * 예약테이블을 5초마다 가져오는 작업을 수행
	 */
    @Override
    public void run() {
    	while (true) {
    		try {
    			List<Customer> customers = gdao.getAllCustomers();
    			System.out.println("\n[60초마다 최신 예약 현황 출력]");
    			for (Customer c : customers) {
    				List<Reservation> resList = cdao.getReservation(c.getNum());
    				if (!resList.isEmpty()) {
    					System.out.println("고객: " + c.getName());
    					for (Reservation r : resList) {
    						System.out.println("  " + r);
    					}
    				}
    			}

    			Thread.sleep(60000); // 60초마다 반복

    		} catch (Exception e) {
    			System.out.println("❌ 예약 갱신 오류: " + e.getMessage());
    		}
    	}
    }
    
}