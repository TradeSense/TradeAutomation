package com.robotrade.model;

public class Stock {
	
	private String instruments;
	private double lastTradePrice;
	private double volume;
	private long instrumentToken;
	
	public String getInstruments() {
		return instruments;
	}
	public void setInstruments(String instruments) {
		this.instruments = instruments;
	}
	public double getLastTradePrice() {
		return lastTradePrice;
	}
	public void setLastTradePrice(double lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}

}
