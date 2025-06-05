package com.gh.vo;

import java.util.ArrayList;

public class Customer {
	private int num; // cus_num
	private String name; // 원래 컬럼명cus_name
	private String address; // cus_adress
	private String ssn; //cus_ssn
	private char gender;// cus_gender
	private String phone ;// cus_phone
	private String grade;// cus_grade
	ArrayList<Reservation>reservation; // reservation을 갖음
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Customer(int num, String name, String address, String ssn, char gender, String phone, String grade) {
		super();
		this.num = num;
		this.name = name;
		this.address = address;
		this.ssn = ssn;
		this.gender = gender;
		this.phone = phone;
		this.grade = grade;
	}
	
	public Customer(int num, String name, String address, String ssn, char gender, String phone, String grade,
			ArrayList<Reservation> reservation) {
		super();
		this.num = num;
		this.name = name;
		this.address = address;
		this.ssn = ssn;
		this.gender = gender;
		this.phone = phone;
		this.grade = grade;
		this.reservation = reservation;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public char getGender() {
		return gender;
	}
	public void setGender(char gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public ArrayList<Reservation> getReservation() {
		return reservation;
	}
	public void setReservation(ArrayList<Reservation> reservation) {
		this.reservation = reservation;
	}
	@Override
	public String toString() {
		return "Customer [num=" + num + ", name=" + name + ", address=" + address + ", ssn=" + ssn + ", gender="
				+ gender + ", phone=" + phone + ", grade=" + grade + ", reservation=" + reservation + "]";
	}
	

	

}
