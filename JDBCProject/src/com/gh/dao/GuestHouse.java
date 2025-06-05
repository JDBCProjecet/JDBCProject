package com.gh.dao;

import java.util.List;
import java.util.Map;

import com.gh.vo.Customer;

public interface GuestHouse {
	// 1. 게스트하우스별 총 매출 확인 (NTILE 등 분석 함수 기반)
	public Map<String, Integer> getTotalSalesPerGuestHouse();

	// 2. 게스트하우스 등록
	public boolean registerGuestHouse(GuestHouse guestHouse);

	// 3. 게스트하우스 정보 수정
	public boolean updateGuestHouse(GuestHouse guestHouse);

	// 4. 게스트하우스 삭제
	public boolean deleteGuestHouse(int guestHouseId);

	// 5. 날짜별 총 이용객 수 확인
	public Map<String, Integer> getUsageStatsByDate();  // key: 날짜(String), value: count

	// 6. 날짜별 총 매출 확인
	public Map<String, Integer> getSalesStatsByDate(); // key: 날짜(String), value: 매출 총합

	// 7. 매출 기준 Top 5 게스트하우스 조회
	public List<GuestHouse> getTop5GuestHousesByRevenue();

	// 8. 전체 회원 조회
	public List<Customer> getAllCustomers();

	// 9. 회원 등급 부여 (예약 횟수 기반 등급)
	public Map<Integer, String> assignCustomerGrades();  // key: customerId, value: 등급

	// 10. 전체 게스트하우스 예약 요약 (예약 수, 매출 집계)
	public List<Map<String, Object>> summarizeReservationsByGuestHouse();

	// 11. 지역별 게스트하우스 예약 보기 (예약 수, 매출, 순위 포함)
	public List<Map<String, Object>> getReservationStatsByRegion();

}
