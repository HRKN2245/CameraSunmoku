package jp.recognize.scenery.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Constants{
	public static final String RECOGNITION_URL = "https://scenery.recognize.jp/v1/scenery/api/recognition";
	public static final String API_KEY = "Q2mFRML6C3hdjZ9qi0Vd3VKoL0yQZFmHLm4R9kbKEe";
	public static final String CHARACTERS = "english+japanese";
	public static final String RETime = "[０-９][０-９][：時][０-９][０-９]";
	public static final String REAdress = "[０-９]+[－ー][０-９]+";
	public static final String REDay = "[０-９][０-９][０-９][０-９]／[０-１]?[０-９]／[０-３]?[０-９]";
	public static final String RETell = "[０-９]+[－]?[０-９]+[－]?[０-９]+";
	public static final String WORDS = "default+user("+RETime+")+user("+REAdress+")+user("+REDay+")+user("+RETell+")";
	public static final String ANALYSIS = "deep";
}

// +user("+RETime+")+user("+REAdress+")+user("+REDay+")+user("+RETell+")
//https://scenery.recognize.jp/v1/scenery/api/recognition
//https://recognize.jp/v1/scenery/api/recognition