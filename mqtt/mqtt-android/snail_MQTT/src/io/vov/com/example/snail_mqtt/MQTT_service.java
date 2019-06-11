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
	private String TelephonyIMEI="";//��ȡ�ֻ�IMEI��
	private MqttClient mqttClient;
	private MqttConnectOptions mqttConnectOptions;
	
	String MqttUserString = "";//�û���
    String MqttPwdString = "";//����
	String MqttServerURI = "";
	
	private Thread recordThread = null;//��¼��������������,�ٴδ�����������ǰ��
	
	
	@Override  
    public void onCreate() {  
		
        super.onCreate();
        InitMqttConnect();//��ʼ��MQTT����
		ConnectMqttService connectMqttService = new ConnectMqttService();//���ӷ�����
		connectMqttService.start();
		recordThread = connectMqttService;//��¼����
		
		IntentFilter filter = new IntentFilter();//�����Ĺ㲥
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
	/*��ʼ��Mqtt����*/
	private void InitMqttConnect()
	{
		try 
		{
			MqttUserString = MainActivity.MqttUserString;
			MqttPwdString = MainActivity.MqttPwdString;
			MqttServerURI = "tcp://"+MainActivity.MqttIPString+":"+MainActivity.MqttPort;
			
			TelephonyIMEI =  getDeviceId(getApplicationContext())+"MqttDemo";//ClientID
			mqttClient = new MqttClient(MqttServerURI,TelephonyIMEI,new MemoryPersistence());
			
			mqttConnectOptions = new MqttConnectOptions();//MQTT����������
	        
	        mqttConnectOptions.setCleanSession(true);//�����Ƿ����session,�����������Ϊfalse��ʾ�������ᱣ���ͻ��˵����Ӽ�¼����������Ϊtrue��ʾÿ�����ӵ������������µ��������
	        
	        mqttConnectOptions.setUserName(MqttUserString);//�������ӵ��û���
	        
	        mqttConnectOptions.setPassword(MqttPwdString.toCharArray());//�������ӵ�����
	        
	        mqttConnectOptions.setConnectionTimeout(10);// �������ӳ�ʱʱ�� ��λΪ��
	        
	        mqttConnectOptions.setKeepAliveInterval(5);// ���ûỰ����ʱ�� ��λΪ�� ��������ÿ��1.5*20���ʱ����ͻ��˷��͸���Ϣ�жϿͻ����Ƿ����ߣ������������û�������Ļ���
	        
	        
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
	
	/*����MQTT����������*/
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
			} catch (MqttSecurityException e) {//�Ѿ�������,
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, 3000);
			} catch (MqttException e) {//����ʱû������,ʲôԭ����ɵ����Ӳ�����,���ڽ�������
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
	        	try {recordThread.interrupt();} catch (Exception e) {}//��ֹ��ǰ���߳�
	        	ConnectMqttService connectMqttService = new ConnectMqttService();//���ӷ�����
	    		connectMqttService.start();
	    		recordThread = connectMqttService;//��¼����
	        }
		}
	};
	/*����Ĺ㲥���ճ���*/
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
						Toast.makeText(getApplicationContext(), "��������MQTT", 500).show();
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
	/*��ȡ�ֻ�IMEI��*/
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
