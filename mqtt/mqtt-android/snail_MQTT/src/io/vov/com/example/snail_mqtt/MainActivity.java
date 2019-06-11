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

	Button fontButton;//发送按钮
	Button backButton;//发送按钮
	Button leftButton;//发送按钮
	Button rightButton;//发送按钮
	
	AlertDialog mqttConfigAlertDialog = null;//MQTT配置对话框
	
	public static String MqttUserString = "snail";//用户名
	public static String MqttPwdString = "123456";//密码
	public static String MqttIPString = "39.104.168.241";//IP地址
	public static int MqttPort = 1883;//端口号
	public static String SubscribeString = "/sub";//订阅的主题
	public static String PublishString = "/pub";//发布的主题
	
    
    private SharedPreferences sharedPreferences;//存储数据
	private SharedPreferences.Editor editor;//存储数据
    
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
	 * 发送数据按钮
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
	
	/**配置MQTT对话框*/
	private void MqttConfigAlertDialog(String Title)
	{
		AlertDialog.Builder MqttConfigAlertDialog = new AlertDialog.Builder(MainActivity.this);
		View MqttConfigView = LayoutInflater.from(MainActivity.this).inflate(R.layout.mqttconfig, null);
		
		final EditText editTextMqttUser = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC1);//用户名
		final EditText editTextMqttPwd = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC2);//密码
		final EditText editTextMqttIP = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC3);//IP地址
		final EditText editTextMqttPort = (EditText) MqttConfigView.findViewById(R.id.editTextDogMC4);//端口号
		final EditText editTextMqttSub = (EditText) MqttConfigView.findViewById(R.id.EditTextDogMC5);//订阅的主题
		final EditText editTextMqttPub = (EditText) MqttConfigView.findViewById(R.id.EditTextDogMC6);//发布的主题
		
		MqttConfigAlertDialog.setIcon(R.drawable.dialog_icon);
		MqttConfigAlertDialog.setTitle(Title);
		
		MqttConfigAlertDialog.setPositiveButton("确定",null);//实现方法在下面,目的是点击按钮不关闭
		
		MqttConfigAlertDialog.setNegativeButton("默认",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	
            	MqttUserString = "snail";
				MqttPwdString = "123456";
				MqttIPString = "39.104.168.241";
				MqttPort = 1883;
				SubscribeString = "/sub";
				PublishString = "/pub";
            	
            	editor = sharedPreferences.edit();
				editor.putString("MqttUser", "snail");//用户名
				editor.putString("MqttPwd", "123456");//密码
				editor.putString("MqttIP", "39.104.168.241");//IP地址
				editor.putInt("MqttPort",1883);//端口号
				editor.putString("MqttSub","/sub");//订阅的主题
				editor.putString("MqttPub","/pub");//发布的主题
				editor.commit();
            	
				Intent intent = new Intent();  
				intent.setAction("Broadcast.OtherActivitySend");  
				intent.putExtra("OtherActivitySend","ResetMqtt;;");  
				sendBroadcast(intent); 
				
    			mqttConfigAlertDialog.dismiss();
            }
        });
		
        MqttConfigAlertDialog.setView(MqttConfigView);//对话框加载视图
		
	    mqttConfigAlertDialog  = MqttConfigAlertDialog.create();
	    
	    //初始化显示
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
	    
	    mqttConfigAlertDialog.show();//必须先显示.....
	    /*点击了确定按钮*/
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
					Toast.makeText(getApplicationContext(), "ip地址不能为空!!",500).show();
					return;
				}
				if (str4.length()==0)
				{
					Toast.makeText(getApplicationContext(), "端口号不能为空!!",500).show();
					return;
				}
				try 
				{
					//检测端口号是否在范围之内
					int port = Integer.parseInt(str4);
					if (port<0 || port>65535) {
						Toast.makeText(getApplicationContext(), "请检查端口号!!",500).show();
						return;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				String[] temp = str3.split("\\.");//解析IP数据
				try 
				{
					if (temp.length!=4) {
						Toast.makeText(getApplicationContext(), "请检查IP地址!!",500).show();
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
				editor.putString("MqttUser", MqttUserString);//用户名
				editor.putString("MqttPwd",MqttPwdString);//密码
				editor.putString("MqttIP",MqttIPString);//IP地址
				editor.putInt("MqttPort",MqttPort);//端口号
				editor.putString("MqttSub",SubscribeString);//订阅的主题
				editor.putString("MqttPub",PublishString);//发布的主题
				editor.commit();
				
				Intent intent = new Intent();  
				intent.setAction("Broadcast.OtherActivitySend");  
				intent.putExtra("OtherActivitySend","ResetMqtt;;");  
				sendBroadcast(intent); 
				
				mqttConfigAlertDialog.dismiss();
			}
		});
	}
	
	/** 当活动即将可见时调用 */
	@Override
    protected void onStart() 
	{
    	Intent startIntent = new Intent(getApplicationContext(), MQTT_service.class);  
        startService(startIntent); //启动后台服务 
        
        IntentFilter filter = new IntentFilter();//监听的广播
        filter.addAction("Broadcast.MqttServiceSend"); 
        
    	super.onStart();
    }
    /** 当活动不再可见时调用 */
    @Override
    protected void onStop() 
    {
        super.onStop();
    }
    /** 当活动注销时调用 */
	@Override
    protected void onDestroy() 
    {
        super.onDestroy();
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);//设置菜单添加图标有效
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.MqttConfig) {
			MqttConfigAlertDialog("MQTT配置");
		}
		return super.onOptionsItemSelected(item);
	}
	
	//enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效  
    protected void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            m.setAccessible(true);  
               
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)  
            m.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    } 
}