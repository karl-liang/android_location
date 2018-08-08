package com.example.administrator.myapplication;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by dell on 2015/11/4.
 */
public class HttpRequestUtil {
    /**
     * POST
     * @param url
     * @param params
     * @param headers
     */
    public static String sendPostRequest(String url,Map<String,String> params,Map<String,String> headers){
        StringBuilder buf = new StringBuilder();
        Set<Map.Entry<String, String>> entrys = null;
        if (params != null && !params.isEmpty()) {
           String json = new Gson().toJson(params);
           buf.append(json);
        }
        URL url1 = null;
        try {
            url1 = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url1.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setDoOutput(true);
        if (headers != null && !headers.isEmpty()) {
            entrys = headers.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        OutputStream out = null;
        try {
            out = conn.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.write(buf.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            conn.getResponseCode(); // 为了发送成功
        } catch (IOException e) {
            e.printStackTrace();
        }
        int code = 0 ;
        InputStream in = null;
        String response = "";
        try {
            code= conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (code == 200) {
            try {
                in = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                String readLine = null;
                while ((readLine = br.readLine()) != null) {
                    response = response + readLine;
                }
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                response = errorJson(300,e.getLocalizedMessage());
            }
        }else
        {
            response =  errorJson(code,"系统错误") ;

        }
        return response;
    }

    public static String errorJson(int code, String errmsg)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("errno:").append(code).append(",");
        sb.append("\"errmsg\":").append("\"").append(errmsg).append("\"");
        sb.append("}");

        return sb.toString();
    }
}