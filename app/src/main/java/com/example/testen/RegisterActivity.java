package com.example.testen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class RegisterActivity extends AppCompatActivity {
    private static URL url;
    private static URLConnection urlCon;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button button0 = findViewById(R.id.button0);
        Button button = findViewById(R.id.scanButton);
        Button button1 = findViewById(R.id.button1);

        EditText t1 = findViewById(R.id.userName);
        EditText t2 = findViewById(R.id.phoneId);
        EditText t3 = findViewById(R.id.password);
        EditText t4 = findViewById(R.id.password1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread.start();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1.setText("");
                t2.setText("");
                t3.setText("");
                t4.setText("");

            }
        });

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    connectHttp("http://192.168.129.16:8080/register");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Cannot connect", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

        thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    sendInfo();
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(RegisterActivity.this, "Cannot send", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void sendInfo() throws Exception{
        urlCon.setDoOutput(true);
        urlCon.setRequestProperty("Content-Type", "application/json");
        // POST请求
        DataOutputStream out = new DataOutputStream(urlCon.getOutputStream());
        JSONObject obj = new JSONObject();
        String json = "{\"userId\":\"darliron\",\"password\":\"Aa@1789\",\"email\":\"123123123@qq.com\"," +
                "\"phone\":\"18612536588\",\"gender\":\"1\",\"age\":\"18\",\"face\":\"23\",\"sign\":\"Hello World\"}";
        out.writeBytes(json);
        System.err.println("请求成功");
        out.flush();
        out.close();

        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
        String lines;
        StringBuffer sb = new StringBuffer("");
        while ((lines = reader.readLine()) != null) {
            lines = URLDecoder.decode(lines, "utf-8");
            sb.append(lines);
        }
        System.err.println(sb);
        reader.close();
    }

    public static void connectHttp(String urlPath) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        url = new URL(urlPath);
        System.err.println("connect ready");
        urlCon = url.openConnection();
        urlCon.setDoOutput(false);
        urlCon.setDoInput(true);
        urlCon.setConnectTimeout(40000);
        urlCon.setReadTimeout(40000);
        urlCon.setUseCaches(false);
        System.err.println("connect success");
//        InputStream inStream = urlCon.getInputStream();
//        while ((len = inStream.read(data)) != -1) {
//            outStream.write(data, 0, len);
//        }
//        System.err.println("read success");
//        inStream.close();
//        return new String(outStream.toByteArray());//通过out.Stream.toByteArray获取到写的数据
    }
}