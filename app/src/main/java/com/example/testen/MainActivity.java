package com.example.testen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private TextView textView,textView1;
    private EditText editX,editY,editZ;
    private int x,y;
    private Map<String,APInfo> APs;
    private List<APInfo> allAP;
    private List<APInfo> APList;
    private static URL url;
    private static URLConnection urlCon;
    private Thread thread;

    private void init(){
        x = -1;
        y = -1;
        APList = new ArrayList<APInfo>();
        APs = new HashMap<String,APInfo>();
        allAP = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button scanButton = findViewById(R.id.scanButton);
        Button recordButton = findViewById(R.id.recordButton);
        Button clearButton = findViewById(R.id.clearButton);
        Button sendButton = findViewById(R.id.sendButton);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.editTextZ);
        editX = findViewById(R.id.editTextX);
        editY = findViewById(R.id.editTextY);
        editZ = findViewById(R.id.editTextZ);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        init();

        checkPermission();

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("");
                APList.clear();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(500);
                        }catch(InterruptedException e){e.printStackTrace();}
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkPermission();
                                ScanWifiInfo();
                            }
                        });
                    }
                });
                t.start();
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APList.clear();
                for (APInfo value : APs.values()) {
                    allAP.add(value);
                }
                allAP.sort(new Comparator<APInfo>() {
                    @Override
                    public int compare(APInfo ap1, APInfo ap2) {
                        Integer ct1 = ap1.getCount();
                        Integer ct2 = ap2.getCount();
                        return ct2.compareTo(ct1);
                    }
                });
                StringBuilder sendBuilder= new StringBuilder();
                sendBuilder.append("当前坐标: "+editX.getText()+","+editY.getText()+","+editZ.getText()+"\n");
                for(int i = 0; i < 10; i++){
                    APList.add(allAP.get(i));
                    sendBuilder.append("\n设备名："+APList.get(i).getName()
                            +"\n设备位置："+APList.get(i).getBSSID()
                            +"\n信号强度："+APList.get(i).getLevel()/APList.get(i).getCount()+"\n");
                }
                textView.setText(sendBuilder);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                textView1.setText("");
                APList.clear();
                allAP.clear();
                APs.clear();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APList.sort(new Comparator<APInfo>() {
                    @Override
                    public int compare(APInfo ap1, APInfo ap2) {
                        String name1 = ap1.getBSSID();
                        String name2 = ap2.getBSSID();
                        return name2.compareTo(name1);
                    }
                });
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            if(APList.size() < 10 || editX.getText() == null|| editY.getText() == null||editZ.getText() == null){
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, x+","+y+"缺少数据",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else{
                                String res = connectHttp("http://192.168.3.13:8080/position/createNewPS"+"/"+editX.getText()+"/"+editY.getText()+"/"+editZ.getText()+
                                        "/"+ APList.get(0).getBSSID()+"/"+(-APList.get(0).getLevel()/APList.get(0).getCount())+
                                        "/"+ APList.get(1).getBSSID()+"/"+(-APList.get(1).getLevel()/APList.get(1).getCount())+
                                        "/"+ APList.get(2).getBSSID()+"/"+(-APList.get(2).getLevel()/APList.get(2).getCount())+
                                        "/"+ APList.get(3).getBSSID()+"/"+(-APList.get(3).getLevel()/APList.get(3).getCount())+
                                        "/"+ APList.get(4).getBSSID()+"/"+(-APList.get(4).getLevel()/APList.get(4).getCount())+
                                        "/"+ APList.get(5).getBSSID()+"/"+(-APList.get(5).getLevel()/APList.get(5).getCount())+
                                        "/"+ APList.get(6).getBSSID()+"/"+(-APList.get(6).getLevel()/APList.get(6).getCount())+
                                        "/"+ APList.get(7).getBSSID()+"/"+(-APList.get(7).getLevel()/APList.get(7).getCount())+
                                        "/"+ APList.get(8).getBSSID()+"/"+(-APList.get(8).getLevel()/APList.get(8).getCount())+
                                        "/"+ APList.get(9).getBSSID()+"/"+(-APList.get(9).getLevel()/APList.get(9).getCount()));
                                switch (res){
                                    case "0":
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this, "发送坐标"+ editX.getText() +","+ editY.getText() +"的数据成功",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                        break;
                                    case "-1":
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this, editX.getText()+","+ editY.getText() +"数据重复",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                        break;
                                    default:
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
                textView.setText("");
                textView1.setText("");
                APList.clear();
                allAP.clear();
                APs.clear();
            }
        });
    }

    private void ScanWifiInfo(){
        WifiManager wifiManager= (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
//        if(! wifiManager.isWifiEnabled()){
//            if(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING){
//                Toast.makeText(this, "请打开WIFI", Toast.LENGTH_SHORT).show();
//            }
//        }
        StringBuilder scanBuilder= new StringBuilder();
        List<ScanResult> scanResults=wifiManager.getScanResults();//搜索到的设备列表
        scanResults.sort(new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult poet1, ScanResult poet2) {
                Integer score1 = poet1.level;
                Integer score2 = poet2.level;
                return score2.compareTo(score1);
            }
        });
        System.err.println("hasWifi "+scanResults.size());
        int count = 0;
        for (ScanResult scanResult : scanResults) {
            //排除本机热点干扰
            if(scanResult.SSID.equals("OPPO A95 5G"))continue;
            System.err.println("count="+count);
            scanBuilder.append("\n设备名："+scanResult.SSID
                    +"\n设备位置："+scanResult.BSSID
                    +"\n信号强度："+scanResult.level+"\n");
            if(APs.containsKey(scanResult.BSSID)){
                System.err.println("有了有了");
                APInfo info = APs.get(scanResult.BSSID);
                info.setCount(info.getCount()+1);
                info.setLevel(info.getLevel()+scanResult.level);
                APs.replace(scanResult.BSSID,info);
            }else{
                System.err.println("还没有");
                APs.put(scanResult.BSSID,new APInfo(scanResult.level,scanResult.BSSID,scanResult.SSID));
            }
            count++;
            if(count >= 10)break;
        }
        textView.setText(scanBuilder);
        textView1.setText("hasWifi "+scanResults.size());
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},
                        REQUEST_CODE
                );
                Toast.makeText(this, "正在授权！", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("getPermission", "checkPermission: 已经授权！");
            }
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String connectHttp(String urlPath) throws Exception {
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
        InputStream inStream = urlCon.getInputStream();
        System.err.println("connect success");
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        System.err.println("read success");
        inStream.close();
        return new String(outStream.toByteArray());//通过out.Stream.toByteArray获取到写的数据
    }
}

