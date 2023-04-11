package ZigBee;

import DataTools.DataTools;
import TcpClient.Tcp_Client;

import java.util.Timer;

public class ZigBee_Sensor {

    private byte[] GetSensor(){
        byte[] bytes = new byte[]{(byte) 0xE2,0x03,0x00,0x00, 0x00,0x00,0x00,0x00,0x00,0x00};
        bytes[9] = (byte) ((bytes[1]+bytes[2]+bytes[3]+bytes[4]+bytes[5]+bytes[6]+bytes[7]+bytes[8])&0xff);
        return bytes;
    }




    private byte[] Ctrl(int num,boolean ii){

        byte is1 = 0,is2 = 0;
        if (num==1){
            is1 = (byte) (ii?0x02:0x01);
        }
        else if (num==2){
            is2 = (byte) (ii?0x02:0x01);
        }
        else if (num==3){
            is1 = (byte) (ii?0x02:0x01);
            is2 = (byte) (ii?0x02:0x01);
        }
        byte[] bytes = new byte[]{(byte) 0xE2,0x02,is1,is2,0x00,0x00,0x00,0x00,0x00,0x00};
        bytes[9] = (byte) ((bytes[1]+bytes[2]+bytes[3]+bytes[4]+bytes[5]+bytes[6]+bytes[7]+bytes[8])&0xff);

        return bytes;
    }

    public static void main(String[] args) {
        ZigBee_Sensor zigBee_sensor = new ZigBee_Sensor();

        Tcp_Client tcp_client = new Tcp_Client("192.168.0.200",6006);
        tcp_client.Connect();

        Timer timer = new Timer();



        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        Thread.sleep(2000);
                        tcp_client.Send(zigBee_sensor.Ctrl(3,true));

                        Thread.sleep(2000);
                        tcp_client.Send(zigBee_sensor.Ctrl(3,false));

                        Thread.sleep(2000);
                        tcp_client.Send(zigBee_sensor.GetSensor());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();



        tcp_client.setTcpClientConnectListener(new Tcp_Client.TcpClientConnectListener() {
            @Override
            public void onConnect() {
                System.out.println("连接成功");
            }
        });



        tcp_client.setDataReceiveListener(new Tcp_Client.TCPClientDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] var1) {
                System.out.println(DataTools.formatByteArray(var1));

                if (var1[0] == (byte) 0xE1){
                    byte[] temphumlight = new byte[15];
                    for (int i = 2; i < 17; i++) {
                        temphumlight[i-2] = var1[i];
                    }
                    String str = new String(temphumlight);
                    String temp = str.substring(0,5);
                    String hum = str.substring(5,10);
                    String light = str.substring(10,15);
                    System.out.println(temp);
                    System.out.println(hum);
                    System.out.println(light);

                    byte[] ppm = new byte[5];
                    for (int i = 19; i < 24; i++) {
                        ppm[i-19] = var1[i];
                    }
                    String mppm = new String(ppm);
                    System.out.println(mppm);
                    byte[] air = new byte[5];
                    for (int i = 26; i < 31; i++) {
                        air[i-26] = var1[i];
                    }
                    String mair = new String(air);
                    System.out.println(mair);

                    String body = "";
                    byte reb= (byte) 0xA2;
                    if (var1[55] == reb){
                        body = "有人";
                    }
                    else {
                        body = "无人";
                    }
                    System.out.println(body);
                }



            }
        });

    }

}
