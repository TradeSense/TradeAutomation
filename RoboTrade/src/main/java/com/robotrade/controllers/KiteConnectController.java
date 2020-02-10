package com.robotrade.controllers;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.springframework.stereotype.Component;

import com.robotrade.entrypoint.RoboTradeApplication;
import com.robotrade.service.KiteApiServices;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Quote;

@Component
public class KiteConnectController {

	public  Map<String, Quote> getNSEStockQuote(KiteConnect kiteConnect, Map<String, Quote> instrumentsNew) throws IOException, KiteException{

		KiteApiServices apiService=new KiteApiServices();
		instrumentsNew=apiService.getQuote(kiteConnect);
		return instrumentsNew;
	}

	public  Map<String, Quote> comparethePriceWithOld(Map<String, Quote> instrumentsNew, Map<String, Quote> instrumentsOld, Map<String, Quote> instrumentsTrending) throws IOException, KiteException {
		KiteApiServices apiService=new KiteApiServices();
		for(int i=0; i<RoboTradeApplication.instruments.length;i++) {
			
			if(!RoboTradeApplication.placeOrderStock.contains(RoboTradeApplication.instruments[i])) {
				
				double newPrice = instrumentsNew.get(RoboTradeApplication.instruments[i]).lastPrice;
				double oldPrice = instrumentsOld.get(RoboTradeApplication.instruments[i]).lastPrice;
				double changeInPercent = ((newPrice - oldPrice)/newPrice)*100;
				if(changeInPercent >1.2 && changeInPercent < 2) {
					System.out.println("Uptrend Stock = "+RoboTradeApplication.instruments[i]+"==> CurrentPrice = "+ newPrice + "==>OldPrice = "+oldPrice);
					//apiService.placeBracketOrder(RoboTradeApplication.kiteConnect,newPrice, oldPrice,RoboTradeApplication.instruments[i],Constants.TRANSACTION_TYPE_BUY);
				}else if(changeInPercent > -1.2 && changeInPercent < -2) {
					//apiService.placeBracketOrder(RoboTradeApplication.kiteConnect, newPrice, oldPrice,RoboTradeApplication.instruments[i],Constants.TRANSACTION_TYPE_SELL);
					System.out.println("DownTrend Stock = "+RoboTradeApplication.instruments[i]+"==> CurrentPrice = "+ newPrice + "==>OldPrice = "+oldPrice);

				}
			}
		}
		return instrumentsTrending;
	}
	
	public void getLTP(KiteConnect kiteConnect) throws JSONException, IOException, KiteException {
		KiteApiServices apiService=new KiteApiServices();
		apiService.getLTP(kiteConnect);
	}

}
