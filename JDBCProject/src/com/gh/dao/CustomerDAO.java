package com.gh.dao;

import java.sql.Date;
import java.util.List;

import com.gh.exception.DMLException;
import com.gh.exception.DuplicateException;
import com.gh.exception.RecordNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;

public interface CustomerDAO {
	// 1. 회원가입
	public void registerCustomer(Customer customer)throws DuplicateException, DMLException;

	// 2. 회원 정보 수정
	public void updateCustomer(Customer customer)throws RecordNotFoundException, DMLException;

	// 3. 회원 삭제
	public void deleteCustomer(int customerId)throws RecordNotFoundException, DMLException;

	// 4. 예약하기
	public void addReservation(Reservation reservation)throws DuplicateException, DMLException;

	// 5. 예약 수정
	public void updateReservation(Reservation reservation)throws RecordNotFoundException, DMLException;

	// 6. 예약 취소
	public void cancelReservation(int reservationId)throws RecordNotFoundException, DMLException;

	// 7. 예약 조회 (본인 예약 내역)
	public List<Reservation> getAllReservation(int customerId)throws RecordNotFoundException, DMLException;

	// 8. 게스트하우스 지역별 조회
	public List<GuestHouse> getGuestHousesByRegion(String Region)throws RecordNotFoundException, DMLException;

	// 9. 회원 등급 확인 및 할인 적용
	public int getCustomerGrade(int customerId);

	// 10. 게스트하우스 전체 보기
	public List<GuestHouse> getAllGuestHouses()throws RecordNotFoundException, DMLException;

	// 11. 게스트하우스 남은 인원 확인
	public int getRemainingCapacity(String guestHouseName)throws RecordNotFoundException, DMLException;

	// 12. 게스트하우스 특성 조건 검색
	public List<GuestHouse> getGuestHouses(String service)throws RecordNotFoundException, DMLException;

	// 13. 요일에 따른 요금 계산
	public int calculatePriceByDay(String guestHouseName, Date date)throws RecordNotFoundException, DMLException;


}
