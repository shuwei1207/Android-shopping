package com.example.shopping;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yuan on 2016/4/25.
 */

public class ServerConnection {

    public JSONArray query(String group, String code, String field, String where){
        String condition = "group=" + group + "&code=" + code + "&field=" + field + "&where=" + where;
        String query_result=doPost("http://140.113.72.150/imf/Android_Query.php",  condition);
        JSONArray ja=null;
        try {
            ja =  new JSONArray(query_result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    public void insert(String group, String code, String field,String value){
        String condition = "group=" + group + "&code=" + code + "&field=" + field + "&value=" + value;
        doPost("http://140.113.72.150/imf/Android_Insert.php", condition);
    }

    public void delete(String group, String code, String where){
        String condition = "group=" + group + "&code=" + code + "&where=" + where;
        doPost("http://140.113.72.150/imf/Android_Delete.php", condition);
    }

    public void update(String group, String code, String where , String set){
        String condition = "group=" + group + "&code=" + code + "&where=" + where + "&set=" + set;
        doPost("http://140.113.72.150/imf/Android_Update.php", condition);
    }

    public static String  doPost(String url, String params) {
        String result = new String();
            try {
                URL http_url=new URL(url);
                if(http_url!=null)
                {
                    HttpURLConnection conn = (HttpURLConnection) http_url.openConnection();
                    conn.setConnectTimeout(5* 1000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length",String.valueOf(params.getBytes().length) );
                    BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                    bw.write(params);
                    bw.close();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        BufferedReader buf=new BufferedReader(new InputStreamReader(is));
                        result = buf.readLine();
                        buf.close();
                        is.close();
                    }
                }
            } catch( Exception e) {
                e.printStackTrace();
            }
        return result;
    }

}
