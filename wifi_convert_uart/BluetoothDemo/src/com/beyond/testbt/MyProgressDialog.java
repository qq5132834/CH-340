package com.beyond.testbt;


import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author 513283439@qq.com
 * */
public class MyProgressDialog {
	private Context context = null;
	private MyProgressDialog myProgressDialog = null;
	private ProgressDialog progressDialog = null;
	
	public MyProgressDialog(Context context){
		this.context = context;
	}
 
	/**
	 * @author 513283439@qq.com
	 * @param title
	 * @param content
	 * */
	public void openProgressDialog(String title,String content){
		if(this.context!=null){
			progressDialog = ProgressDialog.show(this.context, title, content, true);
		}
	}
	
	/**
	 * @author 513283439@qq.com
	 * */
	public void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
}
