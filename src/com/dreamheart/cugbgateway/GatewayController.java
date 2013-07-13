package com.dreamheart.cugbgateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class GatewayController{
	String action_login="http://202.204.105.195/cgi-bin/do_login"; 
	String action_logout="http://202.204.105.195/cgi-bin/force_logout"; 
	String action_keepalive="http://202.204.105.195/cgi-bin/keeplive";
	String UID=null;
	
	DefaultHttpClient httpclient=null;
    HttpPost httpRequest=null; 
    List <NameValuePair> params=null; 
    HttpResponse httpResponse; 
    
    
    public GatewayController() {
		super();
	}

	public String do_gateway(boolean is_login,String username,String password){
    	String strResult=null;
    	/*建立HttpPost连接*/ 
        httpRequest=new HttpPost(is_login?action_login:action_logout); 
        /*Post运作传送变数必须用NameValuePair[]阵列储存*/ 
        params=new ArrayList<NameValuePair>(); 
        params.add(new BasicNameValuePair("username",username)); 
        params.add(new BasicNameValuePair("password",password)); 
        params.add(new BasicNameValuePair("drop","1")); 
        params.add(new BasicNameValuePair("type","3")); 
        params.add(new BasicNameValuePair("n",String.valueOf(is_login?99:1))); 
         
        try { 
	        httpclient = new DefaultHttpClient();
            //发出HTTP request 
            httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
            //取得HTTP response 
            httpResponse=httpclient.execute(httpRequest); 
            //若状态码为200 
            if(httpResponse.getStatusLine().getStatusCode()==200){ 
                //取出回应字串 
                strResult=EntityUtils.toString(httpResponse.getEntity()); 
                
                this.UID =strResult = decodeResult(strResult);
              
            }else{ 
                strResult = "Error Response"+httpResponse.getStatusLine().toString(); 
            } 
        } catch (Exception e) { 
            // TODO Auto-generated catch block 
            strResult = e.getMessage().toString(); 
        } 
        
        return strResult;
    }
    
    private String decodeResult(String strResult) throws Exception {
		String result  = "";
		if(strResult.startsWith("status_error")){
			result = "帐号欠费";
		}else if(strResult.startsWith("logout_error")){
			result = "您不在线上";
		}else if(strResult.startsWith("ip_exist")){
			result = "当前IP已经存在网关连接";
		}else{
			result = strResult;
		}
		
		return result;
	}

	public HashMap<String, String> do_keepaive(){
		HashMap<String, String> connStatus = new HashMap<String, String>();
		
    	/*建立HttpPost连接*/ 
        httpRequest=new HttpPost(action_keepalive); 
        /*Post运作传送变数必须用NameValuePair[]阵列储存*/ 
        params=new ArrayList<NameValuePair>(); 
        params.add(new BasicNameValuePair("uid",this.UID.substring(0, this.UID.length()-1)));
        try { 
	        httpclient = new DefaultHttpClient();
            //发出HTTP request 
            httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
            //取得HTTP response 
            httpResponse=httpclient.execute(httpRequest); 
            //若状态码为200 
            if(httpResponse.getStatusLine().getStatusCode()==200){ 
                //取出回应字串 
                String strResult=EntityUtils.toString(httpResponse.getEntity()); 
                
                String[] result = strResult.split(",");
                if(result.length>=3){
                	int time_seconds = Integer.parseInt(result[0]);
                	double in=Double.parseDouble(result[1]);
                	double out=Double.parseDouble(result[2]);
                	connStatus.put("lastTime", "已连接 : "+String.format("%d",(time_seconds/60))+"分"+(int)(time_seconds%60)+"秒");
                	connStatus.put("inCount","入流量 : "+String.format("%.2f",in/1024/1024)+"MB");
                	connStatus.put("outCount","出流量 : "+String.format("%.2f",out/1024/1024)+"MB");
                }
            }else{ 
            	connStatus.put("error","Error Response : "+httpResponse.getStatusLine().toString()); 
            } 
        } catch (Exception e) { 
            // TODO Auto-generated catch block 
            connStatus.put("error", e.getMessage().toString()); 
        } 
        
        return connStatus;
    }

	
}
