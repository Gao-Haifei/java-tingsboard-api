package Http;

import TcpClient.Tcp_Client;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ThingsBoard_Http {
    private String host;
    private String port;
    private String Token_put;
    private String Token_ctrl;
    private String Token_Rpc;

    public ThingsBoard_Http(String host,String port,String Token_put,String Token_ctrl,String Token_Rpc){
        this.host = host;
        this.port = port;
        this.Token_put = Token_put;
        this.Token_ctrl = Token_ctrl;
        this.Token_Rpc = Token_Rpc;
    }






    private String Put_Sensor = "telemetry";
    private String Ctrl_attr =  "attributes";
    private String Rpc_listen = "rpc";


    public void put_Sensor(String json){
        HttpURLConnection connection;
        OutputStream outputStream;
        URL url;
        try {
            url = new URL(String.format("http://%s:%s/api/v1/%s/%s",host,port,Token_put,Put_Sensor));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type","Application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod("POST");

            outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes());
            outputStream.flush();
            connection.getResponseCode();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void Ctrl(String attr,boolean ii){
        HttpURLConnection connection;
        OutputStream outputStream;
        URL url;
        try {
            url = new URL(String.format("http://%s:%s/api/v1/%s/%s",host,port,Token_ctrl,Ctrl_attr));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type","Application/json");
            String i = ii?"true":"false";
            connection.setDoOutput(true);
            connection.setDoInput(true);
            String str = String.format("{\"%s\":\"%s\"}",attr,i);

            connection.setRequestMethod("POST");

            outputStream = connection.getOutputStream();
            outputStream.write(str.getBytes());
            outputStream.flush();
            connection.getResponseCode();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void Put_Rpc(String data,int id){
        HttpURLConnection connection1;
        URL url1;
        OutputStream outputStream;
        try {

            url1 = new URL(String.format("http://%s:%s/api/v1/%s/%s/%d",host,port,Token_Rpc,Rpc_listen,id));
            connection1 = (HttpURLConnection) url1.openConnection();
            connection1.setRequestMethod("POST");
            connection1.setRequestProperty("Content-Type","Application/json");

            connection1.setDoOutput(true);
            outputStream = connection1.getOutputStream();

            outputStream.write(data.getBytes());
            outputStream.flush();
            connection1.getResponseCode();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] Rpc_Listener() throws IOException {
        HttpURLConnection connection;
        URL url;
        StringBuilder ret = null;
        BufferedReader buffer;
        InputStream inputStream;

            url = new URL(String.format("http://%s:%s/api/v1/%s/%s",host,port,Token_Rpc,Rpc_listen));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type","Application/json");
            int code = connection.getResponseCode();
            if ( code == 200){
                String temp;
                ret = new StringBuilder();
                inputStream = connection.getInputStream();
                buffer = new BufferedReader(new InputStreamReader(inputStream));
                while ((temp = buffer.readLine())!=null){
                    ret.append(temp);
                }
            }else {
                System.out.println(code);
            }

        if (ret!=null){
            Gson gson = new Gson();



            Data data = gson.fromJson(ret.toString(),Data.class);



            int pin = data.params.pin;
            boolean enabled = data.params.enabled;
            int id = data.id;

            return new String[]{String.valueOf(pin), String.valueOf(enabled),String.valueOf(id)};

        }
        else {
            return new String[2];
        }

    }



//    public static void main(String[] args) {
//        ThingsBoard_Http thingsBoard_http = new ThingsBoard_Http("192.168.0.25","9090","3UGdnMHdm3ORt2PHmroW","ZFKIFuw9wqlPqLIz2Wmi","ZFKIFuw9wqlPqLIz2Wmi");
//
//        Tcp_Client tcp_client = new Tcp_Client("192.168.0.200",6005);
//        tcp_client.Connect();
//
//
//
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    thingsBoard_http.random = new Random();
//                    try{
//                        float a = thingsBoard_http.random.nextInt(2000);
//                        thingsBoard_http.put_Sensor("Lightness",a);
//                        System.out.println("Lightness:"+a);
//                        Thread.sleep(2000);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        int i = thingsBoard_http.Rpc_Listener();
//                        System.out.println(i);
//                        if (tcp_client.isRunning()){
//                            if (i == 1){
//                                tcp_client.Send(new byte[]{0x01,0x05,0x00,0x10, (byte) 0xFF,0x00, (byte) 0x8D, (byte) 0xFF});
//                                thingsBoard_http.Ctrl("switch",true);
//                            }
//                            else if (i == 0){
//                                tcp_client.Send(new byte[]{0x01,0x05,0x00,0x10, (byte) 0x00,0x00, (byte) 0xCC, (byte) 0x0F});
//                                thingsBoard_http.Ctrl("switch",false);
//                            }
//                        }
//                        else {
//                            System.out.println("未连接");
//                        }
//
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }

}
