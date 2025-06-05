package com.gh.vo;

import java.sql.Date;
import java.util.ArrayList;

public class Reservation {
	private int num;// res_num
	private String Service_name ; // 외래키
	private int cus_num;// 외래키
	private Date cindate; // res_cindate
	private Date coutdate;//res_coutdate
	private int tprice; //res_tprice
	private int tpeople;// res_tpeople
	public Reservation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Reservation(int num, String service_name, int cus_num, Date cindate, Date coutdate, int tprice,
			int tpeople) {
		super();
		this.num = num;
		Service_name = service_name;
		this.cus_num = cus_num;
		this.cindate = cindate;
		this.coutdate = coutdate;
		this.tprice = tprice;
		this.tpeople = tpeople;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getService_name() {
		return Service_name;
	}
	public void setService_name(String service_name) {
		Service_name = service_name;
	}
	public int getCus_num() {
		return cus_num;
	}
	public void setCus_num(int cus_num) {
		this.cus_num = cus_num;
	}
	public Date getCindate() {
		return cindate;
	}
	public void setCindate(Date cindate) {
		this.cindate = cindate;
	}
	public Date getCoutdate() {
		return coutdate;
	}
	public void setCoutdate(Date coutdate) {
		this.coutdate = coutdate;
	}
	public int getTprice() {
		return tprice;
	}
	public void setTprice(int tprice) {
		this.tprice = tprice;
	}
	public int getTpeople() {
		return tpeople;
	}
	public void setTpeople(int tpeople) {
		this.tpeople = tpeople;
	}
	@Override
	public String toString() {
		return "Reservation [num=" + num + ", Service_name=" + Service_name + ", cus_num=" + cus_num + ", cindate="
				+ cindate + ", coutdate=" + coutdate + ", tprice=" + tprice + ", tpeople=" + tpeople + "]";
	}
	
	

}
