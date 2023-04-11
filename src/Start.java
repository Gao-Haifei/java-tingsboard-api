import DataTools.DataTools;
import Http.Data;
import Http.ThingsBoard_Http;
import LED_Display.Led_dispaly;
import Modbus.*;
import TcpClient.Tcp_Client;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Start {


    static float light = 0.F, temp, hum, wind, co2, noise;
    static int DO = 0;
    static int modbus4150_address = 1;
    static int mdbus4017_address = 2;
    static int Rgb_address = 3;
    static int BeiDou_address = 4;
    static int Led_address = 5;
    static String[] Dingwei = new String[2];
    static Tcp_Client modbus_socket;
    static Tcp_Client led_socket;
    static Tcp_Client beidou_socket;
    static ThingsBoard_Http thingsBoard_modbus4017, thingsBoard_modbus4150, thingsBoard_beidou;
    static Md4017 md4017;
    static Modbus4150 modbus4150;
    static MoubusCtrl moubusCtrl;
    static Md4017Val md4017Val;
    static RGB_Light rgb_light;
    static Led_dispaly led_dispaly;
    static BeiDou beiDou;
    static ArrayBlockingQueue<byte[]> arrayBlockingQueue, recevieArray;
    static Led_Thread led_thread;
    static ModBus_Thread modBus_thread;
    static Beidou_Thread beidou_thread;
    static mThread mthread;

    static boolean is_ledConnect = false;

    static int di0, di1, di2, di3, di4, di5, di6, do0, do1, do2, do3, do4, do5, do6, do7;

    public static void main(String[] args) {

        //定义两个消息队列，一个负责发送，一个负责接收
        arrayBlockingQueue = new ArrayBlockingQueue<>(5);
        recevieArray = new ArrayBlockingQueue<>(5);

        //初始化Thread类
        led_thread = new Led_Thread();
        modBus_thread = new ModBus_Thread();
        beidou_thread = new Beidou_Thread();
        mthread = new mThread();

        //初始化Socket，将IP地址与端口号传入
        modbus_socket = new Tcp_Client("192.168.0.200", 6001);
        led_socket = new Tcp_Client("192.168.0.200", 6005);
        beidou_socket = new Tcp_Client("192.168.0.200", 6002);

        //调用Connect函数来进行连接
        led_socket.Connect();
        modbus_socket.Connect();
        beidou_socket.Connect();

        System.out.println("123");

        //启动消息处理线程
        mthread.start();

        //初始化ThingsBoard的连接方式，将IP地址，端口，上传数据访问令牌，更改属性访问令牌，需要监听的访问令牌
        thingsBoard_modbus4017 = new ThingsBoard_Http("192.168.0.25", "9090", "WLBsTxnHb4XgamfWPC7y", "tyBR6iBNW4uHuQZ7XbzC", "tyBR6iBNW4uHuQZ7XbzC");
        thingsBoard_beidou = new ThingsBoard_Http("192.168.0.25", "9090", "fRgMKW5rfrcWrp4K6m7d", "", "");
        thingsBoard_modbus4150 = new ThingsBoard_Http("192.168.0.25", "9090", "tyBR6iBNW4uHuQZ7XbzC", "", "tyBR6iBNW4uHuQZ7XbzC");

        //调用socket监听器来监听是否连接
        led_socket.setTcpClientConnectListener(new Tcp_Client.TcpClientConnectListener() {
            @Override
            public void onConnect() {
                System.out.println("Led屏幕-socket连接成功");
                is_ledConnect = true;
            }
        });
        modbus_socket.setTcpClientConnectListener(new Tcp_Client.TcpClientConnectListener() {
            @Override
            public void onConnect() {
                System.out.println("modbus—socket连接成功");

                //开启modbus线程来获取4017与4150的传感信息，4150的开关控制
                modBus_thread.start();
                //连接成功后，开启led的线程，将modbus4017获取到的数据发送到led屏幕上
                if (is_ledConnect) {
                    led_thread.start();
                }

            }
        });
        beidou_socket.setTcpClientConnectListener(new Tcp_Client.TcpClientConnectListener() {
            @Override
            public void onConnect() {
                System.out.println("北斗定位-socket连接成功");
                beidou_thread.start();
            }
        });

        //初始化库文件，将485地址与232地址传入
        beiDou = new BeiDou(BeiDou_address);
        rgb_light = new RGB_Light(Rgb_address);
        md4017Val = new Md4017Val();
        md4017 = new Md4017(mdbus4017_address);
        modbus4150 = new Modbus4150(modbus4150_address);
        moubusCtrl = new MoubusCtrl(modbus4150_address);
        led_dispaly = new Led_dispaly(Led_address);


        Timer timer = new Timer();


        beidou_socket.setDataReceiveListener(new Tcp_Client.TCPClientDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] var1) {
                if (var1[0] == BeiDou_address) {
                    Dingwei = beiDou.Data(beiDou.DingWei(var1));
                }
            }
        });
        modbus_socket.setDataReceiveListener(new Tcp_Client.TCPClientDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] var1) {
                try {
                    //将监听到的回调数据放入接收队列
                    recevieArray.put(var1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //监听ThingsBoard ，是否有数据下发
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    arrayBlockingQueue.put(modbus4150.RequestCommand());
                    String[] str = thingsBoard_modbus4150.Rpc_Listener();
                    if (str != null && !str[0].equals("")) {
                        int pin = Integer.parseInt(str[0]);
                        boolean enabled = Boolean.parseBoolean(str[1]);
                        arrayBlockingQueue.put(moubusCtrl.Ctrl(pin, enabled));

                        int id = Integer.parseInt(str[2]);
                        int[] dos = new int[]{do0, do1, do2, do3, do4, do5, do6, do7};
                        dos[pin] = enabled ? 1 : 0;
                        thingsBoard_modbus4150.Put_Rpc(String.format("[%d,%d,%d,%d,%d,%d,%d,%d]", dos[0], dos[1], dos[2], dos[3], dos[4], dos[5], dos[6], dos[7]), id);

                        thingsBoard_modbus4150.put_Sensor(String.format("{\"DO0\":%d,\"DO1\":%d,\"DO2\":%d,\"DO3\":%d,\"DO4\":%d,\"DO5\":%d,\"DO6\":%d,\"DO7\":%d}", dos[0], dos[1], dos[2], dos[3], dos[4], dos[5], dos[6], dos[7]));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 1000);


    }

    private static class Led_Thread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                sleep(200);
                while (true) {
                    led_socket.Send(led_dispaly.RequestCommond("当前光照:" + (int) light + "," + "当前温度:" + (int) temp + "," + "当前湿度:" + (int) hum + "," + "当前二氧化碳:" + (int) co2 + "," + "当前噪音:" + (int) noise + "," + "当前风速:" + (int) wind));
                    sleep(8000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void Get_DI(){
        try {
            Thread.sleep(200);
            //将4150的数据上传至ThingsBoard
            thingsBoard_modbus4150.put_Sensor(String.format("{\"DI0\":%d,\"DI1\":%d,\"DI2\":%d,\"DI3\":%d,\"DI4\":%d,\"DI5\":%d,\"DI6\":%d}", di0, di1, di2, di3, di4, di5, di6));


            System.out.println("各个DO通道口的值：" + do0 + "," + do1 + "," + do2 + "," + do3 + "," + do4 + "," + do5 + "," + do6 + "," + do7);
            System.out.println("各个DI通道口的值：" + di0 + "," + di1 + "," + di2 + "," + di3 + "," + di4 + "," + di5 + "," + di6);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ModBus_Thread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                sleep(200);
                while (true) {

                    //将需要发送的命令放入发送队列

                    arrayBlockingQueue.put(md4017.RequestCommand());
                    arrayBlockingQueue.put(modbus4150.RequestCommand());
                    arrayBlockingQueue.put(rgb_light.RGB_Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));

                    //进行巡航取出并通过socket发送至服务器，需要延时200毫秒，不然串口服务器无法接收
                    while (!arrayBlockingQueue.isEmpty()) {
                        modbus_socket.Send(arrayBlockingQueue.poll());
                        sleep(200);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Beidou_Thread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    beidou_socket.Send(beiDou.RequestCommand());
                    sleep(2000);
                    thingsBoard_beidou.put_Sensor(String.format("{\"latitude\":\"%s\",\"longitude\":\"%s\"}", Dingwei[1], Dingwei[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //消息处理线程
    private static class mThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    sleep(500);
                    byte[] recevie_data = recevieArray.poll();
                    if (recevie_data != null) {
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!消息队列接收的消息:" + DataTools.formatByteArray(recevie_data));

                        String relay_open = DataTools.formatByteArray(moubusCtrl.RequestCommand_Open(DO));
                        String relay_close = DataTools.formatByteArray(moubusCtrl.RequestCommand_Close(DO));
                        String res = DataTools.formatByteArray(recevie_data);

                        if (res.equals(relay_close)) {
                            System.out.println("！！！！！！！！！！！！！！！！！！！继电器关闭成功");
                        } else if (res.equals(relay_open)) {
                            System.out.println("！！！！！！！！！！！！！！！！！！！继电器开启成功");
                        } else if (recevie_data[0] == modbus4150_address) {
                            System.out.println("4150的DI口与DO口数据获取成功");
                            //DI与DO口的值
                            byte[][] datas = modbus4150.receiveMsg(recevie_data);

                            di0 = datas[0][0];
                            di1 = datas[0][1];
                            di2 = datas[0][2];
                            di3 = datas[0][3];
                            di4 = datas[0][4];
                            di5 = datas[0][5];
                            di6 = datas[0][6];

                            do0 = datas[1][0];
                            do1 = datas[1][1];
                            do2 = datas[1][2];
                            do3 = datas[1][3];
                            do4 = datas[1][4];
                            do5 = datas[1][5];
                            do6 = datas[1][6];
                            do7 = datas[1][7];

                            Get_DI();
                        } else if (recevie_data[0] == mdbus4017_address) {
                            md4017.receive(recevie_data);
                            System.out.println("获取4017数据成功");
                        } else if (recevie_data[0] == (byte) 0xA5) {
                            System.out.println("控制RGB灯光成功");
                        }



                        //获取4017各个通道口的电流值
                        int[] Val = md4017.getVin();

                        //将获取的电流值调用公式进行换算，并赋值给变量
                        light = md4017Val.getRealValByType(Md4017VIN.LIGHT, Val[4]);
                        temp = md4017Val.getRealValByType(Md4017VIN.TEM, Val[0]);
                        hum = md4017Val.getRealValByType(Md4017VIN.HUM, Val[1]);
                        co2 = md4017Val.getRealValByType(Md4017VIN.CO2, Val[2]);
                        noise = md4017Val.getRealValByType(Md4017VIN.NOISE, Val[3]);
                        wind = md4017Val.getRealValByType(Md4017VIN.WIN, Val[5]);

                        System.out.println("当前光照:" + light + "," + "当前温度:" + temp + "," + "当前湿度:" + hum + "," + "当前二氧化碳:" + co2 + "," + "当前噪音:" + noise + "," + "当前风速:" + wind);

                        //将换算后的传感值，传入ThingsBoard
                        thingsBoard_modbus4017.put_Sensor(String.format("{\"DI0\":\"%.2f\",\"DI1\":\"%.2f\",\"DI2\":\"%.2f\",\"DI3\":\"%.2f\",\"DI4\":\"%.2f\",\"DI5\":\"%.2f\"}", temp, hum, co2, noise, light, wind));
//                        thingsBoard_modbus4150.put_Sensor(String.format("{\""));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
