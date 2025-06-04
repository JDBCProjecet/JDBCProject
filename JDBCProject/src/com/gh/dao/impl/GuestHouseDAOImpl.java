package com.gh.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gh.dao.GuestHouseDAO;
import com.gh.exception.DMLException;
import com.gh.exception.DuplicateException;
import com.gh.exception.RecordNotFoundException;
import com.gh.vo.Customer;
import com.gh.vo.GuestHouse;
import com.gh.vo.Reservation;

import config.ServerInfo;

public class GuestHouseDAOImpl implements GuestHouseDAO {
	// 싱글톤
	private static GuestHouseDAOImpl dao = new GuestHouseDAOImpl();

	public GuestHouseDAOImpl() {
		System.out.println("Singletone Creating...");
	}

	public static GuestHouseDAOImpl getInstance() {
		return dao;
	}

	public Connection getConnect() throws SQLException {
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASS);
		System.out.println("------데이타베이스 뚜뚜뚜-----");
		return conn;
	}

	public boolean isExist(int num, Connection conn) throws SQLException {
		String query = "SELECT gus_num FROM guesthouse WHERE gus_num=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, num);
		ResultSet rs = ps.executeQuery();
		return rs.next();// ssn이 있으면 true |없으면 false
	}

	public boolean isCustomerExist(int num, Connection conn) throws SQLException{
		String query = "SELECT cus_num FROM customer WHERE cus_num=?";
		PreparedStatement ps=conn.prepareStatement(query);
		ps.setInt(1, num);
		ResultSet rs = ps.executeQuery();
		
		return rs.next();//ssn이 있으면 true |없으면 false
	}

	public void closeAll(PreparedStatement ps, Connection conn) throws DMLException {
		try {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			throw new DMLException("DB 연결해제에 실패했습니다.");
		}
	}

	public void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) throws DMLException {
		try {
			if (rs != null)
				rs.close();
			closeAll(ps, conn);
		} catch (SQLException e) {
			throw new DMLException("DB 연결해제에 실패했습니다.");
		}
	}

	@Override
	public Map<String, Integer> getTotalSalesPerGuestHouse() throws RecordNotFoundException, DMLException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * gus_num INT PRIMARY KEY, -- 게스트하우스 번호 service_name VARCHAR(10), -- 서비스이름 (FK)
	 * gus_name VARCHAR(10), -- 이름 gus_address VARCHAR(30), -- 주소 gus_price INT, --
	 * 가격 gus_capacity INT, -- 수용인원
	 */

	@Override
	public void registerGuestHouse(GuestHouse guestHouse) throws DuplicateException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			if (!isExist(guestHouse.getNum(), conn)) {
				String query = "INSERT INTO guestHouse(gus_num,gus_name,gus_address, gus_price, gus_capacity) VALUES(?,?,?,?,?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, guestHouse.getNum());
				ps.setString(2, guestHouse.getName());
				ps.setString(3, guestHouse.getAddress());
				ps.setInt(4, guestHouse.getPrice());
				ps.setInt(5, guestHouse.getCapacity());
				System.out.println(ps.executeUpdate() + "개 등록성공");
			} else {
				throw new DuplicateException(guestHouse.getName() + "은 등록되어 있는 게스트하우스입니다.");
			}
		} catch (SQLException e) {
			throw new DMLException("등록중 오류" + e.getMessage());
		} finally {
			closeAll(ps, conn);
		}
	}

	@Override
	public void updateGuestHouse(GuestHouse guestHouse) throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			if (isExist(guestHouse.getNum(), conn)) {
				String query = "UPDATE guestHouse SET gus_name=?, gus_address=?, gus_price=?, gus_capacity=? WHERE gus_num =?";
				ps = conn.prepareStatement(query);
				ps.setString(1, guestHouse.getName());
				ps.setString(2, guestHouse.getAddress());
				ps.setInt(3, guestHouse.getPrice());
				ps.setInt(4, guestHouse.getCapacity());
				ps.setInt(5, guestHouse.getNum());
				System.out.println(ps.executeUpdate() + "명 수정함");
			} else {
				throw new RecordNotFoundException("해당 게하없음");
			}
		} catch (SQLException e) {
			throw new DMLException("수정중 오류");
		} finally {
			closeAll(ps, conn);
		}

	}

	@Override
	public void deleteGuestHouse(int guestHouseId) throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = getConnect();
			if (isExist(guestHouseId, conn)) {
				String query = "DELETE FROM guestHouse WHERE gus_num = ?";
				ps = conn.prepareStatement(query);

				ps.setInt(1, guestHouseId);

				System.out.println(ps.executeUpdate() + "명 삭제함");
				ps.setInt(1, guestHouseId);
				System.out.println(ps.executeUpdate() + "개 삭제함");
			} else {
				throw new RecordNotFoundException("해당 게하없음");
			}

		} catch (SQLException e) {
			throw new DMLException("게하 삭제중 오류");

		} finally {
			closeAll(ps, conn);
		}

	}
	
	// 날짜별 총 이용객 수 확인
	@Override
	public Map<String, Integer> getUsageStatsByDate() throws RecordNotFoundException, DMLException {
		
		return null;
	}
	
	//날짜별 총 매출 확인
	@Override
	public Map<String, Integer> getSalesStatsByDate() throws RecordNotFoundException, DMLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	//매출 기준 Top 5 게스트하우스 조회
	@Override
	public List<GuestHouseDAO> getTop5GHByRevenue() throws RecordNotFoundException, DMLException {
		return null;
	}

	@Override
	public List<Customer> getAllCustomers() throws RecordNotFoundException, DMLException {
		 	Connection conn = null;
		    PreparedStatement ps  = null;
		    ResultSet rs = null;
		    List<Customer> list = new ArrayList<>();
		    try {
		        conn = getConnect();
		        String query = "SELECT cus_num, cus_name, cus_address, cus_ssn, cus_gender, cus_phone, cus_grade FROM customer";
		        ps = conn.prepareStatement(query);
		        rs = ps.executeQuery();
		        while (rs.next()) {
		            Customer c = new Customer(
		                rs.getInt("cus_num"),
		                rs.getString("cus_name"),
		                rs.getString("cus_address"),
		                rs.getString("cus_ssn"),
		                rs.getString("cus_gender").charAt(0),
		                rs.getString("cus_phone"),
		                rs.getString("cus_grade")
		            );
		            list.add(c);
		        }
		        return list;
		    } catch (SQLException e) {
		        throw new DMLException("회원 조회 중 문제가 발생했습니다.");
		    } finally {
		        closeAll(rs, ps, conn);
		    }
		}

	@Override
	public void assignCustomerGrades() throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
	    PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			conn = getConnect();
			// 회원별 집계
			String query = "SELECT cus_num, COUNT(*) AS res_count FROM reservation GROUP BY cus_num";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			// 회원 등급 업데이트
			String updateQuery = "UPDATE customer SET cus_grade = ? WHERE cus_num = ?";
	        ps2 = conn.prepareStatement(updateQuery);
			
	        boolean isUpdated = false;
	        
	        while(rs.next()) {
	        	int cusNum  = rs.getInt("cus_num");
	        	int count = rs.getInt("res_count");
	        	
	        	  String grade;
	              if (count >= 10) grade = "VIP";
	              else if (count >= 5) grade = "GOLD";
	              else if (count >= 1) grade = "SILVER";
	              else grade = "BASIC";
	              
	              ps2.setString(1, grade);
	              ps2.setInt(2, cusNum);
	             System.out.println( ps2.executeUpdate() + "회원 등급별 업데이트 완료"); 
	             
	             isUpdated = true;       	
	        }
	        if(!isUpdated)
	        	throw new RecordNotFoundException("등급을 부여할 회원이 없습니다.");
	        
		}catch(SQLException e) {
			throw new DMLException("회원 등굽 부여 중 오류 발생"+ e.getMessage());
		
		
	}finally {
			closeAll(rs, ps, conn);
			closeAll(ps2, null);
		}
	
	}

	@Override
	public Map<Integer, List<Reservation>> getAllGHReservations() throws RecordNotFoundException, DMLException {
		Map<Integer, List<Reservation>> ghAllResList = new HashMap<>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			
			String query = "SELECT gus_num FROM guestHouse ORDER BY gus_Num";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				ghAllResList.put(rs.getInt("gus_num"), new ArrayList<Reservation>());
			}		
			
			query = "SELECT res_num, gus_Num, cus_num, res_cindate, res_coutdate, res_tprice, res_tpeople FROM reservation ORDER BY gus_Num";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				ghAllResList.get(rs.getInt("gus_Num"))
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

	@Override
	public Map<String, List<Reservation>> getRegionGHReservation() throws RecordNotFoundException, DMLException {
		// TODO Auto-generated method stub
		return null;
	}

}
