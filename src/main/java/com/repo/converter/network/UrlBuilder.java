/*
 *  Copyright (C) 2016-2017 Tomas Bajus ICO: 04871774.
 */
package com.repo.converter.network;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class UrlBuilder {

	private StringBuilder query = new StringBuilder();

	public static final String HOST = "http://www.google-analytics.com/collect";

	//verze GTM protokolu
	private static final String V_PARAM = "1";
	//typ GA hitu
	private static final String T_PARAM = "event";
	//GA ID
	private static final String TID_PARAM = "UA-23166937-1";

	//kategorie události
	private final static String EC_PARAM = "CMB Potvrzený lead";

	// ID Z XML
	private String cid;

	//Dovol%C3%A1no%20%E2%80%93%20nem%C3%A1%20z%C3%A1jem - Výsledek z XLS (GA Akce události)
	private String ea;
	//Multiobor - Obor z XLS (GA Štítek události)
	private String el;
	// ID Z XML
	private String cd15;
	//Dovol%C3%A1no%20%E2%80%93%20nem%C3%A1%20z%C3%A1jem - Výsledek z XLS (GA Akce události)
	private String cd17;

	//Multiobor - Obor z XLS (GA vlastní dimenze na úrovni uživatele)
	private String cd18;

	public UrlBuilder() {
		query.append("v=").append(V_PARAM).append("&t=").append(T_PARAM)
				.append("&tid=").append(TID_PARAM).append("&ec=").append(encode(EC_PARAM));
	}

	public void setCid(String cid) {
		String encodedCid = encode(cid);
		this.cid = encodedCid;
		this.cd15 = encodedCid;
		query.append("&cid=").append(encodedCid);
		query.append("&cd15=").append(encodedCid);
	}

	public void setEa(String ea) {
		String encodedEA = encode(ea);
		this.ea = encodedEA;
		this.cd17 = encodedEA;
		query.append("&ea=").append(encodedEA);
		query.append("&cd17=").append(encodedEA);
	}


	public void setEl(String el) {
		String encodedEl = encode(el);
		this.el = encodedEl;
		this.cd18 = encodedEl;
		query.append("&el=").append(encodedEl);
		query.append("&cd18=").append(encodedEl);
	}


	public String build()  {
		return query.toString();
	}

	public static boolean isValid(String url, UrlBuilder urlBuilder) {
		try {
			URL constructedUrl = new URL(url);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(urlBuilder.toString());
			return false;
		}
		return true;
	}

	private String encode(String input) {
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("UrlBuilder{");
		sb.append("V_PARAM='").append(V_PARAM).append('\'');
		sb.append(", T_PARAM='").append(T_PARAM).append('\'');
		sb.append(", TID_PARAM='").append(TID_PARAM).append('\'');
		sb.append(", EC_PARAM='").append(EC_PARAM).append('\'');
		sb.append(", cid='").append(cid).append('\'');
		sb.append(", ea='").append(ea).append('\'');
		sb.append(", el='").append(el).append('\'');
		sb.append(", cd15='").append(cd15).append('\'');
		sb.append(", cd17='").append(cd17).append('\'');
		sb.append(", cd18='").append(cd18).append('\'');
		sb.append(", query='").append(query).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
