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
    	/*����HttpPost����*/ 
        httpRequest=new HttpPost(is_login?action_login:action_logout); 
        /*Post�������ͱ���������NameValuePair[]���д���*/ 
        params=new ArrayList<NameValuePair>(); 
        params.add(new BasicNameValuePair("username",username)); 
        params.add(new BasicNameValuePair("password",password)); 
        params.add(new BasicNameValuePair("drop","1")); 
        params.add(new BasicNameValuePair("type","3")); 
        params.add(new BasicNameValuePair("n",String.valueOf(is_login?99:1))); 
         
        try { 
	        httpclient = new DefaultHttpClient();
            //����HTTP request 
            httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
            //ȡ��HTTP response 
            httpResponse=httpclient.execute(httpRequest); 
            //��״̬��Ϊ200 
            if(httpResponse.getStatusLine().getStatusCode()==200){ 
                //ȡ����Ӧ�ִ� 
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
			result = "�ʺ�Ƿ��";
		}else if(strResult.startsWith("logout_error")){
			result = "����������";
		}else if(strResult.startsWith("ip_exist")){
			result = "��ǰIP�Ѿ�������������";
		}else{
			result = strResult;
		}
		
		return result;
	}

	public HashMap<String, String> do_keepaive(){
		HashMap<String, String> connStatus = new HashMap<String, String>();
		
    	/*����HttpPost����*/ 
        httpRequest=new HttpPost(action_keepalive); 
        /*Post�������ͱ���������NameValuePair[]���д���*/ 
        params=new ArrayList<NameValuePair>(); 
        params.add(new BasicNameValuePair("uid",this.UID.substring(0, this.UID.length()-1)));
        try { 
	        httpclient = new DefaultHttpClient();
            //����HTTP request 
            httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
            //ȡ��HTTP response 
            httpResponse=httpclient.execute(httpRequest); 
            //��״̬��Ϊ200 
            if(httpResponse.getStatusLine().getStatusCode()==200){ 
                //ȡ����Ӧ�ִ� 
                String strResult=EntityUtils.toString(httpResponse.getEntity()); 
                
                String[] result = strResult.split(",");
                if(result.length>=3){
                	int time_seconds = Integer.parseInt(result[0]);
                	double in=Double.parseDouble(result[1]);
                	double out=Double.parseDouble(result[2]);
                	connStatus.put("lastTime", "������ : "+String.format("%d",(time_seconds/60))+"��"+(int)(time_seconds%60)+"��");
                	connStatus.put("inCount","������ : "+String.format("%.2f",in/1024/1024)+"MB");
                	connStatus.put("outCount","������ : "+String.format("%.2f",out/1024/1024)+"MB");
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
