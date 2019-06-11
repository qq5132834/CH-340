package io.vov.com.example.snail_mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MQTT_service extends Service {
	private String TelephonyIMEI="";//获取手机IMEI号
	private MqttClient mqttClient;
	private MqttConnectOptions mqttConnectOptions;
	
	String MqttUserString = "";//用户名
    String MqttPwdString = "";//密码
	String MqttServerURI = "";
	
	private Thread recordThread = null;//记录创建的连接任务,再次创建先销毁以前的
	
	
	@Override  
    public void onCreate() {  
		
        super.onCreate();
        InitMqttConnect();//初始化MQTT配置
		ConnectMqttService connectMqttService = new ConnectMqttService();//连接服务器
		connectMqttService.start();
		recordThread = connectMqttService;//记录任务
		
		IntentFilter filter = new IntentFilter();//监听的广播
        filter.addAction("Broadcast.OtherActivitySend"); 
        registerReceiver(MqttServiceReceiver, filter);
    }
	
	@Override  
    public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
    }  
	
	@Override  
    public void onDestroy() {  
        super.onDestroy();  
       
    }  
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	/*初始化Mqtt连接*/
	private void InitMqttConnect()
	{
		try 
		{
			MqttUserString = MainActivity.MqttUserString;
			MqttPwdString = MainActivity.MqttPwdString;
			MqttServerURI = "tcp://"+MainActivity.MqttIPString+":"+MainActivity.MqttPort;
			
			TelephonyIMEI =  getDeviceId(getApplicationContext())+"MqttDemo";//ClientID
			mqttClient = new MqttClient(MqttServerURI,TelephonyIMEI,new MemoryPersistence());
			
			mqttConnectOptions = new MqttConnectOptions();//MQTT的连接设置
	        
	        mqttConnectOptions.setCleanSession(true);//设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
	        
	        mqttConnectOptions.setUserName(MqttUserString);//设置连接的用户名
	        
	        mqttConnectOptions.setPassword(MqttPwdString.toCharArray());//设置连接的密码
	        
	        mqttConnectOptions.setConnectionTimeout(10);// 设置连接超时时间 单位为秒
	        
	        mqttConnectOptions.setKeepAliveInterval(5);// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
	        
	        
	        mqttClient.setCallback(new MqttCallback() {
				@Override
				public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
					// TODO Auto-generated method stub
					Intent intent = new Intent();  
					intent.setAction("Broadcast.MqttServiceSend");  
					intent.putExtra("MqttServiceSend",arg0+";;"+arg1.toString());  
					sendBroadcast(intent); 
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void connectionLost(Throwable arg0) {
					// TODO Auto-generated method stub
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					mHandler.sendMessageDelayed(msg, 3000);
				}
			});
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/*连接MQTT服务器任务*/
	class ConnectMqttService extends Thread 
	{
		public void run() {
			try 
			{
				try { mqttClient.disconnect();} catch (Exception e) {}
				try {mqttClient.close();} catch (Exception e) {}
				Thread.sleep(1000);
				
				InitMqttConnect();
				mqttClient.connect(mqttConnectOptions);
				mqttClient.subscribe(MainActivity.SubscribeString,0);
			} catch (MqttSecurityException e) {//已经连接了,
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, 3000);
			} catch (MqttException e) {//连接时没有网络,什么原因造成的连接不正常,正在进行连接
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, 3000);
			}
			catch (Exception e) {
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, 3000);
			}
		}
	}
	private Handler mHandler = new Handler()
	{
		@Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        if (msg.what == 1) 
	        {
	        	try {recordThread.interrupt();} catch (Exception e) {}//终止以前的线程
	        	ConnectMqttService connectMqttService = new ConnectMqttService();//连接服务器
	    		connectMqttService.start();
	    		recordThread = connectMqttService;//记录任务
	        }
		}
	};
	/*该类的广播接收程序*/
	private BroadcastReceiver MqttServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try 
			{
				String msgString = intent.getStringExtra("OtherActivitySend");
				
				if (msgString != null) 
				{
					String tempString[] = msgString.split(";;");
					if (tempString[0].equals("ResetMqtt")) {
						Message msg = mHandler.obtainMessage();
						msg.what = 1;
						mHandler.sendMessageDelayed(msg, 10);
						Toast.makeText(getApplicationContext(), "重新配置MQTT", 500).show();
					}
					else if (tempString[0].equals("SendData")){
						MqttMessage msgMessage = new MqttMessage(tempString[1].getBytes());
						try {
							mqttClient.publish(MainActivity.PublishString,msgMessage);
						} catch (MqttPersistenceException e) {
						} catch (MqttException e) {
						}
					}
				}
			} catch (Exception e) {
			}
		}
	};
	/*获取手机IMEI号*/
	private static String getDeviceId(Context context) {
	    String id;
	    //android.telephony.TelephonyManager
	    TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    if (mTelephony.getDeviceId() != null) {
	        id = mTelephony.getDeviceId();
	    } else {
	        //android.provider.Settings;
	        id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
	    }
	    return id;
	}

}
