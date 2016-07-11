/*
 *  Copyright (C) 2016-2017 Tomas Bajus ICO: 04871774.
 */
package com.repo.converter.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleAnalyticsConnector {

	private static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsConnector.class);

	private final static String USER_AGENT = "Mozilla/5.0";

	public String sendPost(String host, String params)  {
		String request = host + params;
		int responseCode = -1;
		StringBuffer response = new StringBuffer();

		try {
			URL obj = new URL(host);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(params);
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			LOG.info("Sending 'POST' request to URL : " + host + params + " Response code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception ex) {
			LOG.error("Error during sending of data to Google analytics for following url: " + host+params, ex);
		}
		return  "RESPONSE CODE "+responseCode + ", REQUEST : "+request ;
	}
}
