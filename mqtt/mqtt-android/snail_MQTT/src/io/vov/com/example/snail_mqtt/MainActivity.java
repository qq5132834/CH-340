package io.vov.com.example.snail_mqtt;

import io.vov.R;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	Button fontButton;//���Ͱ�ť
	Button backButton;//���Ͱ�ť
	Button leftButton;//���Ͱ�ť
	Button rightButton;//���Ͱ�ť
	
	AlertDialog mqttConfigAlertDialog = null;//MQTT���öԻ���
	
	public static String MqttUserString = "snail";//�û���
	public static String MqttPwdString = "123456";//����
	public static String MqttIPString = "39.104.168.241";//IP��ַ
	public static int MqttPort = 1883;//�˿ں�
	public static String SubscribeString = "/sub";//���ĵ�����
	public static String PublishString = "/pub";//����������
	
    
    private SharedPreferences sharedPreferences;//�洢����
	private SharedPreferences.Editor editor;//�洢����
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedPreferences = MainActivity.this.getSharedPreferences("mqttconfig",MODE_PRIVATE );
		MqttUserString = sharedPreferences.getString("MqttUser", "snail");
		MqttPwdString = sharedPreferences.getString("MqttPwd", "123456");
		MqttIPString = sharedPreferences.getString("MqttIP", "39.104.168.241");
		MqttPort = sharedPreferences.getInt("MqttPort", 1883);
		SubscribeString = sharedPreferences.getString("MqttSub", "/sub");
		PublishString = sharedPreferences.getString("MqttPub", "/pub");
		
		
		fontButton = (Button) findViewById(R.id.button1);
		fontButton.setOnClickListener(sendButtonClick);
		
		this.backButton = (Button) findViewById(R.id.button2);
		this.backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();  
				intent.setAction("Broadcast.OtherActivitySend");  
				intent.putExtra("OtherActivitySend","SendData;;"+"back");  
				sendBroadcast(intent); 
			}
		});
		
		this.leftButton = (Button) findViewById(R.id.button3);
		this.leftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();  
				intent.setAction("Broadcast.OtherActivitySend");  
				intent.putExtra("OtherActivitySend","SendData;;"+"left");  
				sendBroadcast(intent); 
			}
		});
		
		this.rightButton = (Button) findViewById(R.id.button4);
		this.rightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();  
				intent.setAction("Broadcast.OtherActivitySend");  
				intent.putExtra("OtherActivitySend","SendData;;"+"right");  
				sendBroadcast(intent); 
			}
		});
		
	}

	
	/**
	 * �������ݰ�ť
	 */
	private OnClickListener sendButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();  
			intent.setAction("Broadcast.OtherActivitySend");  
			intent.putExtra("OtherActivitySend","SendData;;"+"font");  
			sendBroadcast(intent); 
			
		}
	};
	
	/**����MQTT�Ի���*/
	private void MqttConfigAlertDialog(String Title)
	{
		AlertDialog.Builder MqttConfigAlertDialog = new AlertDialog.Builder(MainActivity.this);
		View MqttConfigView = LayoutInflater.from(MainActivity.this).inflate(R.layout.mqttconfig, null);
		
		final EditText editTextMqttUser = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC1);//�û���
		final EditText editTextMqttPwd = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC2);//����
		final EditText editTextMqttIP = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC3);//IP��ַ
		final EditText editTextMqttPort = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC4);//�˿ں�
		final EditText editTextMqttSub = (EditText) MqttConfigView.findViewById(R.id.EditTextDogMC5);//���ĵ�����
		final EditText editTextMqttPub = (EditText) MqttConfigView.findViewById(R.id.EditTextDogMC6);//����������
		
		MqttConfigAlertDialog.setIcon(R.drawable.dialog_icon);
		MqttConfigAlertDialog.setTitle(Title);
		
		MqttConfigAlertDialog.setPositiveButton("ȷ��",null);//ʵ�ַ���������,Ŀ���ǵ����ť���ر�
		
		MqttConfigAlertDialog.setNegativeButton("Ĭ��",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	
            	MqttUserString = "snail";
				MqttPwdString = "123456";
				MqttIPString = "39.104.168.241";
				MqttPort = 1883;
				SubscribeString = "/sub";
				PublishString = "/pub";
            	
            	editor = sharedPreferences.edit();
				editor.putString("MqttUser", "snail");//�û���
				editor.putString("MqttPwd", "123456");//����
				editor.putString("MqttIP", "39.104.168.241");//IP��ַ
				editor.putInt("MqttPort",1883);//�˿ں�
				editor.putString("MqttSub","/sub");//���ĵ�����
				editor.putString("MqttPub","/pub");//����������
				editor.commit();
            	
				Intent intent = new Intent();  
				intent.setAction("Broadcast.OtherActivitySend");  
				intent.putExtra("OtherActivitySend","ResetMqtt;;");  
				sendBroadcast(intent); 
				
    			mqttConfigAlertDialog.dismiss();
            }
        });
		
        MqttConfigAlertDialog.setView(MqttConfigView);//�Ի��������ͼ
		
	    mqttConfigAlertDialog  = MqttConfigAlertDialog.create();
	    
	    //��ʼ����ʾ
	    mqttConfigAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				editTextMqttUser.setText(MqttUserString);
				editTextMqttPwd.setText(MqttPwdString);
				editTextMqttIP.setText(MqttIPString);
				editTextMqttPort.setText(MqttPort+"");
				editTextMqttSub.setText(SubscribeString);
				editTextMqttPub.setText(PublishString);
			}
		});
	    
	    mqttConfigAlertDialog.show();//��������ʾ.....
	    /*�����ȷ����ť*/
	    mqttConfigAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str1 = editTextMqttUser.getText().toString().replace(" ","");
				String str2  = editTextMqttPwd.getText().toString().replace(" ","");
				String str3 = editTextMqttIP.getText().toString().replace(" ","");
				String str4 = editTextMqttPort.getText().toString().replace(" ","");
				String str5 = editTextMqttSub.getText().toString().replace(" ","");
				String str6 = editTextMqttPub.getText().toString().replace(" ","");
				
				if (str1.length()==0) {str1 = "snail";}
				if (str2.length()==0) {str2 = "123456";}
				if (str3.length()==0)
				{
					Toast.makeText(getApplicationContext(), "ip��ַ����Ϊ��!!",500).show();
					return;
				}
				if (str4.length()==0)
				{
					Toast.makeText(getApplicationContext(), "�˿ںŲ���Ϊ��!!",500).show();
					return;
				}
				try 
				{
					//���˿ں��Ƿ��ڷ�Χ֮��
					int port = Integer.parseInt(str4);
					if (port<0 || port>65535) {
						Toast.makeText(getApplicationContext(), "����˿ں�!!",500).show();
						return;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				String[] temp = str3.split("\\.");//����IP����
				try 
				{
					if (temp.length!=4) {
						Toast.makeText(getApplicationContext(), "����IP��ַ!!",500).show();
						return;
					}
				} catch (Exception e) {
				}
				
				if (str5.length()==0) {str5 = "/sub";}
				if (str6.length()==0) {str6 = "/pub";}
				
				
				MqttUserString = str1;
				MqttPwdString = str2;
				MqttIPString = str3;
				MqttPort = Integer.parseInt(str4);
				SubscribeString = str5;
				PublishString = str6;
				
				editor = sharedPreferences.edit();
				editor.putString("MqttUser", MqttUserString);//�û���
				editor.putString("MqttPwd",MqttPwdString);//����
				editor.putString("MqttIP",MqttIPString);//IP��ַ
				editor.putInt("MqttPort",MqttPort);//�˿ں�
				editor.putString("MqttSub",SubscribeString);//���ĵ�����
				editor.putString("MqttPub",PublishString);//����������
				editor.commit();
				
				Intent intent = new Intent();  
				intent.setAction("Broadcast.OtherActivitySend");  
				intent.putExtra("OtherActivitySend","ResetMqtt;;");  
				sendBroadcast(intent); 
				
				mqttConfigAlertDialog.dismiss();
			}
		});
	}
	
	/** ��������ɼ�ʱ���� */
	@Override
    protected void onStart() 
	{
    	Intent startIntent = new Intent(getApplicationContext(), MQTT_service.class);  
        startService(startIntent); //������̨���� 
        
        IntentFilter filter = new IntentFilter();//�����Ĺ㲥
        filter.addAction("Broadcast.MqttServiceSend"); 
        
    	super.onStart();
    }
    /** ������ٿɼ�ʱ���� */
    @Override
    protected void onStop() 
    {
        super.onStop();
    }
    /** ���ע��ʱ���� */
	@Override
    protected void onDestroy() 
    {
        super.onDestroy();
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);//���ò˵����ͼ����Ч
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.MqttConfig) {
			MqttConfigAlertDialog("MQTT����");
		}
		return super.onOptionsItemSelected(item);
	}
	
	//enableΪtrueʱ���˵����ͼ����Ч��enableΪfalseʱ��Ч��4.0ϵͳĬ����Ч  
    protected void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            m.setAccessible(true);  
               
            //MenuBuilderʵ��Menu�ӿڣ������˵�ʱ����������menu��ʵ����MenuBuilder����(java�Ķ�̬����)  
            m.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    } 
}