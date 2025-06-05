package com.gh.vo;

import java.util.ArrayList;

public class Service {

	private String name; // Service_name
	ArrayList<Reservation> reservation;
	ArrayList<GuestHouse> guestHouse;

	public Service() {
		super();
	}

	public Service(String name, ArrayList<Reservation> reservation, ArrayList<GuestHouse> guestHouse) {
		super();
		this.name = name;
		this.reservation = reservation;
		this.guestHouse = guestHouse;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Reservation> getReservation() {
		return reservation;
	}

	public void setReservation(ArrayList<Reservation> reservation) {
		this.reservation = reservation;
	}

	public ArrayList<GuestHouse> getGuestHouse() {
		return guestHouse;
	}

	public void setGuestHouse(ArrayList<GuestHouse> guestHouse) {
		this.guestHouse = guestHouse;
	}


	@Override
	public String toString() {
		return "Service [name=" + name + ", reservation=" + reservation + ", guestHouse=" + guestHouse + "]";
	}


}