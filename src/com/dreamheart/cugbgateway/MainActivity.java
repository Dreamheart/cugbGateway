package com.dreamheart.cugbgateway;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@TargetApi(11)
public class MainActivity extends Activity {
	GatewayController controller = null;
	
    TextView tv=null; 
    TextView tv_time=null; 
    TextView tv_in=null; 
    TextView tv_out=null; 
    Button login_button=null;
    Button logout_button=null;
    EditText text_username=null;
    EditText text_password=null;
	
    @TargetApi(11)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        controller = new GatewayController();

     // 详见StrictMode文档  
     StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
             .detectDiskReads()  
             .detectDiskWrites()  
             .detectNetwork()   // or .detectAll() for all detectable problems  
             .penaltyLog()  
             .build());  
     StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
             .detectLeakedSqlLiteObjects()  
             .detectLeakedClosableObjects()  
             .penaltyLog()  
             .penaltyDeath()  
             .build());  
        
     	tv=(TextView)findViewById(R.id.tv_result);
     	tv_time=(TextView)findViewById(R.id.tv_time);
     	tv_in=(TextView)findViewById(R.id.tv_in);
     	tv_out=(TextView)findViewById(R.id.tv_out);
        text_username=(EditText)findViewById(R.id.edit_username);
        text_password=(EditText)findViewById(R.id.edit_password);
        
        login_button=(Button)findViewById(R.id.button_login);
        login_button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		String strResult = null;
        		tv.setText(strResult = controller.do_gateway(true,
        				text_username.getText().toString(),
        				text_password.getText().toString()));
        		if(!strResult.startsWith("Error")){
        			WriteSharedPreferences(
        					text_username.getText().toString(),
            				text_password.getText().toString());
        		}
        		
        		HashMap<String, String> m = controller.do_keepaive();
        		
        		tv_time.setText(m.get("lastTime"));
        		tv_in.setText(m.get("inCount"));
        		tv_out.setText(m.get("outCount"));
        	}
        });
        logout_button=(Button)findViewById(R.id.button_logout);
        logout_button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		tv.setText(controller.do_gateway(false,
        				text_username.getText().toString(),
        				text_password.getText().toString()));
        	}
        });
        
        //读取已经存储的登录凭证
        this.ReadSharedPreferences(text_username,text_password);
        text_username.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	public void ReadSharedPreferences(TextView text_username,TextView text_password)
    {
	    SharedPreferences user = getSharedPreferences("user_info",0);
	    text_username.setText(user.getString("NAME",""));
	    text_password.setText(user.getString("PASSWORD",""));
    }
    public void WriteSharedPreferences(String strName,String strPassword)
    {
	    SharedPreferences user = getSharedPreferences("user_info",0);
	    Editor userEditor=user.edit();
	    userEditor.putString("NAME", strName);
	    userEditor.putString("PASSWORD" ,strPassword);
	    userEditor.commit();
    }

}
