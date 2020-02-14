package com.robotrade.entrypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.robotrade.authentication.UserAuthentication;
import com.robotrade.controllers.KiteConnectController;
import com.robotrade.service.KiteApiServices;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Quote;

@EnableScheduling
@SpringBootApplication
@Component
public class RoboTradeApplication {

	public static String[] instruments = {"NSE:AARTIDRUGS","NSE:AARTIIND","NSE:ABB","NSE:ACCELYA","NSE:AJANTPHARM","NSE:ALBERTDAVD","NSE:ALKYLAMINE","NSE:AMARAJABAT","NSE:AMBIKCO","NSE:AMRUTANJAN","NSE:ANUP","NSE:APLLTD","NSE:APOLLOPIPE","NSE:APOLSINHOT","NSE:ARMANFIN","NSE:ASTEC","NSE:ASTRAL","NSE:ATFL","NSE:AUBANK","NSE:AUROPHARMA","NSE:AUTOAXLES","NSE:AVANTIFEED","NSE:AXISBANK","NSE:BAJAJELEC","NSE:BALAMINES","NSE:BALKRISIND","NSE:BANARISUG","NSE:BANDHANBNK","NSE:BASF","NSE:BBL","NSE:BBTC","NSE:BEML","NSE:BERGEPAINT","NSE:BHARATFORG","NSE:BHARTIARTL","NSE:BIRLACORPN","NSE:BLUESTARCO","NSE:BPCL","NSE:BSE","NSE:CANFINHOME","NSE:CARERATING","NSE:CEATLTD","NSE:CENTUM","NSE:CENTURYTEX","NSE:CESC","NSE:CHOLAHLDNG","NSE:CIPLA","NSE:CLNINDIA","NSE:COLPAL","NSE:CONCOR","NSE:COROMANDEL","NSE:CREDITACC","NSE:CUMMINSIND","NSE:CYIENT","NSE:DABUR","NSE:DALBHARAT","NSE:DEEPAKNTR","NSE:DHANUKA","NSE:DIAMONDYD","NSE:DYNAMATECH","NSE:ECLERX","NSE:ENDURANCE","NSE:ERIS","NSE:ESCORTS","NSE:EXCELINDUS","NSE:FAIRCHEM","NSE:FIEMIND","NSE:FINPIPE","NSE:FLUOROCHEM","NSE:FMGOETZE","NSE:FORCEMOT","NSE:FOSECOIND","NSE:FSC","NSE:GEPIL","NSE:GKWLIMITED","NSE:GLAXO","NSE:GMBREW","NSE:GODFRYPHLP","NSE:GODREJAGRO","NSE:GODREJCP","NSE:GODREJIND","NSE:GODREJPROP","NSE:GRASIM","NSE:GREENLAM","NSE:GRINDWELL","NSE:GRPLTD","NSE:GULFOILLUB","NSE:HAL","NSE:HARITASEAT","NSE:HATSUN","NSE:HAVELLS","NSE:HCLTECH","NSE:HDFCBANK","NSE:HDFCLIFE","NSE:HEG","NSE:HGS","NSE:HIL","NSE:HNDFDS","NSE:HONDAPOWER","NSE:ICICIBANK","NSE:ICICIGI","NSE:ICICIPRULI","NSE:IFBIND","NSE:IGL","NSE:IIFLWAM","NSE:IMPAL","NSE:INDIGO","NSE:INDUSINDBK","NSE:INEOSSTYRO","NSE:INFY","NSE:INGERRAND","NSE:INOXLEISUR","NSE:INSECTICID","NSE:IPCALAB","NSE:IRCON","NSE:ISEC","NSE:JBCHEPHARM","NSE:JKCEMENT","NSE:JUBILANT","NSE:JUSTDIAL","NSE:KAJARIACER","NSE:KALPATPOWR","NSE:KANSAINER","NSE:KEI","NSE:KENNAMET","NSE:KINGFA","NSE:KIRLOSIND","NSE:KKCL","NSE:KPRMILL","NSE:KSB","NSE:KSCL","NSE:KUANTUM","NSE:LAURUSLABS","NSE:LICHSGFIN","NSE:LINDEINDIA","NSE:LT","NSE:LUMAXIND","NSE:LUPIN","NSE:M&M","NSE:MAHLIFE","NSE:MAHLOG","NSE:MAITHANALL","NSE:MAJESCO","NSE:MASFIN","NSE:MASTEK","NSE:MATRIMONY","NSE:MAZDA","NSE:MCDOWELL-N","NSE:MCX","NSE:MFSL","NSE:MGL","NSE:MINDTREE","NSE:MMFL","NSE:MOTILALOFS","NSE:MPHASIS","NSE:MUTHOOTCAP","NSE:MUTHOOTFIN","NSE:NATCOPHARM","NSE:NAVINFLUOR","NSE:NDGL","NSE:NESCO","NSE:NEULANDLAB","NSE:NILKAMAL","NSE:NIPPOBATRY","NSE:NSIL","NSE:OBEROIRLTY","NSE:OCCL","NSE:PERSISTENT","NSE:PHOENIXLTD","NSE:PNBHOUSING","NSE:POLYCAB","NSE:POLYPLEX","NSE:POWERMECH","NSE:PSPPROJECT","NSE:PUNJABCHEM","NSE:QUESS","NSE:RADICO","NSE:RAJESHEXPO","NSE:RAMCOCEM","NSE:RANEHOLDIN","NSE:RATNAMANI","NSE:RAYMOND","NSE:RBL","NSE:RELAXO","NSE:RELIANCE","NSE:REPRO","NSE:REVATHI","NSE:RIIL","NSE:SAFARI","NSE:SAGCEM","NSE:SANDESH","NSE:SASKEN","NSE:SBILIFE","NSE:SEAMECLTD","NSE:SHANKARA","NSE:SHARDAMOTR","NSE:SHOPERSTOP","NSE:SHRIRAMCIT","NSE:SIS","NSE:SMLISUZU","NSE:SOBHA","NSE:SOLARA","NSE:SOLARINDS","NSE:SOTL","NSE:SPANDANA","NSE:SRTRANSFIN","NSE:SSWL","NSE:STAR","NSE:SUDARSCHEM","NSE:SUMMITSEC","NSE:SUNDRMFAST","NSE:SUNPHARMA","NSE:SUNTV","NSE:SUPREMEIND","NSE:SWARAJENG","NSE:SYMPHONY","NSE:TATACHEM","NSE:TATACOMM","NSE:TATAELXSI","NSE:TATAINVEST","NSE:TATAMETALI","NSE:TATASTEEL","NSE:TATASTLLP","NSE:TCIEXP","NSE:TCNSBRANDS","NSE:TECHM","NSE:THANGAMAYL","NSE:THERMAX","NSE:THYROCARE","NSE:TIINDIA","NSE:TIMKEN","NSE:TITAN","NSE:TRENT","NSE:TTKHLTCARE","NSE:TVSMOTOR","NSE:UBL","NSE:UJJIVAN","NSE:UPL","NSE:VADILALIND","NSE:VAIBHAVGBL","NSE:VARROC","NSE:VBL","NSE:VESUVIUS","NSE:VHL","NSE:VINATIORGA","NSE:VINDHYATEL","NSE:VIPIND","NSE:VOLTAMP","NSE:VOLTAS","NSE:VSTTILLERS","NSE:VTL","NSE:WESTLIFE","NSE:WHEELS","NSE:ZYDUSWELL"};

	public static KiteConnect kiteConnect;
	public static Map<String, Quote> instrumentsOld=new HashMap<String, Quote>();
	public static Map<String, Quote> instrumentsNew=new HashMap<String, Quote>();
	public static Map<String, Quote> instrumentsTrending=new HashMap<String, Quote>();
	public static List<String> placeOrderStock = new ArrayList<String>();
	

	int Count=0;

	public static void main(String[] args) {
		SpringApplication.run(RoboTradeApplication.class, args);
	}

	public RoboTradeApplication () {		
		UserAuthentication loginToKite=new UserAuthentication();
		try {
			kiteConnect=loginToKite.userLogin();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KiteException e) {
			e.printStackTrace();
		}		
	}

	@Scheduled(fixedDelay = 120000)
	public void findTrandingStocks() {
		KiteConnectController kiteController=new KiteConnectController();
		try {

			instrumentsNew=new HashMap<String, Quote>();
			instrumentsNew=kiteController.getNSEStockQuote(kiteConnect,instrumentsNew);
			if(instrumentsOld.size() > 0) {
				kiteController.comparethePriceWithOld(instrumentsNew,instrumentsOld,instrumentsTrending);
			}
			instrumentsOld = new HashMap<String, Quote>();
			instrumentsOld = instrumentsNew;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (KiteException e) {
			e.printStackTrace();
		}

	}

	/*@Scheduled(fixedDelay = 120000)
	public void findStocksAbove200() {
		KiteApiServices apiService=new KiteApiServices();
		try {
			apiService.getLTP(kiteConnect);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KiteException e) {
			e.printStackTrace();
		}
	}*/
}
