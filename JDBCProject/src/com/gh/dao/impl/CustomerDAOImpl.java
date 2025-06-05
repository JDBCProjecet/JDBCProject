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
import java.util.List;

import com.gh.dao.CustomerDAO;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateException;
import com.gh.exception.RecordNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;

import config.ServerInfo;

public class CustomerDAOImpl implements CustomerDAO{
	// 싱글톤
	private static CustomerDAOImpl dao = new CustomerDAOImpl();
	
	public CustomerDAOImpl() {
		System.out.println("Singletone Creating...");
	}
	
	public static CustomerDAOImpl getInstance() {
		return dao;
	}

	// 공통 로직
	private Connection getConnect() throws SQLException {
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASS);
		System.out.println("------데이타베이스 뚜뚜뚜-----");
		return conn;
	}
	

	private void closeAll(PreparedStatement ps, Connection conn) throws DMLException {
		try {
			if(ps != null) ps.close();
			if(conn != null) conn.close();
		} catch (SQLException e) {
			throw new DMLException("DB 연결해제에 실패했습니다.");
		}
	}



	private void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) throws DMLException {
		try {
			if(rs != null) rs.close();
			closeAll(ps, conn);
		} catch (SQLException e) {
			throw new DMLException("DB 연결해제에 실패했습니다.");
		}
	}
	

	public boolean isExist(int num, Connection conn) throws SQLException{
		String query = "SELECT cus_num FROM customer WHERE cus_num=?";
		PreparedStatement ps=conn.prepareStatement(query);
		ps.setInt(1, num);
		ResultSet rs = ps.executeQuery();
		
		return rs.next();//ssn이 있으면 true |없으면 false
	}
	
	public boolean isResExist(int resId, Connection conn) throws SQLException{
		String query = "SELECT res_num FROM reservation WHERE res_num=?";
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setInt(1, resId);
		
		ResultSet rs = ps.executeQuery();
		
		return rs.next(); // 예약이 있으면 true | 없으면 false

	}
	
	/// 숙박기간동안 총 가격 구하는 함수
	private int totalPrice(int guestHouseNum, LocalDate checkInDate, LocalDate checkOutDate) throws RecordNotFoundException, DMLException {
		int totalPrice = 0;
		LocalDate date = checkInDate;
		
		while (date.isBefore(checkOutDate)) {
			totalPrice += calculatePriceByDay(guestHouseNum, date);
			date.plusDays(1);
		}
		
		return totalPrice;
	}

	// 비즈니스 로직
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
	@Override
	public void updateCustomer(Customer customer) throws RecordNotFoundException, DMLException { // 회원정보 수정
		Connection conn = null;
		PreparedStatement ps = null;
		try {
		    conn = getConnect();
		    if (isExist(customer.getNum(), conn)) { // 추가하려는 회원번호가 맞다면
		    	String query = "UPDATE customer SET cus_name = ?, cus_address = ?, cus_ssn = ?, cus_gender = ?, cus_phone = ?, cus_grade = ? WHERE cus_num = ?";
		        ps = conn.prepareStatement(query);
		        ps.setString(1, customer.getName());
		        ps.setString(2, customer.getAddress());
		        ps.setString(3, customer.getSsn());
		        ps.setString(4, String.valueOf(customer.getGender())); 
		        ps.setString(5, customer.getPhone());
		        ps.setString(6, customer.getGrade());
		        ps.setInt(7, customer.getNum());
		        System.out.println(ps.executeUpdate() + "업데이트 성공!!!");
		    }else {
		    	throw new RecordNotFoundException("업데이트 할 대상을 찾지 못했습니다.");
		    }
		    
			}catch (SQLException e) {
			throw  new DMLException("업데이트 중 문제가 생겼습니다.");
			}
	}

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

	@Override
	public void addReservation(Reservation reservation) throws DuplicateException, DMLException {
		String query = "INSERT INTO reservation (res_num, gus_Num, cus_num, res_cindate, res_coutdate, res_tprice, res_tpeople)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;
		PreparedStatement ps = null;
		
		try  {			
			conn = getConnect();
			
			if (isResExist(reservation.getNum(), conn)) {
				throw new SQLIntegrityConstraintViolationException();
			}
			
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, reservation.getNum()); // res_num
			ps.setInt(2, reservation.getGusNum()); // gus_Num
			ps.setInt(3, reservation.getCusNum()); // cus_num
			ps.setDate(4, Date.valueOf(reservation.getCheckInDate())); // res_cindate
			ps.setDate(5, Date.valueOf(reservation.getCheckOutDate())); // res_coutdate
			ps.setInt(6, totalPrice(reservation.getGusNum(), reservation.getCheckInDate(), reservation.getCheckOutDate())); // res_tprice
			ps.setInt(7, reservation.getTotalPeople()); // res_tpeople
			
			System.out.println("예약 " + ps.executeUpdate() + "건 등록 성공...");
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new DuplicateException("");
		} catch (SQLException e) {
			throw new DMLException("예약 등록에 실패하였습니다.");
		} catch (Exception e) {
			throw new DMLException("예약 등록에 실패하였습니다.");
		} finally {
			closeAll(ps, conn);
		}
	}

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

	@Override
	public List<GuestHouse> getGuestHousesByRegion(String Region) throws RecordNotFoundException, DMLException {
		// TODO Auto-generated method stub
		return null;
	}

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
	@Override
	public List<GuestHouse> getAllGuestHouses() throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<GuestHouse> list = new ArrayList<GuestHouse>();
		
		String query = "SELECT gus_num, service_name, gus_name, gus_address, gus_price, gus_capacity FROM guestHouse";
		
		try {
			conn = getConnect();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				GuestHouse gh = new GuestHouse(
						rs.getInt("gus_num"),
						rs.getString("service_name"),
						rs.getString("gus_name"),
						rs.getString("gus_address"),
						rs.getInt("gus_price"),
						rs.getInt("gus_capacity")
						);
				list.add(gh);
			}
			
			
		}catch(SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("기록된거없음");
		}catch(SQLException e) {
			throw new DMLException("오류발생함");
		} finally {
			closeAll(rs, ps, conn);
		}
		
		
		return list;
		}

	@Override
	public int getRemainingCapacity(String guestHouseName) throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int remainingCapacity = 0;
		
		try {
			conn = getConnect();
			String qeury = "SELECT ";
		} catch(SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("해당게하 없음");
		} catch(SQLException e) {
			throw new DMLException("오류발생"+e.getMessage());
		} finally {
			closeAll(rs, ps, conn);
		}
		
		return remainingCapacity;
	}

	@Override
	public List<GuestHouse> getGuestHouses(String service) throws RecordNotFoundException, DMLException {
		//sdfsdfsfd
		return null;
	}

	@Override
	public int calculatePriceByDay(int gusetHouseNum, LocalDate date) throws RecordNotFoundException, DMLException {
		int price = 0;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String query = "SELECT gus_price FROM gusetHouse WHERE gus_name=?";
		
		try  {			
			conn = getConnect();
			ps = conn.prepareStatement(query);
			ps.setInt(1, gusetHouseNum);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				price = rs.getInt("price");
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new RecordNotFoundException("해당 게스트하우스가 존재하지 않습니다.");
		} catch (SQLException e) {
			throw new DMLException("게스트 하우스 가격조회에 실패했습니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		
		// 금, 토요일일 경우 추가요금
		if (date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
			price = price * 12 / 10;
		}		
		
		return price;
	}
}
