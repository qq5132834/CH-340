/**
 * 
 */
package com.example.wificlient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.nfc.Tag;
import android.util.Log;

/**
 *@author ���ߵ�����  E-mail: 513283439@qq.com
 *@version ����ʱ�䣺2019��3��28��  ����12:27:22
 */
public class ConnectAP {
	
	private static Socket socketClient;
	private static PrintWriter printWriter;
	private static int times;
	
	public static boolean createConnect(){
		try {
			socketClient = new Socket("192.168.4.1",1010);
			if(socketClient.isConnected()){
				OutputStream os = socketClient.getOutputStream();
				printWriter = new PrintWriter(os);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("", e.getMessage());
		}  
		return false;
	}
	
	public static void closeConnect(){
		if(printWriter!=null){
			printWriter.close();
		}
		if(socketClient!=null){
			try {
				socketClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static boolean sentData(String data){
		if(socketClient!=null && socketClient.isConnected() && printWriter!=null){
			times=0;
			printWriter.write(data);
			printWriter.flush();
			return true;
		}
		else{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			times++;
			if(times>=2){
				return false;
			}
			createConnect();
		}
		return false;
	}
	

}
