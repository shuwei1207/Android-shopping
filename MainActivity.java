package com.example.shopping;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ServerConnection sc;
    Button bt_login,bt_scan,bt_download,bt_update,bt_clear;
    EditText et_id,et_passwd;
    String s1,s2,s3;
    JSONArray ja,jb;
    String userid;
    private List<Map<String,Object>> list;
    ListView lv;
    SimpleAdapter adapter;
    String group = "imf55";
    String code = "imf55";
    TextView tp;
    int totalp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_login = (Button)findViewById(R.id.login);
        et_id = (EditText)findViewById(R.id.id);
        et_passwd = (EditText)findViewById(R.id.passwd);
        sc = new ServerConnection();
        ja = new JSONArray();
        jb = new JSONArray();
        bt_login.setOnClickListener(login);
        et_id.setText("Jack");
        et_passwd.setText("benny");
    }

    private View.OnClickListener login =new View.OnClickListener(){
        public void onClick(View v) {
            s1 = et_id.getText().toString();
            s2 = et_passwd.getText().toString();
            if(s1.equals("")||s2.equals(""))
                Toast.makeText(MainActivity.this, "請輸入正確的帳號及密碼", Toast.LENGTH_LONG).show();
            else
            {
                Thread t1 = new Thread(r1);
                t1.start();
            }
        }
    };

    private View.OnClickListener scan =new View.OnClickListener(){
        public void onClick(View v) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
// 設定參數，兩種條碼都讀
            intent.putExtra("SCAN_MODE", "SCAN_MODE");
//只判斷QRCode
//'/intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//只判斷二維條碼
//intent.putExtra("SCAN_MODE", "PRODUCT_MODE");

//因為要回傳掃描結果所以要使用startActivityForResult
// 要求回傳1
            startActivityForResult(intent, 1);
        }
    };
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

//requestCode在startActivityForResult傳入參數時決定的，如果成功的話會傳回相同的值
        if (requestCode == 1) {
//成功回傳值
            if (resultCode == RESULT_OK) {
//ZXing回傳的內容
                String contents = intent.getStringExtra("SCAN_RESULT");
                String[] ca = contents.split(";");
                String iid="";
                int j,k;
                for (int i=0;i<ca.length;i+=2)
                {
                    String qrc = ca[i];
                    String jj="";
                    int count = Integer.parseInt(ca[i+1]);
                    for(j=0;j<jb.length();j++)
                    {
                        try {
                            jj=jb.getJSONObject(j).get("B").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //找到jb 中第j個的（"B")欄位
                        if(qrc.equals(jj))
                            break;
                    }
                    if(j<jb.length())
                    {
                        try {
                            iid = jb.getJSONObject(j).get("id").toString();//jb中get(j)裡的"id"
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (k=0;k<list.size();k++)
                        {
                            if (iid.equals(list.get(k).get("id").toString()))//list中第k個的 "id")
                                break;
                        }
                        if (k<list.size()) {
                            Map<String, Object> item = list.get(k);
                            int countt = Integer.parseInt(item.get("count").toString());
                            int price = Integer.parseInt(item.get("price").toString());
                            int total = Integer.parseInt(item.get("total").toString());
                            countt = countt + Integer.parseInt(ca[i+1]);
                            item.put("total",price*countt);
                            item.put("count",countt);
                            item.put("note","單價:"+price+"      個數:"+countt);
                            totalp=totalp+price*Integer.parseInt(ca[i+1]);
                            list.set(k,item);
                            adapter.notifyDataSetChanged();

                            //寫程式

                            //更新list[k]
                        }

                        else{

                            JSONObject object= null;
                            try {
                                int countt = Integer.parseInt(ca[i+1]);
                                object = jb.getJSONObject(j);
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("id",object.getString("id"));
                                item.put("name", object.getString("C"));
                                int price = Integer.parseInt(object.getString("D"));
                                item.put("price", price);
                                item.put("count",countt);
                                item.put("total", ""+price*countt);
                                item.put("note","單價:"+price+"      個數:"+countt);
                                list.add(item);
                                totalp = totalp+price*countt;
                                tp.setText("total:"+totalp);
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                tp.setText("total"+totalp);
                //然後更新Map可參考onitemclicklistener，新增則參考handler裡msg.what==3
            }
        }
//ZXing回傳的格式
    }





    private View.OnClickListener download =new View.OnClickListener(){
        public void onClick(View v) {
            Thread t3 = new Thread(r3);
            t3.start();
            //寫程式
        }
    };

    private View.OnClickListener update =new View.OnClickListener(){
        public void onClick(View v) {
            Thread t4 = new Thread(r4);
            t4.start();
            //寫程式
        }
    };

    private View.OnClickListener clear =new View.OnClickListener(){
        public void onClick(View v) {
            list.clear();
            totalp=0;
            tp.setText("total:"+totalp);
            adapter.notifyDataSetChanged();
            //寫程式
        }
    };

    private ListView.OnItemClickListener itemclick = new ListView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> adapterview, View view, int position, long arg)
        {
            Map<String,Object> item = list.get(position);
            int count = Integer.parseInt(item.get("count").toString());
            int price = Integer.parseInt(item.get("price").toString());
            int total = Integer.parseInt(item.get("total").toString());
            count--;
            totalp=totalp-price;
            if(count==0)
                list.remove(position);
            else
            {
                item.put("total",price*count);
                item.put("count",count);
                item.put("note","單價:"+price+"      個數:"+count);
                list.set(position,item);
            }
            tp.setText("total:"+totalp);
            //寫程式
            adapter.notifyDataSetChanged();
        }
    };

    private Runnable r1 = new Runnable()
    {
        public void run()
        {
            ja = sc.query(group,code,"id,C","A='1' AND B='"+s1+"'");
            Message message = new Message();
            message.what = 1;
            h1.sendMessage(message);
        }
    };

    private Runnable r2 = new Runnable()
    {
        public void run()
        {
            sc.insert(group,code,"A,B,C","'1','"+s1+"','"+s2+"'");
            ja = sc.query(group,code,"id","A='1' AND B='"+s1+"'");
            Message message = new Message();
            message.what = 2;
            h1.sendMessage(message);
        }
    };

    private Runnable r3 = new Runnable()
    {
        public void run()
        {
            jb = sc.query(group,code,"id,B,C,D","A='2'");
            ja = sc.query(group,code,"B,C,D","A='3' AND B='"+userid+"'");
            Message message = new Message();
            message.what = 3;
            h1.sendMessage(message);
        }
    };

    private Runnable r4 = new Runnable()
    {
        public void run()
        {
            sc.delete(group,code,"A='3' AND B='"+userid+"'");
            for (int k=0;k<list.size();k++)
            {
                Map<String, Object> item = list.get(k);
                int ccount = Integer.parseInt(item.get("count").toString());
                String itemm =item.get("id").toString();
                sc.insert(group,code,"A,B,C,D","'3','"+userid+"','"+itemm+"','"+ccount+"'");

            }
        }
    };

    Handler h1 = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==1) {
                if(ja.length()==0)
                {
                    Thread t2 = new Thread(r2);
                    t2.start();
                }
                else {
                    try {
                        if (ja.getJSONObject(0).getString("C").equals(s2)) {
                            Toast.makeText(MainActivity.this, "密碼正確，歡迎光臨", Toast.LENGTH_LONG).show();
                            userid = ja.getJSONObject(0).getString("id");

                            setContentView(R.layout.layout);
                            lv = (ListView)findViewById(R.id.listView);
                            bt_scan = (Button)findViewById(R.id.buttonscan);
                            bt_scan.setOnClickListener(scan);
                            bt_download = (Button)findViewById(R.id.buttondownload);
                            bt_download.setOnClickListener(download);
                            bt_update = (Button)findViewById(R.id.buttonupdate);
                            bt_update.setOnClickListener(update);
                            bt_clear = (Button)findViewById(R.id.buttonclear);
                            bt_clear.setOnClickListener(clear);
                            list = new ArrayList<Map<String, Object>>();
                            adapter = new SimpleAdapter(MainActivity.this,list,R.layout.listitem,new String[]{"name","total","note"},new int[]{R.id.itemname, R.id.itemtotal, R.id.itemnote});
                            tp=(TextView)findViewById(R.id.totalprice);
                            lv.setAdapter(adapter);
                            lv.setOnItemClickListener(itemclick);
                            Thread t3 = new Thread(r3);
                            t3.start();

                        } else {
                            Toast.makeText(MainActivity.this, "密碼錯誤，請重新輸入", Toast.LENGTH_LONG).show();
                            et_passwd.setText("");
                        }
                    } catch (JSONException e) {
                    }
                }
            }
            else if(msg.what==2)
            {
                try {
                    userid = ja.getJSONObject(0).getString("id");
                } catch (JSONException e) {
                }
                setContentView(R.layout.layout);
                lv = (ListView)findViewById(R.id.listView);
                bt_scan = (Button)findViewById(R.id.buttonscan);
                bt_scan.setOnClickListener(scan);
                bt_download = (Button)findViewById(R.id.buttondownload);
                bt_download.setOnClickListener(download);
                bt_update = (Button)findViewById(R.id.buttonupdate);
                bt_update.setOnClickListener(update);
                bt_clear = (Button)findViewById(R.id.buttonclear);
                bt_clear.setOnClickListener(clear);
                tp=(TextView)findViewById(R.id.totalprice);
                list = new ArrayList<Map<String, Object>>();
                adapter = new SimpleAdapter(MainActivity.this,list,R.layout.listitem,new String[]{"name","total","note"},new int[]{R.id.itemname, R.id.itemtotal, R.id.itemnote});
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(itemclick);
                Thread t3 = new Thread(r3);
                t3.start();
            }
            else if(msg.what==3)
            {
                try {
                    list.clear();
                    totalp=0;
                    for(int i=0;i<ja.length();i++)
                    {
                        JSONObject buy = ja.getJSONObject(i);
                        for(int j=0;j<jb.length();j++)
                        {
                            JSONObject object = jb.getJSONObject(j);
                            if(buy.getString("C").equals(object.getString("id")))
                            {
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("id",object.getString("id"));
                                item.put("name", object.getString("C"));
                                int price = Integer.parseInt(object.getString("D"));
                                item.put("price", price);
                                int count = Integer.parseInt(buy.getString("D"));
                                item.put("count",count);
                                item.put("total", ""+price*count);
                                item.put("note","單價:"+price+"      個數:"+count);
                                list.add(item);
                                totalp = totalp+price*count;
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tp.setText("total:"+totalp);
                } catch (JSONException e) {
                }
            }
            //寫程式
            super.handleMessage(msg);
        }
    };
}
