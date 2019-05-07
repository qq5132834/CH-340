package com.beyond.testbt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class HttpWork {
	
	private String ip="";
	private Handler mHandler;
	private String time ="";
	private String lon = "";
	private String lat = "";
	private String data = "";
	private Context context;
	/*
	 * ip地址；
	 * time时间
	 * lon经度
	 * lat纬度
	 * data来自arduino的数据
	 * http://192.168.0.104:8080/taobao/taobao.jsp
	 * */
	public HttpWork(String ip,String time,String lon,String lat,String data,Handler handle,Context context){
		this.data =data;
		this.time = time;
		this.lat =lat;
		this.lon =lon;
		this.mHandler =handle;
		this.ip=ip;
		this.context=context;
	}
	
	public String executeHttpPost() {
        String result = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL("http://"+ip+":8080/taobao/taobao.jsp");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            DataOutputStream dop = new DataOutputStream(connection.getOutputStream());
            dop.writeBytes("time="+this.time);
            dop.writeBytes("&lon="+this.lon);
            dop.writeBytes("&lat="+this.lat);
            dop.writeBytes("&data="+this.data);
            dop.flush();
            dop.close();
 
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
            //Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
 
        }
        return result;
    }
}
