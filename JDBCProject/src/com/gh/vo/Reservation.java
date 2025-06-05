package com.gh.vo;

import java.time.LocalDate;

public class Reservation {
	private int num;// res_num
	private int gusNum ; // 외래키
	private int cusNum;// 외래키
	private LocalDate checkInDate; // res_cindate
	private LocalDate checkOutDate;//res_coutdate
	private int totalPrice; //res_tprice
	private int totalPeople;// res_tpeople
	public Reservation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Reservation(int num, int gusNum, int cusNum, LocalDate checkInDate, LocalDate checkOutDate, int totalPrice,
			int totalPeople) {
		super();
		this.num = num;
		this.gusNum = gusNum;
		this.cusNum = cusNum;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.totalPrice = totalPrice;
		this.totalPeople = totalPeople;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getGusNum() {
		return gusNum;
	}
	public void setGusNum(int gusNum) {
		this.gusNum = gusNum;
	}
	public int getCusNum() {
		return cusNum;
	}
	public void setCusNum(int cusNum) {
		this.cusNum = cusNum;
	}
	public LocalDate getCheckInDate() {
		return checkInDate;
	}
	public void setCheckInDate(LocalDate checkInDate) {
		this.checkInDate = checkInDate;
	}
	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}
	public void setCheckOutDate(LocalDate checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	public int getTotalPeople() {
		return totalPeople;
	}
	public void setTotalPeople(int totalPeople) {
		this.totalPeople = totalPeople;
	}
	@Override
	public String toString() {
		return "Reservation [num=" + num + ", gusNum=" + gusNum + ", cusNum=" + cusNum + ", checkInDate="
				+ checkInDate + ", checkOutDate=" + checkOutDate + ", totalPrice=" + totalPrice + ", totalPeople="
				+ totalPeople + "]";
	}
}
