package com.example.wificlient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private Button connButton;
	private Button leftButton;
	private Button rightButton;
	private Button fontButton;
	private Button backButton;
	private Button stopButton;
	public Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		 mHandler=new Handler(){  
			 
		        public void handleMessage(Message msg) {  
		        	int what = msg.what;
		        	Bundle bundle = msg.getData();
		        	String val = (String) bundle.get("key");
		        	if("create".equals(val)){
		        		new Thread(){
							@Override
							public void run() {
								boolean st = ConnectAP.createConnect();
							}
		        		}.start();
		        		
		        	}else{
		        		ConnectAP.sentData(val);
		        	}
		        	Toast.makeText(MainActivity.this, val, Toast.LENGTH_LONG).show();
		        }     
		    }; 
		    
		//³õÊ¼»¯¿Ø¼þ
		this.connButton = (Button) this.findViewById(R.id.connect);
		this.leftButton = (Button) this.findViewById(R.id.left);
		this.rightButton = (Button) this.findViewById(R.id.right);
		this.fontButton = (Button) this.findViewById(R.id.front);
		this.backButton = (Button) this.findViewById(R.id.back);
		this.stopButton = (Button) this.findViewById(R.id.stop);
		
		this.connButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				Bundle data = new Bundle();
				data.putString("key", "create");
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		});
		
		this.leftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				Bundle data = new Bundle();
				data.putString("key", "left");
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		});
		
		this.rightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				Bundle data = new Bundle();
				data.putString("key", "right");
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		});
		
		this.fontButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Message msg = new Message();
						msg.what = 1;
						Bundle data = new Bundle();
						data.putString("key", "font");
						msg.setData(data);
						mHandler.sendMessage(msg);
					}
				});
		
		this.backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				Bundle data = new Bundle();
				data.putString("key", "back");
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		});
		
		this.stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				Bundle data = new Bundle();
				data.putString("key", "stop");
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
