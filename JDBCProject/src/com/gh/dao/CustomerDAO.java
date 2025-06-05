package com.gh.dao;


import java.time.LocalDate;
import java.util.List;

import com.gh.exception.DMLException;
import com.gh.exception.DuplicateException;
import com.gh.exception.RecordNotFoundException;

import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;

public interface CustomerDAO {
	// 1. 회원가입

	public boolean registerCustomer(Customer customer);

	// 2. 회원 정보 수정
	public boolean updateCustomerInfo(Customer customer);

	// 3. 회원 삭제
	public boolean deleteCustomer(int customerId);

	// 4. 예약하기
	public boolean addReservation(Reservation reservation);

	// 5. 예약 수정
	public boolean updateReservation(Reservation reservation);

	// 6. 예약 취소
	public boolean cancelReservation(int reservationId);

	// 7. 예약 조회 (본인 예약 내역)
	public List<Reservation> getMyReservations(int customerId);

	// 8. 게스트하우스 지역별 조회
	public List<GuestHouse> getGuestHousesByRegion(String region);

	// 9. 회원 등급 확인 및 할인 적용
	public int getDiscountedPrice(int customerId) throws DMLException;

	// 10. 게스트하우스 전체 보기
	public List<GuestHouse> getAllGuestHouses();

	// 11. 게스트하우스 남은 인원 확인
	public int getRemainingCapacity(String guestHouseName);

	// 12. 게스트하우스 특성 조건 필터
	public List<GuestHouse> filterGuestHouses(String keyword);

	// 13. 요일에 따른 요금 계산
	public int calculatePriceByDay(String guestHouseName, Date date);


}
