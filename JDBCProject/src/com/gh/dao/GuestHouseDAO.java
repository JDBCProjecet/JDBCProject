package com.gh.dao;

import java.util.List;
import java.util.Map;

import com.gh.exception.DMLException;
import com.gh.exception.DuplicateException;
import com.gh.exception.RecordNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;

public interface GuestHouseDAO {
	// 1. 게스트하우스별 총 매출 확인 (NTILE 등 분석 함수 기반)
	public Map<String, Integer> getTotalSalesPerGuestHouse()throws RecordNotFoundException, DMLException;

	// 2. 게스트하우스 등록
	public void registerGuestHouse(GuestHouse guestHouse)throws DuplicateException, DMLException;

	// 3. 게스트하우스 정보 수정
	public void updateGuestHouse(GuestHouse guestHouse)throws RecordNotFoundException, DMLException;

	// 4. 게스트하우스 삭제
	public void deleteGuestHouse(int guestHouseId)throws RecordNotFoundException, DMLException;

	// 5. 날짜별 총 이용객 수 확인
	public Map<String, Integer> getUsageStatsByDate()throws RecordNotFoundException, DMLException;  
	// key: 날짜(String), value: count

	// 6. 날짜별 총 매출 확인
	public Map<String, Integer> getSalesStatsByDate()throws RecordNotFoundException, DMLException; 
	// key: 날짜(String), value: 매출 총합

	// 7. 매출 기준 Top 5 게스트하우스 조회
	public Map<String, String> getTop5GHByRevenue()throws RecordNotFoundException, DMLException;

	// 8. 전체 회원 조회
	public List<Customer> getAllCustomers()throws RecordNotFoundException, DMLException;

	// 9. 회원 등급 부여 (예약 횟수 기반 등급)
	public void assignCustomerGrades()throws RecordNotFoundException, DMLException; 
	// key: customerId, value: 등급

	// 10. 전체 게스트하우스 예약 요약 (예약 수, 매출 집계)
	public Map<Integer, List<Reservation>> getAllGHReservations()throws RecordNotFoundException, DMLException;

	// 11. 지역별 게스트하우스 예약 보기 (예약 수, 매출, 순위 포함)
	public Map<String, List<Reservation>> getRegionGHReservation()throws RecordNotFoundException, DMLException;

}
