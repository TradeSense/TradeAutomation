package com.robotrade.authentication;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.springframework.stereotype.Component;

import com.neovisionaries.ws.client.WebSocketException;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Margin;
import com.zerodhatech.models.User;

@Component
public class UserAuthentication {
	
	
	
	public KiteConnect userLogin() throws JSONException, IOException, KiteException {

        KiteConnect kiteConnect = new KiteConnect("rjf10isn126m4may");
        kiteConnect.setUserId("XY5401");

        // Get login url
        String url = kiteConnect.getLoginURL();

        System.out.println(url);
        
        // Set session expiry callback.
        kiteConnect.setSessionExpiryHook(new SessionExpiryHook() {
            @Override
            public void sessionExpired() {
                System.out.println("session expired");
            }
        });

        User user =  kiteConnect.generateSession("KBqn6qmlU9GQnXaAldAgqcArfc7SIdgk", "12hb924b99mcs0w3n5szrcd357xs7v1o");
        kiteConnect.setAccessToken(user.accessToken);
        kiteConnect.setPublicToken(user.publicToken);
        
        return kiteConnect;
	}

}
