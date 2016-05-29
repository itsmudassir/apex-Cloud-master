package com.example.aizaz.mainapexcloudversion.model;

public class Tour {
	public  boolean read=false;
	private long id;
	private String date;
	private String time;
	private String actLabel;
	private double latitude;
	private double longitude;

	public boolean getread(){

		return read;
	}

	public void setread(){

		read=true;
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}


	public void setTime(String time) {this.time = time;}


	public String getActLabelctlabel() {
		return actLabel;
	}


	public void setActLabel(String actLabel) {
		this.actLabel = actLabel;
	}
	public String getActLabel() {
		return actLabel;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}




}
