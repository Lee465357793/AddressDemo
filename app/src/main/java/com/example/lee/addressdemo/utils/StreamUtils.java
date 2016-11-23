package com.example.lee.addressdemo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 输入流 转 String
 */
public class StreamUtils {
	public static String getStringFromStream(InputStream is){
		String body = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = -1;
		try {
			while((len = is.read(b)) != -1){
				baos.write(b, 0, len);
			}
			body = baos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}
}