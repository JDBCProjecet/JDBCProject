package com.gh.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gh.dao.CustomerDAO;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateException;
import com.gh.exception.RecordNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;

import config.ServerInfo;
/**
 * CustomerDAOImpl: 고객 및 예약 관련 DAO 구현체
 * 데이터베이스 연동을 통해 회원가입, 예약, 수정, 삭제 등의 기능을 수행합니다.
 */
public class CustomerDAOImpl implements CustomerDAO{
	
	private static CustomerDAOImpl dao = new CustomerDAOImpl();
	
	public CustomerDAOImpl() {
		System.out.println("Singletone Creating...");
	}
	/**
     * 싱글톤 인스턴스 반환
     * @return CustomerDAOImpl 객체
     */	
	public static CustomerDAOImpl getInstance() {
		return dao;
	}

	 /**
     * DB 연결을 생성합니다.
     * @return Connection 객체
     * @throws SQLException DB 연결 실패 시
     */
	private Connection getConnect() throws SQLException {
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASS);
		System.out.println("-----DataBase Connecting-----");
		return conn;
	}
	
	 /**
     * 자원 해제
     * @param ps PreparedStatement
     * @param conn Connection
     * @throws DMLException 해제 실패 시
     */
	private void closeAll(PreparedStatement ps, Connection conn) throws DMLException {
		try {
			if(ps != null) ps.close();
			if(conn != null) conn.close();
		} catch (SQLException e) {
			throw new DMLException("DB 연결해제에 실패했습니다.");
		}
	}


	 /**
     * 자원 해제 (ResultSet 포함)
     * @param rs ResultSet
     * @param ps PreparedStatement
     * @param conn Connection
     * @throws DMLException 해제 실패 시
     */
	private void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) throws DMLException {
		try {
			if(rs != null) rs.close();
			closeAll(ps, conn);
		} catch (SQLException e) {
			throw new DMLException("DB 연결해제에 실패했습니다.");
		}
	}
	
	 /**
     * 고객 번호로 존재 여부 확인
     * @param num 고객 번호
     * @param conn DB 연결
     * @return 존재 시 true
     * @throws SQLException SQL 실행 오류
     */
	public boolean isExist(int num, Connection conn) throws SQLException{
		String query = "SELECT cus_num FROM customer WHERE cus_num=?";
		PreparedStatement ps=conn.prepareStatement(query);
		ps.setInt(1, num);
		ResultSet rs = ps.executeQuery();
		
		return rs.next();//ssn이 있으면 true |없으면 false
	}
	  /**
     * 예약 번호로 존재 여부 확인
     * @param resId 예약 번호
     * @param conn DB 연결
     * @return 존재 시 true
     * @throws SQLException SQL 실행 오류
     */
	public boolean isResExist(int resId, Connection conn) throws SQLException{
		String query = "SELECT res_num FROM reservation WHERE res_num=?";
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setInt(1, resId);
		
		ResultSet rs = ps.executeQuery();
		
		return rs.next(); // 예약이 있으면 true | 없으면 false

	}
	
	 /**
     * 숙박 기간 동안 총 가격 계산
     * @param guestHouseNum 게스트하우스 번호
     * @param checkInDate 체크인 날짜
     * @param checkOutDate 체크아웃 날짜
     * @return 총 요금
     * @throws RecordNotFoundException 가격 정보 없음
     * @throws DMLException DB 오류
     */
	private int totalPrice(int guestHouseNum, int customerNum, LocalDate checkInDate, LocalDate checkOutDate) throws RecordNotFoundException, DMLException {
		int totalPrice = 0;
		LocalDate date = checkInDate;
		
		while (date.isBefore(checkOutDate)) {
			totalPrice += calculatePriceByDay(guestHouseNum, date);
			date = date.plusDays(1);
		}
		
		totalPrice = totalPrice * (100 - getDiscountedPrice(customerNum)) / 100;
		
		return totalPrice;
	}
	
	/**
	 * 방이 해당 날짜간에 비어있는지 확인하는 함수
	 * @param checkInDate
	 * @param checkOutDate
	 * @param totalPeople
	 * @param conn
	 * @return
	 * @throws DMLException 
	 */
	private boolean isRoomAble(int gusNum, LocalDate checkInDate, LocalDate checkOutDate, int totalPeople, Connection conn) throws DMLException {
		LocalDate date = checkInDate;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {			
			String query = "SELECT gus_capacity FROM guesthouse WHERE gus_num=?";
			ps = conn.prepareStatement(query);
			ps.setInt(1, gusNum);
			rs = ps.executeQuery();
			
			int capacity = 0;
			if (rs.next()) {
				capacity = rs.getInt("gus_capacity");
			}
			
			while (date.isEqual(checkOutDate) || date.isBefore(checkOutDate)) {
				if (totalPeople > capacity || totalPeople > getRemainingCapacity(gusNum, Date.valueOf(date)) ) { // 방이 꽉찬 날이 있다면 false를 리턴
					System.out.println(date + "에는 수용량을 초과합니다.");
					return false;
				}
				
				date = date.plusDays(1);
			}
			
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			closeAll(rs, ps, null);
		}
		
		return true;
	}

	/**
	 * 고객이 존재하는지 확인하는 함수
	 * @param num
	 * @param conn
	 * @return
	 * @throws DMLException 
	 */
	private boolean isCusExist(int cusNum, Connection conn) throws DMLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String query = "SELECT * FROM customer WHERE cus_num=?";
			
			ps = conn.prepareStatement(query);
			ps.setInt(1, cusNum);
			rs = ps.executeQuery();
			
			return rs.next();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			closeAll(rs, ps, null);
		}
		
		return true;
	}

	/**
	 * 게스트하우스가 존재하는지 확인하는 함수
	 * @param num
	 * @param conn
	 * @return
	 * @throws DMLException 
	 */
	private boolean isGHExist(int gusNum, Connection conn) throws DMLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String query = "SELECT gus_num, gus_name FROM guesthouse WHERE gus_num=?";
			
			ps = conn.prepareStatement(query);
			ps.setInt(1, gusNum);
			rs = ps.executeQuery();
			
			return rs.next();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeAll(rs, ps, null);
		}
		
		return true;
	}

	/**
	 * 고객 정보를 등록합니다.
	 *
	 * @param customer 등록할 고객 객체
	 * @throws DuplicateException 이미 등록된 고객일 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public void registerCustomer(Customer customer) throws DuplicateException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
		    conn = getConnect();
		    if (!isExist(customer.getNum(), conn)) { // 추가하려는 회원번호가 없다면
		        String query = "INSERT INTO customer( cus_num, cus_name, cus_address, cus_ssn, cus_gender, cus_phone) VALUES (?, ?, ?, ?, ?, ?)";
		        ps = conn.prepareStatement(query);
		        ps.setInt(1, customer.getNum());
		        ps.setString(2, customer.getName());
		        ps.setString(3, customer.getAddress());
		        ps.setString(4, customer.getSsn());
		        ps.setString(5, String.valueOf(customer.getGender())); 
		        ps.setString(6, customer.getPhone());

		        System.out.println(ps.executeUpdate() + "명 성공!!!");
		    }
		} catch (SQLIntegrityConstraintViolationException e) {
		    throw new DuplicateException(customer.getName() + "은 등록되어 있는 회원입니다.");
		} catch (SQLException e) {
		    throw new DMLException("등록 중 문제가 생겼습니다.");
		} finally {
		    closeAll(ps, conn);
		}
	}
	/**
     * 고객 정보를 수정합니다.
     *
     * @param customer 수정할 고객 객체
     * @throws RecordNotFoundException 해당 고객이 존재하지 않을 경우
     * @throws DMLException DB 작업 중 오류 발생 시
     */
	@Override
	public void updateCustomer(Customer customer) throws RecordNotFoundException, DMLException { // 회원정보 수정
		Connection conn = null;
		PreparedStatement ps = null;
		try {
		    conn = getConnect();
		    if (isExist(customer.getNum(), conn)) { // 추가하려는 회원번호가 맞다면
		    	String query = "UPDATE customer SET cus_name = ?, cus_address = ?, cus_ssn = ?, cus_gender = ?, cus_phone = ? WHERE cus_num = ?";
		        ps = conn.prepareStatement(query);
		        ps.setString(1, customer.getName());
		        ps.setString(2, customer.getAddress());
		        ps.setString(3, customer.getSsn());
		        ps.setString(4, String.valueOf(customer.getGender())); 
		        ps.setString(5, customer.getPhone());
		        ps.setInt(7, customer.getNum());
		        System.out.println(ps.executeUpdate() + "업데이트 성공!!!");
		    }else {
		    	throw new RecordNotFoundException("업데이트 할 대상을 찾지 못했습니다.");
		    }
		    
			}catch (SQLException e) {
			throw  new DMLException("업데이트 중 문제가 생겼습니다.");
			}
	}
	 /**
     * 고객 정보를 삭제합니다.
     *
     * @param customerId 삭제할 고객 번호
     * @throws RecordNotFoundException 해당 고객이 존재하지 않을 경우
     * @throws DMLException DB 작업 중 오류 발생 시
     */
	@Override
	public void deleteCustomer(int customerId) throws RecordNotFoundException, DMLException { // 회원 삭제 customerid가 num 이여야함
	    Connection conn = null;
	    PreparedStatement ps = null;
	    try {
	        conn = getConnect();
	        if (isExist(customerId, conn)) {
	            String query = "DELETE FROM customer WHERE cus_num=?";
	            ps = conn.prepareStatement(query);
	            ps.setInt(1, customerId);
	            System.out.println(ps.executeUpdate() + "명 삭제 완료");
	        } else {
	            throw new RecordNotFoundException("해당 회원을 찾지 못했습니다.....");
	        }
	    } catch (SQLException e) {
	        throw new DMLException("삭제 중 문제가 생겼습니다.");
	    } finally {
	        closeAll(ps, conn);
	    }
	}
	/**
	 * 예약 정보를 등록합니다.
	 *
	 * @param reservation 등록할 예약 객체
	 * @throws DuplicateException 예약 번호가 이미 존재할 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public void addReservation(Reservation reservation) throws DuplicateException, RecordNotFoundException, DMLException {
		String query = "INSERT INTO reservation (res_num, gus_Num, cus_num, res_cindate, res_coutdate, res_tprice, res_tpeople)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;
		PreparedStatement ps = null;
		
		try  {			
			conn = getConnect();			

			// 해당하는 예약번호가 존재하는 지 
			if (isResExist(reservation.getNum(), conn)) {
				throw new DuplicateException("예약번호가 존재합니다.");
			}		
			
			// 해당하는 게스트하우스가 존재하는 지
			if (!isGHExist(reservation.getGusNum(), conn)) {
				throw new RecordNotFoundException("게스트하우스가 존재하지 않습니다.");
			}
			
			// 해당하는 고객정보가 존재하는지
			if (!isCusExist(reservation.getCusNum(), conn)) {
				throw new RecordNotFoundException("고객이 존재하지 않습니다.");
			}
			
			// 해당 날짜 순서가 정상적인 지
			if (!reservation.getCheckInDate().isBefore(reservation.getCheckOutDate())) {
				throw new DMLException("날짜 입력이 잘못되었습니다.");
			}
			
			// 해당 날짜에 게스트하우스가 비어있는지
			if (!isRoomAble(reservation.getGusNum(), reservation.getCheckInDate(), reservation.getCheckOutDate(), reservation.getTotalPeople(), conn)) {
				throw new DMLException("방이 꽉차있습니다.");
			}
			
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, reservation.getNum()); // res_num
			ps.setInt(2, reservation.getGusNum()); // gus_Num
			ps.setInt(3, reservation.getCusNum()); // cus_num
			ps.setDate(4, Date.valueOf(reservation.getCheckInDate())); // res_cindate
			ps.setDate(5, Date.valueOf(reservation.getCheckOutDate())); // res_coutdate
			ps.setInt(6, totalPrice(reservation.getGusNum(), reservation.getCusNum(), reservation.getCheckInDate(), reservation.getCheckOutDate())); // res_tprice
			ps.setInt(7, reservation.getTotalPeople()); // res_tpeople
			
			System.out.println("예약 " + ps.executeUpdate() + "건 등록 성공...");
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new DuplicateException();
		} catch (SQLException e) {
			throw new DMLException("예약 등록에 실패하였습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new DMLException("예약 등록에 실패하였습니다.");
		} finally {
			closeAll(ps, conn);
		}
	}
	/**
     * 고객 정보를 수정합니다.
     *
     * @param customer 수정할 고객 객체
     * @throws RecordNotFoundException 해당 고객이 존재하지 않을 경우
     * @throws DMLException DB 작업 중 오류 발생 시
     */
	@Override
	public void updateReservation(Reservation reservation) throws RecordNotFoundException, DMLException {
		String query = "UPDATE reservation SET res_cindate=?, res_coutdate=?, res_tpeople=? WHERE res_num=?";
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {			
			conn = getConnect();
			
			if (!isResExist(reservation.getNum(), conn)) {
				throw new SQLIntegrityConstraintViolationException();
			}
			
			// 해당하는 게스트하우스가 존재하는 지
			if (!isGHExist(reservation.getGusNum(), conn)) {
				throw new RecordNotFoundException("게스트하우스가 존재하지 않습니다.");
			}
			
			// 해당하는 고객정보가 존재하는지
			if (!isCusExist(reservation.getCusNum(), conn)) {
				throw new RecordNotFoundException("고객이 존재하지 않습니다.");
			}
			
			// 해당 날짜 순서가 정상적인 지
			if (!reservation.getCheckInDate().isBefore(reservation.getCheckOutDate())) {
				throw new DMLException("날짜 입력이 잘못되었습니다.");
			}
			
			// 해당 날짜에 게스트하우스가 비어있는지
			if (!isRoomAble(reservation.getGusNum(), reservation.getCheckInDate(), reservation.getCheckOutDate(), reservation.getTotalPeople(), conn)) {
				throw new DMLException("방이 꽉차있습니다.");
			}
			
			
			
			ps = conn.prepareStatement(query);
			
			ps.setDate(1, Date.valueOf(reservation.getCheckInDate()));
			ps.setDate(2, Date.valueOf(reservation.getCheckOutDate()));
			ps.setInt(3, reservation.getTotalPeople());
			ps.setInt(4, reservation.getNum());
			
			System.out.println("예약 " + ps.executeUpdate() + "건 수정 성공...");
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("");
		} catch (SQLException e) {
			throw new DMLException("예약 수정에 실패하였습니다.");
		} finally {
			closeAll(ps, conn);
		}
	}
	/**
	 * 예약 정보를 취소합니다.
	 *
	 * @param reservationId 취소할 예약 번호
	 * @throws RecordNotFoundException 해당 예약이 존재하지 않을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public void cancelReservation(int reservationId) throws RecordNotFoundException, DMLException {
		String query = "DELETE FROM reservation WHERE res_num=?";
		Connection conn = null;
		PreparedStatement ps = null;
		
		try  {			
			conn = getConnect();
			
			if (!isResExist(reservationId, conn)) {
				throw new SQLIntegrityConstraintViolationException();
			}
			
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, reservationId);
			
			System.out.println("예약 " + ps.executeUpdate() + "건 삭제 성공...");
			
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("해당 예약은 이미 존재하지 않습니다.");
		} catch (SQLException e) {
			throw new DMLException("예약 삭제에 실패하였습니다.");
		} finally {
			closeAll(ps, conn);
		}
	}
	/**
	 * 고객의 예약 내역을 조회합니다.
	 *
	 * @param customerId 고객 번호
	 * @return 예약 목록 리스트
	 * @throws RecordNotFoundException 예약 정보가 없을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public List<Reservation> getReservation(int customerId) throws RecordNotFoundException, DMLException {
		List<Reservation> resList = new ArrayList<>();
		String query = "SELECT res_num, gus_Num, cus_num, res_cindate, res_coutdate, res_tprice, res_tpeople FROM reservation WHERE cus_num=?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try  {			
			conn = getConnect();
			ps = conn.prepareStatement(query);
			ps.setInt(1, customerId);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				resList.add(new Reservation(rs.getInt("res_num"), rs.getInt("gus_Num"), rs.getInt("cus_num"), 
						rs.getDate("res_cindate").toLocalDate(), rs.getDate("res_coutdate").toLocalDate(), rs.getInt("res_tprice"), rs.getInt("res_tpeople")));
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("등록된 예약이 없습니다.");
		} catch (SQLException e) {
			throw new DMLException("예약 등록에 실패하였습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		
		return resList;
	}
	/**
	 * 지역별로 게스트하우스 예약 목록을 조회합니다.
	 *
	 * @return 지역명과 예약 리스트를 매핑한 맵
	 * @throws RecordNotFoundException 해당하는 게스트하우스가 존재하지 않을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
    @Override
    public Map<String, List<Reservation>> getRegionGHReservation() throws RecordNotFoundException, DMLException {
        Map<String, List<Reservation>> ghAllResList = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getConnect();
            
            // {지역: 예약 리스트}로 반환
            // 1. 
            String query = "SELECT gus_num, gus_address FROM guestHouse";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            Map<Integer, String> ghAddressMap = new HashMap<>(); 
            while (rs.next()) {
                ghAllResList.put(rs.getString("gus_address"), new ArrayList<Reservation>());
                ghAddressMap.put(rs.getInt("gus_num"), rs.getString("gus_address"));
            }        
            
            query = "SELECT res_num, gus_Num, cus_num, res_cindate, res_coutdate, res_tprice, res_tpeople FROM reservation";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                ghAllResList.get(ghAddressMap.get(rs.getInt("gus_Num")))
                            .add(new Reservation(rs.getInt("res_num"), rs.getInt("gus_Num"), rs.getInt("cus_num"), 
                                    rs.getDate("res_cindate").toLocalDate(), rs.getDate("res_coutdate").toLocalDate(), 
                                    rs.getInt("res_tprice"), rs.getInt("res_tpeople")));
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RecordNotFoundException("해당하는 게스트하우스가 존재하지 않음.");
        } catch (SQLException e) {
            throw new DMLException("전체 게스트하우스 예약 조회 실패함.");
        }
        
        return ghAllResList;
    }
    /**
     * 고객 등급에 따라 할인율을 반환합니다.
     *
     * @param customerId 고객 ID
     * @return 할인율 (VIP:30, GOLD:20, SILVER:15, BRONZE:10)
     * @throws DMLException DB 작업 중 오류 발생 시
     */
	@Override
	public int  getDiscountedPrice(int customerId) throws DMLException {
	    int discountRate = 10; // 기본값 (BRONZE)
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        conn = getConnect(); // 커넥션 메서드
	        String sql = "SELECT cus_grade FROM customer WHERE cus_num = ?";
	        ps = conn.prepareStatement(sql);
	        ps.setInt(1, customerId);
	        rs = ps.executeQuery();

	        if (rs.next()) {
	            String grade = rs.getString("cus_grade");

	            // 등급 문자열에 따라 할인율 정함
	            switch (grade.toUpperCase()) {
	                case "VIP": discountRate = 30; break;
	                case "GOLD": discountRate = 20; break;
	                case "SILVER": discountRate = 15; break;
	                case "BRONZE": default: discountRate = 10; break;
	            }
	        }

	    } catch (SQLException e) {
	        throw new DMLException("할인율 계산중 오류"); // 필요시 로깅
	    } finally {
	        closeAll(rs, ps, conn); 
	    }

	    return discountRate;
	}
	/**
	 * 모든 게스트하우스 정보를 조회합니다.
	 *
	 * @return 게스트하우스 리스트
	 * @throws RecordNotFoundException 기록이 없을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public List<GuestHouse> getAllGuestHouses() throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<GuestHouse> list = new ArrayList<GuestHouse>();

		String query = "SELECT gus_num, gus_name, gus_address, gus_price, gus_capacity, gus_service FROM guestHouse";

		try {
			conn = getConnect();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				GuestHouse gh = new GuestHouse(rs.getInt("gus_num"),
						rs.getString("gus_name"),
						rs.getString("gus_address"),
						rs.getInt("gus_price"),
						rs.getInt("gus_capacity"),
						rs.getString("gus_service")
						);
				list.add(gh);
			}

		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("기록된거없음");
		} catch (SQLException e) {
			throw new DMLException("오류발생함");
		} finally {
			closeAll(rs, ps, conn);
		}

		return list;
	}

	/**
	 * 특정 날짜에 해당 게스트하우스의 남은 수용 가능 인원을 조회합니다.
	 *
	 * @param gusNum 게스트하우스 번호
	 * @param date 조회할 날짜
	 * @return "남은인원/전체수용인원" 형식의 문자열
	 * @throws RecordNotFoundException 해당 게스트하우스가 존재하지 않을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public int getRemainingCapacity(int gusNum, Date date) throws RecordNotFoundException, DMLException {
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    
	    int remainingCapacity = 0;

	    try {
	        conn = getConnect();
	        String query =
	        	"""
	            SELECT g.gus_capacity - IFNULL(SUM(r.res_tpeople), 0) AS remaining_capacity, g.gus_capacity
	            FROM guesthouse g
	            LEFT JOIN reservation r ON g.gus_num = r.gus_num
	            AND ? BETWEEN r.res_cindate AND r.res_coutdate
	            WHERE g.gus_num = ?
	            GROUP BY g.gus_capacity
	            """;

	        ps = conn.prepareStatement(query);
	        ps.setDate(1, date);
	        ps.setInt(2, gusNum);
	        rs = ps.executeQuery();

	        if (rs.next()) {
	            remainingCapacity = rs.getInt("remaining_capacity");
	        } else {
	            throw new RecordNotFoundException("해당 게스트하우스가 존재하지 않거나 예약 내역이 없습니다.");
	        }

	    } catch (SQLException e) {
	        throw new DMLException("오류 발생: " + e.getMessage());
	    } finally {
	        closeAll(rs, ps, conn);
	    }

	    return remainingCapacity;
	}

	/**
	 * 특정 서비스를 제공하는 게스트하우스 리스트를 반환합니다.
	 *
	 * @param service 서비스 이름 (예: "party", "breakfast")
	 * @return 해당 서비스 제공 게스트하우스 리스트
	 * @throws RecordNotFoundException 해당 서비스가 없을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public List<GuestHouse> getGuestHouses(String service) throws RecordNotFoundException, DMLException {
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    List<GuestHouse> list = new ArrayList<>();

	    try {
	        conn = getConnect();
	        String query = "SELECT gus_num, gus_name, gus_address, gus_price, gus_capacity, gus_service FROM guestHouse WHERE gus_service = ?";
	        ps = conn.prepareStatement(query);
	        ps.setString(1, service); // "party" 또는 "breakfast"
	        rs = ps.executeQuery();

	        while (rs.next()) {
	            GuestHouse gh = new GuestHouse(
	                rs.getInt("gus_num"),
	                rs.getString("gus_name"),
	                rs.getString("gus_address"),
	                rs.getInt("gus_price"),
	                rs.getInt("gus_capacity"),
	                rs.getString("gus_service")
	            );
	            list.add(gh);
	        }

	        if (list.isEmpty()) {
	            throw new RecordNotFoundException("해당 서비스 '" + service + "'를 제공하는 게스트하우스가 없습니다.");
	        }

	    } catch (SQLException e) {
	        throw new DMLException(e.getMessage());
	    } finally {
	        closeAll(rs, ps, conn);
	    }

	    return list;
	}
	/**
	 * 특정 날짜의 게스트하우스 요금을 계산합니다. (주말 추가요금 포함)
	 *
	 * @param gusetHouseNum 게스트하우스 번호
	 * @param date 조회 날짜
	 * @return 계산된 요금
	 * @throws RecordNotFoundException 해당 게스트하우스가 없을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public int calculatePriceByDay(int gusetHouseNum, LocalDate date) throws RecordNotFoundException, DMLException {
		int price = 0;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String query = "SELECT gus_price FROM guesthouse WHERE gus_num=?";
		
		try  {			
			conn = getConnect();
			ps = conn.prepareStatement(query);
			ps.setInt(1, gusetHouseNum);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				price = rs.getInt("gus_price");
			}	
			
			// 금, 토요일일 경우 추가요금
			if (date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
				price = price * 12 / 10;
			
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("해당 게스트하우스가 존재하지 않습니다.");
		} catch (SQLException e) {
			throw new DMLException("게스트 하우스 가격조회에 실패했습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}		
		
		return price;
	}

	/**
	 * 지역명을 기준으로 게스트하우스를 검색합니다.
	 *
	 * @param region 지역명 (예: "서울", "부산")
	 * @return 해당 지역의 게스트하우스 리스트
	 * @throws RecordNotFoundException 해당 지역 게스트하우스가 없을 경우
	 * @throws DMLException DB 작업 중 오류 발생 시
	 */
	@Override
	public List<GuestHouse> getRegionGuestHouse(String region) throws RecordNotFoundException, DMLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<GuestHouse> RegionGuestHouse = new ArrayList<>();
		
		try {
			conn = getConnect();
			String query = """
					SELECT gus_num, gus_name, gus_address, gus_price, gus_capacity, gus_service 
					FROM guestHouse
					WHERE substr(gus_address,1,2) = ?
					""";
			ps=conn.prepareStatement(query);
			ps.setString(1, region);
			rs=ps.executeQuery();
			while(rs.next()) {
				RegionGuestHouse.add(new GuestHouse(
						   rs.getInt("gus_num"),
			                rs.getString("gus_name"),
			                rs.getString("gus_address"),
			                rs.getInt("gus_price"),
			                rs.getInt("gus_capacity"),
			                rs.getString("gus_service")
			            ));
			}
			if(RegionGuestHouse.isEmpty()) {
				throw new RecordNotFoundException();
			}
		}catch(SQLException e) {
			throw new DMLException();
		}finally{
			closeAll(rs,ps,conn);
		}
		return RegionGuestHouse;
	}
}