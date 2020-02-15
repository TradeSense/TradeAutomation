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
				double volumeTradedNew = instrumentsNew.get(RoboTradeApplication.instruments[i]).volumeTradedToday;
				double volumeTradedOld = instrumentsOld.get(RoboTradeApplication.instruments[i]).volumeTradedToday;
				double changeInPercent = ((newPrice - oldPrice)/newPrice)*100;
				double volumeTradedInPercent = ((volumeTradedNew - volumeTradedOld)/volumeTradedNew)*100;
				
				double dayPriceHigh = instrumentsOld.get(RoboTradeApplication.instruments[i]).oiDayHigh;
				double dayPriceLow = instrumentsOld.get(RoboTradeApplication.instruments[i]).oiDayLow;
				
				double dayOpen = instrumentsOld.get(RoboTradeApplication.instruments[i]).ohlc.open;
				
				double dayHighPercentDayOpen = ((dayPriceHigh - dayOpen)/dayPriceHigh)*100;
				
				double dayLowPercentDayOpen = ((dayOpen - dayPriceLow)/dayOpen)*100;
				
				
				double changeInPrice = instrumentsOld.get(RoboTradeApplication.instruments[i]).change;

				if(changeInPercent >1 && volumeTradedInPercent > 5 && volumeTradedNew > 10000 && (dayHighPercentDayOpen > dayLowPercentDayOpen) ) {
					System.out.println("Uptrend Stock = "+RoboTradeApplication.instruments[i]+"==> CurrentPrice = "+ newPrice + "==>OldPrice = "+oldPrice +"==>NewVolume ="+volumeTradedNew+"==>TimeStamp="+instrumentsNew.get(RoboTradeApplication.instruments[i]).timestamp);
					//apiService.placeBracketOrder(RoboTradeApplication.kiteConnect,newPrice, oldPrice,RoboTradeApplication.instruments[i],Constants.TRANSACTION_TYPE_BUY);
				}else if(changeInPercent <-1 && volumeTradedInPercent > 5 && volumeTradedNew > 10000 && (dayHighPercentDayOpen < dayLowPercentDayOpen)) {
					//apiService.placeBracketOrder(RoboTradeApplication.kiteConnect, newPrice, oldPrice,RoboTradeApplication.instruments[i],Constants.TRANSACTION_TYPE_SELL);
					System.out.println("DownTrend Stock = "+RoboTradeApplication.instruments[i]+"==> CurrentPrice = "+ newPrice + "==>OldPrice = "+oldPrice +"==>OldVolume ="+volumeTradedOld+"==>TimeStamp="+instrumentsOld.get(RoboTradeApplication.instruments[i]).timestamp);

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
