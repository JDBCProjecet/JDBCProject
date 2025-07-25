package com.gh.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
		System.out.println("------DataBase Connecting-----");
		return conn;
	}

	public boolean isExist(int num, Connection conn) throws SQLException {
		String query = "SELECT gus_num FROM guesthouse WHERE gus_num=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, num);
		ResultSet rs = ps.executeQuery();
		return rs.next();// ssn이 있으면 true |없으면 false
	}

	public boolean isCustomerExist(int num, Connection conn) throws SQLException {
		String query = "SELECT cus_num FROM customer WHERE cus_num=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, num);
		ResultSet rs = ps.executeQuery();

		return rs.next();// ssn이 있으면 true |없으면 false
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		// 결과를 담을 맵: 게스트하우스 이름 → 매출 등급(1~4)
		Map<String, Integer> result = new LinkedHashMap<>();

		try {
			conn = getConnect(); // DB 연결

			// SQL: 각 게스트하우스의 총 매출을 구한 뒤, 매출순으로 NTILE(4)로 등급 부여
			String query = """
					    SELECT
					        g.gus_name AS guesthouse_name,                         -- 게스트하우스 이름
					        SUM(r.res_tprice) AS total_sales,                     -- 총 매출 (reservation 테이블의 가격 합계)
					        NTILE(4) OVER (ORDER BY SUM(r.res_tprice) DESC) AS sales_rank -- 매출 순서대로 1~4등급 나눔
					    FROM guesthouse g
					    JOIN reservation r ON g.gus_num = r.gus_num              -- 게스트하우스와 예약 테이블 조인
					    GROUP BY g.gus_name                                       -- 게스트하우스별로 그룹화
					""";

			ps = conn.prepareStatement(query); // SQL 실행 준비
			rs = ps.executeQuery(); // SQL 실행 → 결과 받아오기

			// 결과 ResultSet을 순회하며 Map에 저장
			while (rs.next()) {
				String name = rs.getString("guesthouse_name"); // 게스트하우스 이름
				int rank = rs.getInt("sales_rank"); // NTILE로 계산된 등급 (1~4)

				result.put(name, rank); // 결과 맵에 저장 (이름 → 등급)
			}

			// 결과가 아무것도 없으면 예외 발생
			if (result.isEmpty()) {
				throw new RecordNotFoundException("게스트하우스 매출 정보가 없습니다.");
			}

			return result; // 최종 결과 반환

		} catch (SQLException e) {
			// DB 작업 중 예외 발생 시 DMLException으로 래핑하여 던짐
			throw new DMLException("게스트 하우스 매출 등급 조회 중 오류 발생: " + e.getMessage());
		} finally {
			// 리소스 정리
			closeAll(rs, ps, conn);
		}
	}

	/*
	 * 1. DB 연결 2. 게스트하우스와 예약 테이블 JOIN 3. 게스트하우스별 총 매출 계산 (SUM) 4. NTILE(4)로 등급 나누기
	 * (1: 상위 ~ 4: 하위) 5. 결과를 Map<게스트하우스 이름, 등급>에 저장 6. 반환 or 예외 처리
	 * 
	 */

	@Override
	public void registerGuestHouse(GuestHouse guestHouse) throws DuplicateException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = getConnect();
			if (!isExist(guestHouse.getNum(), conn)) {
				String query = "INSERT INTO guestHouse(gus_num, gus_name, gus_address, gus_price, gus_capacity, gus_service) VALUES(?,?,?,?,?,?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, guestHouse.getNum());
				ps.setString(2, guestHouse.getName());
				ps.setString(3, guestHouse.getAddress());
				ps.setInt(4, guestHouse.getPrice());
				ps.setInt(5, guestHouse.getCapacity());
				ps.setString(6, guestHouse.getService());
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
			conn = getConnect();

			if (isExist(guestHouse.getNum(), conn)) {
				String query = "UPDATE guestHouse SET gus_name=?, gus_address=?, gus_price=?, gus_capacity=?, gus_service=? WHERE gus_num =?";
				ps = conn.prepareStatement(query);
				ps.setString(1, guestHouse.getName());
				ps.setString(2, guestHouse.getAddress());
				ps.setInt(3, guestHouse.getPrice());
				ps.setInt(4, guestHouse.getCapacity());
				ps.setString(5, guestHouse.getService());
				ps.setInt(6, guestHouse.getNum());
				System.out.println(ps.executeUpdate() + guestHouse.getName() + "정보 업데이트완료");
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

				System.out.println(ps.executeUpdate() + "개 게하 삭제완료");
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Integer> customersByDay = new LinkedHashMap<>();

		try {
			conn = getConnect();
			String sql = """
					SELECT res_cindate, SUM(res_tpeople) AS total_people
					FROM reservation
					GROUP BY res_cindate
					ORDER BY 1
					""";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String day = rs.getString("res_cindate");
				int totalPeople = rs.getInt("total_people");
				customersByDay.put(day, totalPeople);

			}
		} catch (SQLException e) {
			throw new DMLException("잘못된 쿼리문입니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		return customersByDay;
	}

	// 날짜별 총 매출 확인
	@Override
	public Map<String, Integer> getSalesStatsByDate() throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Integer> salesByDay = new LinkedHashMap<>();

		try {
			conn = getConnect();
			String sql = """
					SELECT res_cindate, SUM(res_tprice) AS total_price
					FROM reservation
					GROUP BY res_cindate
					ORDER BY 1
					""";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String day = rs.getString("res_cindate");
				int totalPeople = rs.getInt("total_price");
				salesByDay.put(day, totalPeople);
			}
		} catch (SQLException e) {
			throw new DMLException("잘못된 쿼리문입니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		return salesByDay;
	}

	// 매출 기준 Top 5 게스트하우스 조회
	@Override
	public Map<String, String> getTop5GHByRevenue() throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, String> ghRank = new LinkedHashMap<>();
		try {
			conn = getConnect();
			String sql = """
					SELECT
					    gus_num,
					    gus_name,
					    total_price,
					    ranking
					FROM ( SELECT
						        gus_num,
						        gus_name,
						        SUM(r.res_tprice) AS total_price,
						        RANK() OVER (ORDER BY SUM(r.res_tprice) DESC) AS ranking
						    FROM reservation r
						    JOIN guesthouse g USING (gus_num)
						    GROUP BY gus_num, gus_name
						) AS ranked
						WHERE ranking <= 5
						""";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ghRank.put(rs.getString("ranking") + "등",
						rs.getString("gus_name") + ", 총 매출: " + rs.getInt("total_price"));

			}
		} catch (SQLException e) {
			throw new DMLException("잘못된 쿼리문입니다.");
		} finally {
			closeAll(rs, ps, conn);
		}
		return ghRank;
	}

	@Override
	public List<Customer> getAllCustomers() throws RecordNotFoundException, DMLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Customer> list = new ArrayList<>();
		try {
			conn = getConnect();
			String query = "SELECT cus_num, cus_name, cus_address, cus_ssn, cus_gender, cus_phone, cus_grade FROM customer";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				Customer c = new Customer(rs.getInt("cus_num"), rs.getString("cus_name"), rs.getString("cus_address"),
						rs.getString("cus_ssn"), rs.getString("cus_gender").charAt(0), rs.getString("cus_phone"),
						rs.getString("cus_grade"));
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

			// 1. 회원별 이용횟수와 현재 등급 조회
			String query = """
				SELECT c.cus_name,c.cus_num, COUNT(r.res_num) AS res_count, c.cus_grade
				FROM customer c
				LEFT JOIN reservation r ON c.cus_num = r.cus_num
				GROUP BY c.cus_num, c.cus_grade
			""";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			// 2. 등급 업데이트 쿼리
			String updateQuery = "UPDATE customer SET cus_grade = ? WHERE cus_num = ?";
			ps2 = conn.prepareStatement(updateQuery);

			boolean isUpdated = false;

			while (rs.next()) {
				int cusNum = rs.getInt("cus_num");
				int count = rs.getInt("res_count");
				String currentGrade = rs.getString("cus_grade");
				String cusName=rs.getString("cus_name");
				// 새 등급 판단
				String newGrade = "BRONZE";
				if (count >= 10) {
					newGrade = "GOLD";
				} else if (count >= 5) {
					newGrade = "SILVER";
				}

				// 현재 등급과 다르면 업데이트
				if (!newGrade.equalsIgnoreCase(currentGrade)) {
					ps2.setString(1, newGrade);
					ps2.setInt(2, cusNum);

					int result = ps2.executeUpdate();
					if (result > 0) {
						System.out.println(cusName+" 회원님의 등급이 " + currentGrade + " → " + newGrade + "로 변경되었습니다.");
						isUpdated = true;
					}
				}
			}

			if (!isUpdated) {
				System.out.println("변경된 회원 등급이 없습니다.");
			}

		} catch (SQLException e) {
			throw new DMLException("회원 등급 갱신 중 오류 발생: " + e.getMessage());
		} finally {
			closeAll(rs, ps, conn);
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
		Map<String, List<Reservation>> ghAllResList = new HashMap<>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getConnect();

			// {지역: 예약 리스트}로 반환
			// 1.
			String query = "SELECT gus_num, gus_address, substr(gus_address, 1, 2) address FROM guestHouse";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			Map<Integer, String> ghAddressMap = new HashMap<>();
			while (rs.next()) {
				ghAllResList.put(rs.getString("address"), new ArrayList<Reservation>());
				ghAddressMap.put(rs.getInt("gus_num"), rs.getString("address"));
			}

			query = "SELECT res_num, gus_Num, cus_num, res_cindate, res_coutdate, res_tprice, res_tpeople FROM reservation";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				ghAllResList.get(ghAddressMap.get(rs.getInt("gus_num")))
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

}
