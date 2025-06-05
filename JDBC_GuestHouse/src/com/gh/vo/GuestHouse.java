package com.gh.vo;

public class GuestHouse {
	private int num;// gus_num
	private String serviceName;//service_name
	private String name;// gus_name
	private String address ;// gus_address
	private int price;//gus_price
	private int capacity;//gus_capacity
	public GuestHouse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GuestHouse(int num, String serviceName, String name, String address, int price, int capacity) {
		super();
		this.num = num;
		this.serviceName = serviceName;
		this.name = name;
		this.address = address;
		this.price = price;
		this.capacity = capacity;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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
		return "GuestHouse [num=" + num + ", serviceName=" + serviceName + ", name=" + name + ", address=" + address
				+ ", price=" + price + ", capacity=" + capacity + "]";
	}
	
	

}
