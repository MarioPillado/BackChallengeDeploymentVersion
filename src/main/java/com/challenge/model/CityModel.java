package com.challenge.model;

public class CityModel {
	private String name;
	private String latitude;
	private String longitude;
	private float score;
	
	public CityModel(String name, String latitude, String longitude, float score) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.score = score;
	}
	
	public CityModel() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
}