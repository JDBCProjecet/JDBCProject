package com.gh.vo;

import java.util.ArrayList;

public class GuestHouse {
	private int num;// gus_num
	private String name;// gus_name
	private String address ;// gus_address
	private int price;//gus_price
	private int capacity;//gus_capacity
	private String service; //gus_service
	ArrayList<Reservation>reservation;
	public GuestHouse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GuestHouse(String name, int price) {
		super();
		this.name = name;
		this.price = price;
	}
	public GuestHouse(int num, String name, String address, int price, int capacity, String service) {
		super();
		this.num = num;
		this.name = name;
		this.address = address;
		this.price = price;
		this.capacity = capacity;
		this.service = service;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
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
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	@Override
	public String toString() {
		return "GuestHouse [num=" + num + ", name=" + name + ", address=" + address + ", price=" + price + ", capacity="
				+ capacity + ", service=" + service + ", reservation=" + reservation + "]";
	}
	
	

}
