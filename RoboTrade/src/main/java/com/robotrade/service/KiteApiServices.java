package com.robotrade.service;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.neovisionaries.ws.client.WebSocketException;
import com.robotrade.entrypoint.RoboTradeApplication;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.GTT;
import com.zerodhatech.models.GTTParams;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Holding;
import com.zerodhatech.models.Instrument;
import com.zerodhatech.models.MFHolding;
import com.zerodhatech.models.MFInstrument;
import com.zerodhatech.models.MFOrder;
import com.zerodhatech.models.MFSIP;
import com.zerodhatech.models.Margin;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Position;
import com.zerodhatech.models.Profile;
import com.zerodhatech.models.Quote;
import com.zerodhatech.models.Tick;
import com.zerodhatech.models.Trade;
import com.zerodhatech.models.TriggerRange;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;
import com.zerodhatech.ticker.OnError;
import com.zerodhatech.ticker.OnOrderUpdate;
import com.zerodhatech.ticker.OnTicks;

/**
 * Created by sujith on 15/10/16.
 */
public class KiteApiServices {

	
    public void getProfile(KiteConnect kiteConnect) throws IOException, KiteException {
        Profile profile = kiteConnect.getProfile();
        System.out.println(profile.userName);
    }

    /**Gets Margin.*/
    public void getMargins(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get margins returns margin model, you can pass equity or commodity as arguments to get margins of respective segments.
        //Margins margins = kiteConnect.getMargins("equity");
        Margin margins = kiteConnect.getMargins("equity");
        System.out.println(margins.available.cash);
        System.out.println(margins.utilised.debits);
        System.out.println(margins.utilised.m2mUnrealised);
    }

    /**Place order.*/
    public void placeOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Place order method requires a orderParams argument which contains,
         * tradingsymbol, exchange, transaction_type, order_type, quantity, product, price, trigger_price, disclosed_quantity, validity
         * squareoff_value, stoploss_value, trailing_stoploss
         * and variety (value can be regular, bo, co, amo)
         * place order will return order model which will have only orderId in the order model
         *
         * Following is an example param for LIMIT order,
         * if a call fails then KiteException will have error message in it
         * Success of this call implies only order has been placed successfully, not order execution. */

        OrderParams orderParams = new OrderParams();
        orderParams.quantity = 1;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = "ASHOKLEY";
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = 122.2;
        orderParams.triggerPrice = 0.0;
        orderParams.tag = "myTag"; //tag is optional and it cannot be more than 8 characters and only alphanumeric is allowed

        Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
        System.out.println(order.orderId);
    }

    /** Place bracket order.*/
    public void placeBracketOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Bracket order:- following is example param for bracket order*
         * trailing_stoploss and stoploss_value are points and not tick or price
         */
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = 1;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.price = 30.5;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.trailingStoploss = 1.0;
        orderParams.stoploss = 2.0;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.squareoff = 3.0;
        orderParams.product = Constants.PRODUCT_MIS;
         Order order10 = kiteConnect.placeOrder(orderParams, Constants.VARIETY_BO);
         System.out.println(order10.orderId);
    }

    /** Place cover order.*/
    public void placeCoverOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Cover Order:- following is an example param for the cover order
         * key: quantity value: 1
         * key: price value: 0
         * key: transaction_type value: BUY
         * key: tradingsymbol value: HINDALCO
         * key: exchange value: NSE
         * key: validity value: DAY
         * key: trigger_price value: 157
         * key: order_type value: MARKET
         * key: variety value: co
         * key: product value: MIS
         */
        OrderParams orderParams = new OrderParams();
        orderParams.price = 0.0;
        orderParams.quantity = 1;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.orderType = Constants.ORDER_TYPE_MARKET;
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.triggerPrice = 30.5;
        orderParams.product = Constants.PRODUCT_MIS;

        Order order11 = kiteConnect.placeOrder(orderParams, Constants.VARIETY_CO);
        System.out.println(order11.orderId);
    }

    /** Get trigger range.*/
    public void getTriggerRange(KiteConnect kiteConnect) throws KiteException, IOException {
        // You need to send transaction_type, exchange and tradingsymbol to get trigger range.
        String[] instruments = {"BSE:INFY", "NSE:APOLLOTYRE", "NSE:SBIN"};
        Map<String, TriggerRange> triggerRangeMap = kiteConnect.getTriggerRange(instruments, Constants.TRANSACTION_TYPE_BUY);
        System.out.println(triggerRangeMap.get("NSE:SBIN").lower);
        System.out.println(triggerRangeMap.get("NSE:APOLLOTYRE").upper);
        System.out.println(triggerRangeMap.get("BSE:INFY").percentage);
    }

    /** Get orderbook.*/
    public void getOrders(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get orders returns order model which will have list of orders inside, which can be accessed as follows,
        List<Order> orders = kiteConnect.getOrders();
        for(int i = 0; i< orders.size(); i++){
            System.out.println(orders.get(i).tradingSymbol+" "+orders.get(i).orderId+" "+orders.get(i).parentOrderId+" "+orders.get(i).orderType+" "+orders.get(i).averagePrice+" "+orders.get(i).exchangeTimestamp);
        }
        System.out.println("list of orders size is "+orders.size());
    }

    /** Get order details*/
    public void getOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        List<Order> orders = kiteConnect.getOrderHistory("180111000561605");
        for(int i = 0; i< orders.size(); i++){
            System.out.println(orders.get(i).orderId+" "+orders.get(i).status);
        }
        System.out.println("list size is "+orders.size());
    }

    /** Get tradebook*/
    public void getTrades(KiteConnect kiteConnect) throws KiteException, IOException {
        // Returns tradebook.
        List<Trade> trades = kiteConnect.getTrades();
        for (int i=0; i < trades.size(); i++) {
            System.out.println(trades.get(i).tradingSymbol+" "+trades.size());
        }
        System.out.println(trades.size());
    }

    /** Get trades for an order.*/
    public void getTradesWithOrderId(KiteConnect kiteConnect) throws KiteException, IOException {
        // Returns trades for the given order.
        List<Trade> trades = kiteConnect.getOrderTrades("180111000561605");
        System.out.println(trades.size());
    }

    /** Modify order.*/
    public void modifyOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        // Order modify request will return order model which will contain only order_id.
        OrderParams orderParams =  new OrderParams();
        orderParams.quantity = 1;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = "ASHOKLEY";
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = 122.25;

        Order order21 = kiteConnect.modifyOrder("180116000984900", orderParams, Constants.VARIETY_REGULAR);
        System.out.println(order21.orderId);
    }

    /** Modify first leg bracket order.*/
    public void modifyFirstLegBo(KiteConnect kiteConnect) throws KiteException, IOException {
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = 1;
        orderParams.price = 31.0;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.product = Constants.PRODUCT_MIS;
        orderParams.tag = "myTag";
        orderParams.triggerPrice = 0.0;

        Order order = kiteConnect.modifyOrder("180116000798058", orderParams, Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    public void modifySecondLegBoSLM(KiteConnect kiteConnect) throws KiteException, IOException {

        OrderParams orderParams = new OrderParams();
        orderParams.parentOrderId = "180116000798058";
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.product = Constants.PRODUCT_MIS;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.triggerPrice = 30.5;
        orderParams.price = 0.0;
        orderParams.orderType = Constants.ORDER_TYPE_SLM;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;

        Order order = kiteConnect.modifyOrder("180116000812154", orderParams, Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    public void modifySecondLegBoLIMIT(KiteConnect kiteConnect) throws KiteException, IOException {
        OrderParams orderParams =  new OrderParams();
        orderParams.parentOrderId = "180116000798058";
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.quantity =  1;
        orderParams.product = Constants.PRODUCT_MIS;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = 35.3;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;

        Order order = kiteConnect.modifyOrder("180116000812153", orderParams, Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    /** Cancel an order*/
    public void cancelOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        // Order modify request will return order model which will contain only order_id.
        // Cancel order will return order model which will only have orderId.
        Order order2 = kiteConnect.cancelOrder("180116000727266", Constants.VARIETY_REGULAR);
        System.out.println(order2.orderId);
    }

    public void exitBracketOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        Order order = kiteConnect.cancelOrder("180116000812153","180116000798058", Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    /**Get all gtts. */
    public void getGTTs(KiteConnect kiteConnect) throws KiteException, IOException {
        List<GTT> gtts = kiteConnect.getGTTs();
        System.out.println(gtts.get(0).createdAt);
        System.out.println(gtts.get(0).condition.exchange);
        System.out.println(gtts.get(0).orders.get(0).price);
    }

    /** Get a particular GTT. */
    public void getGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTT gtt = kiteConnect.getGTT(177574);
        System.out.println(gtt.condition.tradingSymbol);
    }

    /** Place a GTT (Good till trigger)*/
    public void placeGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTTParams gttParams = new GTTParams();
        gttParams.triggerType = Constants.OCO;
        gttParams.exchange = "NSE";
        gttParams.tradingsymbol = "SBIN";
        gttParams.lastPrice = 302.95;

        List<Double> triggerPrices = new ArrayList<>();
        triggerPrices.add(290d);
        triggerPrices.add(320d);
        gttParams.triggerPrices = triggerPrices;

        /** Only sell is allowed for OCO or two-leg orders.
         * Single leg orders can be buy or sell order.
         * Passing a last price is mandatory.
         * A stop-loss order must have trigger and price below last price and target order must have trigger and price above last price.
         * Only limit order type  and CNC product type is allowed for now.
         * */

        /** Stop-loss or lower trigger. */
        GTTParams.GTTOrderParams order1Params = gttParams. new GTTOrderParams();
        order1Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order1Params.price = 290;
        order1Params.product = Constants.PRODUCT_CNC;
        order1Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order1Params.quantity = 0;

        GTTParams.GTTOrderParams order2Params = gttParams. new GTTOrderParams();
        order2Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order2Params.price = 320;
        order2Params.product = Constants.PRODUCT_CNC;
        order2Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order2Params.quantity = 1;

        /** Target or upper trigger. */
        List<GTTParams.GTTOrderParams> ordersList = new ArrayList();
        ordersList.add(order1Params);
        ordersList.add(order2Params);
        gttParams.orders = ordersList;

        GTT gtt = kiteConnect.placeGTT(gttParams);
        System.out.println(gtt.id);
    }

    /** Modify a GTT (Good till trigger)*/
    public void modifyGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTTParams gttParams = new GTTParams();
        gttParams.triggerType = Constants.OCO;
        gttParams.exchange = "NSE";
        gttParams.tradingsymbol = "SBIN";
        gttParams.lastPrice = 302.95;

        List<Double> triggerPrices = new ArrayList<>();
        triggerPrices.add(290d);
        triggerPrices.add(320d);
        gttParams.triggerPrices = triggerPrices;

        GTTParams.GTTOrderParams order1Params = gttParams. new GTTOrderParams();
        order1Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order1Params.price = 290;
        order1Params.product = Constants.PRODUCT_CNC;
        order1Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order1Params.quantity = 1;

        GTTParams.GTTOrderParams order2Params = gttParams. new GTTOrderParams();
        order2Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order2Params.price = 320;
        order2Params.product = Constants.PRODUCT_CNC;
        order2Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order2Params.quantity = 1;

        List<GTTParams.GTTOrderParams> ordersList = new ArrayList();
        ordersList.add(order1Params);
        ordersList.add(order2Params);
        gttParams.orders = ordersList;

        GTT gtt = kiteConnect.modifyGTT(176036, gttParams);
        System.out.println(gtt.id);
    }

    /** Cancel a GTT.*/
    public void cancelGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTT gtt = kiteConnect.cancelGTT(175859);
        System.out.println(gtt.id);
    }

    /** Get all positions.*/
    public void getPositions(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get positions returns position model which contains list of positions.
        Map<String, List<Position>> position = kiteConnect.getPositions();
        System.out.println(position.get("net").size());
        System.out.println(position.get("day").size());
    }

    /** Get holdings.*/
    public void getHoldings(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get holdings returns holdings model which contains list of holdings.
        List<Holding> holdings = kiteConnect.getHoldings();
        System.out.println(holdings.size());
    }

    /** Converts position*/
    public void converPosition(KiteConnect kiteConnect) throws KiteException, IOException {
        //Modify product can be used to change MIS to NRML(CNC) or NRML(CNC) to MIS.
        JSONObject jsonObject6 = kiteConnect.convertPosition("ASHOKLEY", Constants.EXCHANGE_NSE, Constants.TRANSACTION_TYPE_BUY, Constants.POSITION_DAY, Constants.PRODUCT_MIS, Constants.PRODUCT_CNC, 1);
        System.out.println(jsonObject6);
    }

    /** Get all instruments that can be traded using kite connect.*/
    public void getAllInstruments(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get all instruments list. This call is very expensive as it involves downloading of large data dump.
        // Hence, it is recommended that this call be made once and the results stored locally once every morning before market opening.
        List<Instrument> instruments = kiteConnect.getInstruments();
        System.out.println(instruments.size());
    }

    /** Get instruments for the desired exchange.*/
    public void getInstrumentsForExchange(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get instruments for an exchange.
        List<Instrument> nseInstruments = kiteConnect.getInstruments("CDS");
        System.out.println(nseInstruments.size());
    }

    /** Get quote for a scrip.*/
    public Map<String, Quote> getQuote(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get quotes returns quote for desired tradingsymbol.
        Map<String, Quote> quotes = kiteConnect.getQuote(RoboTradeApplication.instruments);
        
        for(int i=0;i<RoboTradeApplication.instruments.length;i++) {   	
	        System.out.println(quotes.get(RoboTradeApplication.instruments[i]).instrumentToken+"");
	        System.out.println(quotes.get(RoboTradeApplication.instruments[i]).oi +"");
	        System.out.println(quotes.get(RoboTradeApplication.instruments[i]).lastPrice);

	        System.out.println(quotes.get(RoboTradeApplication.instruments[i]).depth.buy.get(4).getPrice());
	        System.out.println(quotes.get(RoboTradeApplication.instruments[i]).timestamp);
	        System.out.println(quotes.get(RoboTradeApplication.instruments[i]).lowerCircuitLimit+"");
	        System.out.println(quotes.get(RoboTradeApplication.instruments[i]).upperCircuitLimit+"");
        }
		return quotes;
    }

    /* Get ohlc and lastprice for multiple instruments at once.
     * Users can either pass exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265, 265}*/
    public void getOHLC(KiteConnect kiteConnect) throws KiteException, IOException {
        String[] instruments = {"256265","BSE:INFY", "NSE:INFY", "NSE:NIFTY 50"};
        System.out.println(kiteConnect.getOHLC(instruments).get("256265").lastPrice);
        System.out.println(kiteConnect.getOHLC(instruments).get("NSE:NIFTY 50").ohlc.open);
    }

    /** Get last price for multiple instruments at once.
     * USers can either pass exchange with t.radingsymbol or instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265, 265}*/
    public void getLTP(KiteConnect kiteConnect) throws KiteException, IOException {
        String[] instruments = {"NSE:AKASH","NSE:20MICRONS","NSE:21STCENMGM","NSE:3IINFOTECH","NSE:3MINDIA","NSE:3PLAND","NSE:5PAISA","NSE:63MOONS","NSE:A2ZINFRA","NSE:AARTIDRUGS", "NSE:AARTIIND","NSE:AARVEEDEN","NSE:AAVAS","NSE:ABAN","NSE:ABB","NSE:ABBOTINDIA","NSE:ABCAPITAL","NSE:ABFRL","NSE:ABMINTLTD-BE","NSE:ACC","NSE:ACCELYA", "NSE:ACE", "NSE:ADANIENT", "NSE:ADANIGAS", "NSE:ADANIGREEN", "NSE:ADANIPORTS", "NSE:ADANIPOWER", "NSE:ADANITRANS", "NSE:ADFFOODS", "NSE:ADHUNIKIND", "NSE:ADLABS-BE", "NSE:ADORWELD", "NSE:ADROITINFO-BE", "NSE:ADSL-BE", "NSE:ADVANIHOTR", "NSE:ADVENZYMES", "NSE:AEGISCHEM", "NSE:AFFLE", "NSE:AGARIND", "NSE:AGCNET", "NSE:AGRITECH", "NSE:AGROPHOS", "NSE:AHLEAST", "NSE:AHLUCONT", "NSE:AHLWEST", "NSE:AIAENG", "NSE:AIONJSW", "NSE:AIRAN", "NSE:AJANTPHARM", "NSE:AJMERA", "NSE:AKSHARCHEM", "NSE:AKSHOPTFBR", "NSE:AKZOINDIA", "NSE:ALANKIT", "NSE:ALBERTDAVD", "NSE:ALBK", "NSE:ALCHEM-BE", "NSE:ALEMBICLTD", "NSE:ALICON", "NSE:ALKALI", "NSE:ALKEM", "NSE:ALKYLAMINE", "NSE:ALLCARGO", "NSE:ALLSEC", "NSE:ALMONDZ", "NSE:ALPA", "NSE:ALPHAGEO", "NSE:ALPSINDUS-BE", "NSE:AMARAJABAT", "NSE:AMBER", "NSE:AMBIKCO", "NSE:AMBUJACEM", "NSE:AMDIND", "NSE:AMJLAND", "NSE:AMRUTANJAN", "NSE:ANANTRAJ", "NSE:ANDHRABANK", "NSE:ANDHRACEMT", "NSE:ANDHRSUGAR", "NSE:ANDPAPER", "NSE:ANIKINDS", "NSE:ANKITMETAL", "NSE:ANSALAPI", "NSE:ANSALHSG", "NSE:ANTGRAPHIC", "NSE:ANUP", "NSE:APARINDS", "NSE:APCL", "NSE:APCOTEXIND", "NSE:APEX", "NSE:APLAPOLLO", "NSE:APLLTD", "NSE:APOLLO", "NSE:APOLLOHOSP", "NSE:APOLLOPIPE", "NSE:APOLLOTYRE", "NSE:APOLSINHOT", "NSE:APTECHT", "NSE:ARCHIDPLY", "NSE:ARCHIES", "NSE:ARCOTECH", "NSE:ARENTERP", "NSE:ARIES", "NSE:ARIHANT", "NSE:ARIHANTSUP", "NSE:ARMANFIN", "NSE:AROGRANITE", "NSE:ARROWGREEN", "NSE:ARROWTEX", "NSE:ARSHIYA", "NSE:ARSSINFRA", "NSE:ARTEMISMED", "NSE:ARVIND", "NSE:ARVINDFASN", "NSE:ARVSMART", "NSE:ASAHIINDIA", "NSE:ASAHISONG", "NSE:ASAL", "NSE:ASHAPURMIN", "NSE:ASHIANA", "NSE:ASHIMASYN", "NSE:ASHOKA", "NSE:ASHOKLEY", "NSE:ASIANHOTNR", "NSE:ASIANPAINT", "NSE:ASIANTILES", "NSE:ASPINWALL", "NSE:ASTEC", "NSE:ASTERDM", "NSE:ASTRAL", "NSE:ASTRAMICRO", "NSE:ASTRAZEN", "NSE:ASTRON", "NSE:ATFL", "NSE:ATLANTA", "NSE:ATLASCYCLE", "NSE:ATUL", "NSE:ATULAUTO", "NSE:AUBANK", "NSE:AURIONPRO", "NSE:AUROPHARMA", "NSE:AUSOMENT", "NSE:AUTOAXLES", "NSE:AUTOIND", "NSE:AUTOLITIND", "NSE:AVADHSUGAR", "NSE:AVANTIFEED", "NSE:AVTNPL", "NSE:AXISBANK", "NSE:AXISCADES", "NSE:AYMSYNTEX", "NSE:BAGFILMS", "NSE:BAJAJ-AUTO", "NSE:BAJAJCON", "NSE:BAJAJELEC", "NSE:BAJAJFINSV", "NSE:BAJAJHIND", "NSE:BAJAJHLDNG", "NSE:BAJFINANCE", "NSE:BALAJITELE", "NSE:BALAMINES", "NSE:BALAXI", "NSE:BALKRISHNA", "NSE:BALKRISIND", "NSE:BALLARPUR", "NSE:BALMLAWRIE", "NSE:BALPHARMA", "NSE:BALRAMCHIN", "NSE:BANARBEADS", "NSE:BANARISUG", "NSE:BANCOINDIA", "NSE:BANDHANBNK", "NSE:BANG", "NSE:BANKBARODA", "NSE:BANKINDIA", "NSE:BANSWRAS", "NSE:BARTRONICS", "NSE:BASF", "NSE:BASML", "NSE:BATAINDIA", "NSE:BAYERCROP", "NSE:BBL", "NSE:BBTC", "NSE:BCG", "NSE:BCP", "NSE:BDL", "NSE:BEARDSELL", "NSE:BEDMUTHA", "NSE:BEL", "NSE:BEML", "NSE:BEPL", "NSE:BERGEPAINT", "NSE:BFINVEST", "NSE:BFUTILITIE", "NSE:BGLOBAL", "NSE:BGRENERGY", "NSE:BHAGERIA", "NSE:BHAGYANGR", "NSE:BHAGYAPROP", "NSE:BHANDARI", "NSE:BHARATFORG", "NSE:BHARATGEAR", "NSE:BHARATRAS", "NSE:BHARATWIRE", "NSE:BHARTIARTL", "NSE:BHEL", "NSE:BIGBLOC", "NSE:BIL", "NSE:BILENERGY", "NSE:BINDALAGRO", "NSE:BIOCON", "NSE:BIOFILCHEM", "NSE:BIRLACABLE", "NSE:BIRLACORPN", "NSE:BIRLAMONEY", "NSE:BKMINDST", "NSE:BLBLIMITED", "NSE:BLISSGVS", "NSE:BLKASHYAP", "NSE:BLS", "NSE:BLUEBLENDS", "NSE:BLUEDART", "NSE:BLUESTARCO", "NSE:BODALCHEM", "NSE:BOMDYEING", "NSE:BOROSIL", "NSE:BOSCHLTD", "NSE:BPCL", "NSE:BPL", "NSE:BRFL", "NSE:BRIGADE", "NSE:BRITANNIA", "NSE:BRNL", "NSE:BROOKS", "NSE:BSE", "NSE:BSELINFRA", "NSE:BSL", "NSE:BSOFT", "NSE:BURNPUR", "NSE:BUTTERFLY", "NSE:BVCL", "NSE:BYKE", "NSE:CADILAHC", "NSE:CALSOFT", "NSE:CAMLINFINE", "NSE:CANBK", "NSE:CANDC", "NSE:CANFINHOME", "NSE:CANTABIL", "NSE:CAPACITE", "NSE:CAPLIPOINT", "NSE:CAPTRUST", "NSE:CARBORUNIV", "NSE:CAREERP", "NSE:CARERATING", "NSE:CASTEXTECH", "NSE:CASTROLIND", "NSE:CCHHL", "NSE:CCL", "NSE:CDSL", "NSE:CEATLTD", "NSE:CEBBCO", "NSE:CELEBRITY", "NSE:CELESTIAL", "NSE:CENTENKA", "NSE:CENTEXT", "NSE:CENTRALBK", "NSE:CENTRUM", "NSE:CENTUM", "NSE:CENTURYPLY", "NSE:CENTURYTEX", "NSE:CERA", "NSE:CEREBRAINT", "NSE:CESC", "NSE:CESCVENT", "NSE:CGCL", "NSE:CGPOWER", "NSE:CHALET", "NSE:CHAMBLFERT", "NSE:CHEMBOND", "NSE:CHEMFAB", "NSE:CHENNPETRO", "NSE:CHOLAFIN", "NSE:CHOLAHLDNG", "NSE:CHROMATIC", "NSE:CIGNITITEC", "NSE:CIMMCO", "NSE:CINELINE", "NSE:CINEVISTA", "NSE:CIPLA", "NSE:CKFSL", "NSE:CLEDUCATE", "NSE:CLNINDIA", "NSE:CMICABLES", "NSE:CNOVAPETRO", "NSE:COALINDIA", "NSE:COCHINSHIP", "NSE:COFFEEDAY", "NSE:COLPAL", "NSE:COMPINFO", "NSE:COMPUSOFT", "NSE:CONCOR", "NSE:CONFIPET", "NSE:CONSOFINVT", "NSE:CONTROLPR", "NSE:CORALFINAC", "NSE:CORDSCABLE", "NSE:COROMANDEL", "NSE:CORPBANK", "NSE:COSMOFILMS", "NSE:COUNCODOS", "NSE:COX&KINGS", "NSE:CREATIVE", "NSE:CREATIVEYE", "NSE:CREDITACC", "NSE:CREST", "NSE:CRISIL", "NSE:CROMPTON", "NSE:CSBBANK", "NSE:CTE", "NSE:CUB", "NSE:CUBEXTUB", "NSE:CUMMINSIND", "NSE:CUPID", "NSE:CURATECH", "NSE:CYBERMEDIA", "NSE:CYBERTECH", "NSE:CYIENT", "NSE:DAAWAT", "NSE:DABUR", "NSE:DALBHARAT", "NSE:DALMIASUG", "NSE:DAMODARIND", "NSE:DATAMATICS", "NSE:DBCORP", "NSE:DBL", "NSE:DBREALTY", "NSE:DBSTOCKBRO", "NSE:DCAL", "NSE:DCBBANK", "NSE:DCM", "NSE:DCMFINSERV", "NSE:DCMNVL", "NSE:DCMSHRIRAM", "NSE:DCW", "NSE:DECCANCE", "NSE:DEEPAKFERT", "NSE:DEEPAKNTR", "NSE:DEEPIND", "NSE:DELTACORP", "NSE:DELTAMAGNT", "NSE:DEN", "NSE:DENORA", "NSE:DFMFOODS", "NSE:DGCONTENT", "NSE:DHAMPURSUG", "NSE:DHANBANK", "NSE:DHANUKA", "NSE:DHARSUGAR", "NSE:DHFL", "NSE:DHUNINV", "NSE:DIAMONDYD", "NSE:DIAPOWER", "NSE:DICIND", "NSE:DIGISPICE", "NSE:DIGJAMLTD", "NSE:DISHTV", "NSE:DIVISLAB", "NSE:DIXON", "NSE:DLF", "NSE:DLINKINDIA", "NSE:DMART", "NSE:DNAMEDIA", "NSE:DOLAT", "NSE:DOLLAR", "NSE:DONEAR", "NSE:DPSCLTD", "NSE:DPWIRES", "NSE:DQE", "NSE:DREDGECORP", "NSE:DRREDDY", "NSE:DSSL", "NSE:DTIL", "NSE:DUCON", "NSE:DVL", "NSE:DWARKESH", "NSE:DYNAMATECH", "NSE:DYNPRO", "NSE:EASTSILK", "NSE:EASUNREYRL", "NSE:ECLERX", "NSE:EDELWEISS", "NSE:EDL", "NSE:EDUCOMP", "NSE:EICHERMOT", "NSE:EIDPARRY", "NSE:EIHAHOTELS", "NSE:EIHOTEL", "NSE:EIMCOELECO", "NSE:EKC", "NSE:ELECON", "NSE:ELECTCAST", "NSE:ELECTHERM", "NSE:ELGIEQUIP", "NSE:ELGIRUBCO", "NSE:EMAMILTD", "NSE:EMAMIPAP", "NSE:EMAMIREAL", "NSE:EMCO", "NSE:EMKAY", "NSE:EMMBI", "NSE:ENDURANCE", "NSE:ENERGYDEV", "NSE:ENGINERSIN", "NSE:ENIL", "NSE:EON", "NSE:EQUITAS", "NSE:ERIS", "NSE:EROSMEDIA", "NSE:ESABINDIA", "NSE:ESCORTS", "NSE:ESSARSHPNG", "NSE:ESSELPACK", "NSE:ESTER", "NSE:EUROCERA", "NSE:EUROMULTI", "NSE:EUROTEXIND", "NSE:EVEREADY", "NSE:EVERESTIND", "NSE:EXCEL", "NSE:EXCELINDUS", "NSE:EXIDEIND", "NSE:EXPLEOSOL", "NSE:FACT", "NSE:FAIRCHEM", "NSE:FCL", "NSE:FCONSUMER", "NSE:FCSSOFT", "NSE:FDC", "NSE:FEDERALBNK", "NSE:FEL", "NSE:FELDVR", "NSE:FIEMIND", "NSE:FILATEX", "NSE:FINCABLES", "NSE:FINEORG", "NSE:FINPIPE", "NSE:FLEXITUFF", "NSE:FLFL", "NSE:FLUOROCHEM", "NSE:FMGOETZE", "NSE:FMNL", "NSE:FORCEMOT", "NSE:FORTIS", "NSE:FOSECOIND", "NSE:FRETAIL", "NSE:FSC", "NSE:FSL", "NSE:GABRIEL", "NSE:GAEL", "NSE:GAIL", "NSE:GAL", "NSE:GALAXYSURF", "NSE:GALLANTT", "NSE:GALLISPAT", "NSE:GAMMNINFRA", "NSE:GANDHITUBE", "NSE:GANECOS", "NSE:GANESHHOUC", "NSE:GANGESSECU", "NSE:GANGOTRI", "NSE:GARDENSILK", "NSE:GARFIBRES", "NSE:GATI", "NSE:GAYAHWS", "NSE:GAYAPROJ", "NSE:GDL", "NSE:GEECEE", "NSE:GENESYS", "NSE:GENUSPAPER", "NSE:GENUSPOWER", "NSE:GEOJITFSL", "NSE:GEPIL", "NSE:GESHIP", "NSE:GET&D", "NSE:GFLLIMITED", "NSE:GHCL", "NSE:GICHSGFIN", "NSE:GICRE", "NSE:GILLANDERS", "NSE:GILLETTE", "NSE:GINNIFILA", "NSE:GIPCL", "NSE:GISOLUTION", "NSE:GKWLIMITED", "NSE:GLAXO", "NSE:GLENMARK", "NSE:GLFL", "NSE:GLOBALVECT", "NSE:GLOBOFFS", "NSE:GLOBUSSPR", "NSE:GMBREW", "NSE:GMDCLTD", "NSE:GMMPFAUDLR", "NSE:GMRINFRA", "NSE:GNA", "NSE:GNFC", "NSE:GOACARBON", "NSE:GOCLCORP", "NSE:GODFRYPHLP", "NSE:GODREJAGRO", "NSE:GODREJCP", "NSE:GODREJIND", "NSE:GODREJPROP", "NSE:GOENKA", "NSE:GOKEX", "NSE:GOKUL", "NSE:GOKULAGRO", "NSE:GOLDENTOBC", "NSE:GOLDIAM", "NSE:GOLDTECH", "NSE:GOODLUCK", "NSE:GPIL", "NSE:GPPL", "NSE:GPTINFRA", "NSE:GRANULES", "NSE:GRAPHITE", "NSE:GRASIM", "NSE:GRAVITA", "NSE:GREAVESCOT", "NSE:GREENLAM", "NSE:GREENPANEL", "NSE:GREENPLY", "NSE:GREENPOWER", "NSE:GRINDWELL", "NSE:GROBTEA", "NSE:GRPLTD", "NSE:GRSE", "NSE:GSCLCEMENT", "NSE:GSFC", "NSE:GSKCONS", "NSE:GSPL", "NSE:GSS", "NSE:GTL", "NSE:GTLINFRA", "NSE:GTNIND", "NSE:GTNTEX", "NSE:GTPL", "NSE:GUFICBIO", "NSE:GUJALKALI", "NSE:GUJAPOLLO", "NSE:GUJGASLTD", "NSE:GUJRAFFIA", "NSE:GULFOILLUB", "NSE:GULFPETRO", "NSE:GULPOLY", "NSE:GVKPIL", "NSE:HAL", "NSE:HARITASEAT", "NSE:HARRMALAYA", "NSE:HATHWAY", "NSE:HATSUN", "NSE:HAVELLS", "NSE:HAVISHA", "NSE:HBLPOWER", "NSE:HBSL", "NSE:HCC", "NSE:HCG", "NSE:HCL-INSYS", "NSE:HCLTECH", "NSE:HDFC", "NSE:HDFCAMC", "NSE:HDFCBANK", "NSE:HDFCLIFE", "NSE:HDIL", "NSE:HEG", "NSE:HEIDELBERG", "NSE:HERCULES", "NSE:HERITGFOOD", "NSE:HEROMOTOCO", "NSE:HESTERBIO", "NSE:HEXATRADEX", "NSE:HEXAWARE", "NSE:HFCL", "NSE:HGINFRA", "NSE:HGS", "NSE:HIKAL", "NSE:HIL", "NSE:HILTON", "NSE:HIMATSEIDE", "NSE:HINDALCO", "NSE:HINDCOMPOS", "NSE:HINDCOPPER", "NSE:HINDMOTORS", "NSE:HINDNATGLS", "NSE:HINDOILEXP", "NSE:HINDPETRO", "NSE:HINDSYNTEX", "NSE:HINDUNILVR", "NSE:HINDZINC", "NSE:HIRECT", "NSE:HISARMETAL", "NSE:HITECH", "NSE:HITECHCORP", "NSE:HITECHGEAR", "NSE:HLVLTD", "NSE:HMT", "NSE:HMVL", "NSE:HNDFDS", "NSE:HONAUT", "NSE:HONDAPOWER", "NSE:HOTELRUGBY", "NSE:HOVS", "NSE:HPL", "NSE:HSCL", "NSE:HSIL", "NSE:HTMEDIA", "NSE:HUBTOWN", "NSE:HUDCO", "NSE:IBREALEST", "NSE:IBULHSGFIN", "NSE:IBULISL", "NSE:IBVENTURES", "NSE:ICICIBANK", "NSE:ICICIGI", "NSE:ICICIPRULI", "NSE:ICIL", "NSE:ICRA", "NSE:IDBI", "NSE:IDEA", "NSE:IDFC", "NSE:IDFCFIRSTB", "NSE:IEX", "NSE:IFBAGRO", "NSE:IFBIND", "NSE:IFCI", "NSE:IFGLEXPOR", "NSE:IGARASHI", "NSE:IGL", "NSE:IGPL", "NSE:IIFL", "NSE:IIFLSEC", "NSE:IIFLWAM", "NSE:IITL", "NSE:IL&FSENGG", "NSE:IL&FSTRANS", "NSE:IMFA", "NSE:IMPAL", "NSE:IMPEXFERRO", "NSE:INDBANK", "NSE:INDHOTEL", "NSE:INDIACEM", "NSE:INDIAGLYCO", "NSE:INDIAMART", "NSE:INDIANB", "NSE:INDIANCARD", "NSE:INDIANHUME", "NSE:INDIGO", "NSE:INDLMETER", "NSE:INDNIPPON", "NSE:INDOCO", "NSE:INDORAMA", "NSE:INDOSOLAR", "NSE:INDOSTAR", "NSE:INDOTECH", "NSE:INDOTHAI", "NSE:INDOWIND", "NSE:INDRAMEDCO", "NSE:INDSWFTLAB", "NSE:INDSWFTLTD", "NSE:INDTERRAIN", "NSE:INDUSINDBK", "NSE:INEOSSTYRO", "NSE:INFIBEAM", "NSE:INFOBEAN", "NSE:INFRATEL", "NSE:INFY", "NSE:INGERRAND", "NSE:INOXLEISUR", "NSE:INOXWIND", "NSE:INSECTICID", "NSE:INSPIRISYS", "NSE:INTEGRA", "NSE:INTELLECT", "NSE:INTENTECH", "NSE:INVENTURE", "NSE:IOB", "NSE:IOC", "NSE:IOLCP", "NSE:IPCALAB", "NSE:IRB", "NSE:IRCON", "NSE:IRCTC", "NSE:ISEC", "NSE:ISFT", "NSE:ISMTLTD", "NSE:ITC", "NSE:ITDC", "NSE:ITDCEM", "NSE:ITI", "NSE:IVC", "NSE:IVP", "NSE:IVRCLINFRA", "NSE:IZMO", "NSE:J&KBANK", "NSE:JAGRAN", "NSE:JAGSNPHARM", "NSE:JAIBALAJI", "NSE:JAICORPLTD", "NSE:JAIHINDPRO", "NSE:JAINSTUDIO", "NSE:JAMNAAUTO", "NSE:JASH", "NSE:JAYAGROGN", "NSE:JAYBARMARU", "NSE:JAYNECOIND", "NSE:JAYSREETEA", "NSE:JBCHEPHARM", "NSE:JBFIND", "NSE:JBMA", "NSE:JCHAC", "NSE:JETAIRWAYS", "NSE:JHS", "NSE:JIKIND", "NSE:JINDALPHOT", "NSE:JINDALPOLY", "NSE:JINDALSAW", "NSE:JINDALSTEL", "NSE:JINDCOT", "NSE:JINDRILL", "NSE:JINDWORLD", "NSE:JISLDVREQS", "NSE:JISLJALEQS", "NSE:JITFINFRA", "NSE:JIYAECO", "NSE:JKCEMENT", "NSE:JKIL", "NSE:JKLAKSHMI", "NSE:JKPAPER", "NSE:JKTYRE", "NSE:JMA", "NSE:JMCPROJECT", "NSE:JMFINANCIL", "NSE:JMTAUTOLTD", "NSE:JOCIL", "NSE:JPASSOCIAT", "NSE:JPINFRATEC", "NSE:JPOLYINVST", "NSE:JPPOWER", "NSE:JSL", "NSE:JSLHISAR", "NSE:JSWENERGY", "NSE:JSWHL", "NSE:JSWSTEEL", "NSE:JTEKTINDIA", "NSE:JUBILANT", "NSE:JUBLFOOD", "NSE:JUBLINDS", "NSE:JUMPNET", "NSE:JUSTDIAL", "NSE:JVLAGRO", "NSE:JYOTHYLAB", "NSE:KABRAEXTRU", "NSE:KAJARIACER", "NSE:KAKATCEM", "NSE:KALPATPOWR", "NSE:KALYANI", "NSE:KALYANIFRG", "NSE:KAMATHOTEL", "NSE:KAMDHENU", "NSE:KANANIIND", "NSE:KANORICHEM", "NSE:KANSAINER", "NSE:KARDA", "NSE:KARMAENG", "NSE:KARURVYSYA", "NSE:KAUSHALYA", "NSE:KAVVERITEL", "NSE:KAYA", "NSE:KCP", "NSE:KCPSUGIND", "NSE:KDDL", "NSE:KEC", "NSE:KECL", "NSE:KEI", "NSE:KELLTONTEC", "NSE:KENNAMET", "NSE:KERNEX", "NSE:KESORAMIND", "NSE:KEYFINSERV", "NSE:KGL", "NSE:KHADIM", "NSE:KHAITANLTD", "NSE:KHANDSE", "NSE:KICL", "NSE:KILITCH", "NSE:KINGFA", "NSE:KIOCL", "NSE:KIRIINDUS", "NSE:KIRLFER", "NSE:KIRLOSBROS", "NSE:KIRLOSENG", "NSE:KIRLOSIND", "NSE:KITEX", "NSE:KKCL", "NSE:KMSUGAR", "NSE:KNRCON", "NSE:KOHINOOR", "NSE:KOKUYOCMLN", "NSE:KOLTEPATIL", "NSE:KOPRAN", "NSE:KOTAKBANK", "NSE:KOTARISUG", "NSE:KOTHARIPET", "NSE:KOTHARIPRO", "NSE:KPITTECH", "NSE:KPRMILL", "NSE:KRBL", "NSE:KREBSBIO", "NSE:KRIDHANINF", "NSE:KRISHANA", "NSE:KSB", "NSE:KSCL", "NSE:KSERASERA", "NSE:KSK", "NSE:KSL", "NSE:KTKBANK", "NSE:KUANTUM", "NSE:KWALITY", "NSE:L&TFH", "NSE:LAKPRE", "NSE:LAKSHVILAS", "NSE:LALPATHLAB", "NSE:LAMBODHARA", "NSE:LAOPALA", "NSE:LASA", "NSE:LAURUSLABS", "NSE:LAXMIMACH", "NSE:LEMONTREE", "NSE:LFIC", "NSE:LGBBROSLTD", "NSE:LGBFORGE", "NSE:LIBAS", "NSE:LIBERTSHOE", "NSE:LICHSGFIN", "NSE:LINCOLN", "NSE:LINCPEN", "NSE:LINDEINDIA", "NSE:LOKESHMACH", "NSE:LOTUSEYE", "NSE:LOVABLE", "NSE:LPDC", "NSE:LSIL", "NSE:LT", "NSE:LTI", "NSE:LTTS", "NSE:LUMAXIND", "NSE:LUMAXTECH", "NSE:LUPIN", "NSE:LUXIND", "NSE:LYKALABS", "NSE:LYPSAGEMS", "NSE:M&M", "NSE:M&MFIN", "NSE:MAANALU", "NSE:MADHAV", "NSE:MADHUCON", "NSE:MADRASFERT", "NSE:MAGADSUGAR", "NSE:MAGMA", "NSE:MAGNUM", "NSE:MAHABANK", "NSE:MAHAPEXLTD", "NSE:MAHASTEEL", "NSE:MAHESHWARI", "NSE:MAHINDCIE", "NSE:MAHLIFE", "NSE:MAHLOG", "NSE:MAHSCOOTER", "NSE:MAHSEAMLES", "NSE:MAITHANALL", "NSE:MAJESCO", "NSE:MALUPAPER", "NSE:MANAKALUCO", "NSE:MANAKCOAT", "NSE:MANAKSIA", "NSE:MANAKSTEEL", "NSE:MANALIPETC", "NSE:MANAPPURAM", "NSE:MANGALAM", "NSE:MANGCHEFER", "NSE:MANGLMCEM", "NSE:MANGTIMBER", "NSE:MANINDS", "NSE:MANINFRA", "NSE:MANUGRAPH"};
        //System.out.println(kiteConnect.getLTP(instruments).get("NSE:INFY").lastPrice);
        
        for(int i=0;i<instruments.length;i++) {   	
        	//if(kiteConnect.getLTP(instruments).get(instruments[i]).lastPrice>400) {
        	if(kiteConnect.getLTP(instruments).get(instruments[i])!=null) {
	        	//System.out.println(instruments[i]+" = "+kiteConnect.getLTP(instruments).get(instruments[i]).lastPrice);
            	if(kiteConnect.getLTP(instruments).get(instruments[i]).lastPrice>600 && kiteConnect.getLTP(instruments).get(instruments[i]).lastPrice<1500) {

            		System.out.print("\""+instruments[i]+"\",");
            	}
        	}
        	//}
        }

    }

    /** Get historical data for an instrument.*/
    public void getHistoricalData(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Get historical data dump, requires from and to date, intrument token, interval, continuous (for expired F&O contracts), oi (open interest)
         * returns historical data object which will have list of historical data inside the object.*/
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date from =  new Date();
        Date to = new Date();
        try {
            from = formatter.parse("2020-01-16 09:15:00");
            to = formatter.parse("2010-01-16 15:30:00");
        }catch (ParseException e) {
            e.printStackTrace();
        }
        HistoricalData historicalData = kiteConnect.getHistoricalData(from, to, "54872327", "3minute", false, true);
        System.out.println(historicalData.dataArrayList.size());
        System.out.println(historicalData.dataArrayList.get(0).volume);
        System.out.println(historicalData.dataArrayList.get(historicalData.dataArrayList.size() - 1).volume);
        System.out.println(historicalData.dataArrayList.get(0).oi);
    }

    /** Logout user.*/
    public void logout(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Logout user and kill session. */
        JSONObject jsonObject10 = kiteConnect.logout();
        System.out.println(jsonObject10);
    }

    /** Retrieve mf instrument dump */
    public void getMFInstruments(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFInstrument> mfList = kiteConnect.getMFInstruments();
        System.out.println("size of mf instrument list: "+mfList.size());
    }

    /* Get all mutualfunds holdings */
    public void getMFHoldings(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFHolding> MFHoldings = kiteConnect.getMFHoldings();
        System.out.println("mf holdings "+ MFHoldings.size());
    }

    /* Place a mutualfunds order */
    public void placeMFOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("place order: "+ kiteConnect.placeMFOrder("INF174K01LS2", Constants.TRANSACTION_TYPE_BUY, 5000, 0, "myTag").orderId);
    }

    /* cancel mutualfunds order */
    public void cancelMFOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        kiteConnect.cancelMFOrder("668604240868430");
        System.out.println("cancel order successful");
    }

    /* retrieve all mutualfunds orders */
    public void getMFOrders(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFOrder> MFOrders = kiteConnect.getMFOrders();
        System.out.println("mf orders: "+ MFOrders.size());
    }

    /* retrieve individual mutualfunds order */
    public void getMFOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("mf order: "+ kiteConnect.getMFOrder("106580291331583").tradingsymbol);
    }

    /* place mutualfunds sip */
    public void placeMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("mf place sip: "+ kiteConnect.placeMFSIP("INF174K01LS2", "monthly", 1, -1, 5000, 1000).sipId);
    }

    /* modify a mutual fund sip */
    public void modifyMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        kiteConnect.modifyMFSIP("weekly", 1, 5, 1000, "active", "504341441825418");
    }

    /* cancel a mutualfunds sip */
    public void cancelMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        kiteConnect.cancelMFSIP("504341441825418");
        System.out.println("cancel sip successful");
    }

    /* retrieve all mutualfunds sip */
    public void getMFSIPS(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFSIP> sips = kiteConnect.getMFSIPs();
        System.out.println("mf sips: "+ sips.size());
    }

    /* retrieve individual mutualfunds sip */
    public void getMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("mf sip: "+ kiteConnect.getMFSIP("291156521960679").instalments);
    }

    /** Demonstrates com.zerodhatech.ticker connection, subcribing for instruments, unsubscribing for instruments, set mode of tick data, com.zerodhatech.ticker disconnection*/
    public void tickerUsage(KiteConnect kiteConnect, ArrayList<Long> tokens) throws IOException, WebSocketException, KiteException {
        /** To get live price use websocket connection.
         * It is recommended to use only one websocket connection at any point of time and make sure you stop connection, once user goes out of app.
         * custom url points to new endpoint which can be used till complete Kite Connect 3 migration is done. */
        final KiteTicker tickerProvider = new KiteTicker(kiteConnect.getAccessToken(), kiteConnect.getApiKey());

        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                /** Subscribe ticks for token.
                 * By default, all tokens are subscribed for modeQuote.
                 * */
               // tickerProvider.subscribe(tokens);
              //  tickerProvider.setMode(tokens, KiteTicker.modeFull);
            }
        });

        tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                // your code goes here
            }
        });

        /** Set listener to get order updates.*/
        tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
            @Override
            public void onOrderUpdate(Order order) {
                System.out.println("order update "+order.orderId);
            }
        });

        /** Set error listener to listen to errors.*/
        tickerProvider.setOnErrorListener(new OnError() {
            @Override
            public void onError(Exception exception) {
                //handle here.
            }

            @Override
            public void onError(KiteException kiteException) {
                //handle here.
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });

        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(ArrayList<Tick> ticks) {
                NumberFormat formatter = new DecimalFormat();
                System.out.println("ticks size "+ticks.size());
                if(ticks.size() > 0) {
                    System.out.println("last price "+ticks.get(0).getLastTradedPrice());
                    System.out.println("open interest "+formatter.format(ticks.get(0).getOi()));
                    System.out.println("day high OI "+formatter.format(ticks.get(0).getOpenInterestDayHigh()));
                    System.out.println("day low OI "+formatter.format(ticks.get(0).getOpenInterestDayLow()));
                    System.out.println("change "+formatter.format(ticks.get(0).getChange()));
                    System.out.println("tick timestamp "+ticks.get(0).getTickTimestamp());
                    System.out.println("tick timestamp date "+ticks.get(0).getTickTimestamp());
                    System.out.println("last traded time "+ticks.get(0).getLastTradedTime());
                    System.out.println(ticks.get(0).getMarketDepth().get("buy").size());
                }
            }
        });
        // Make sure this is called before calling connect.
        tickerProvider.setTryReconnection(true);
        //maximum retries and should be greater than 0
        tickerProvider.setMaximumRetries(10);
        //set maximum retry interval in seconds
        tickerProvider.setMaximumRetryInterval(30);

        /** connects to com.zerodhatech.com.zerodhatech.ticker server for getting live quotes*/
        tickerProvider.connect();

        /** You can check, if websocket connection is open or not using the following method.*/
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);

        /** set mode is used to set mode in which you need tick for list of tokens.
         * Ticker allows three modes, modeFull, modeQuote, modeLTP.
         * For getting only last traded price, use modeLTP
         * For getting last traded price, last traded quantity, average price, volume traded today, total sell quantity and total buy quantity, open, high, low, close, change, use modeQuote
         * For getting all data with depth, use modeFull*/
        tickerProvider.setMode(tokens, KiteTicker.modeLTP);

        // Unsubscribe for a token.
        tickerProvider.unsubscribe(tokens);

        // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
        tickerProvider.disconnect();
    }
}
