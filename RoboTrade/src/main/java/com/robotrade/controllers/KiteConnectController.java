package com.robotrade.controllers;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.springframework.stereotype.Component;

import com.robotrade.entrypoint.RoboTradeApplication;
import com.robotrade.service.KiteApiServices;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Quote;

@Component
public class KiteConnectController {
	
	public  Map<String, Quote> getNSEStockQuote(KiteConnect kiteConnect, Map<String, Quote> instrumentsNew) throws IOException, KiteException{
		
		KiteApiServices apiService=new KiteApiServices();
		instrumentsNew=apiService.getQuote(kiteConnect);
		
		return instrumentsNew;
		
	}

	public  Map<String, Quote> comparethePriceWithOld(Map<String, Quote> instrumentsNew, Map<String, Quote> instrumentsOld, Map<String, Quote> instrumentsTrending) {
		
		
		return instrumentsTrending;
		
	}
	 public void getLTP(KiteConnect kiteConnect) throws JSONException, IOException, KiteException {
		 KiteApiServices apiService=new KiteApiServices();
		 apiService.getLTP(kiteConnect);
	 }

}
