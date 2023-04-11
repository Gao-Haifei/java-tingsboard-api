package Modbus;


import DataTools.DataTools;
import TcpClient.Tcp_Client;

import java.util.concurrent.ArrayBlockingQueue;

public class Md4017 {




    int address;
    private int[] Vin = new int[8];







    public Md4017(int address){
        this.address = address;
    }





//    public void Connect(){
//        tcp_client = new Tcp_Client(ip,port);
//        tcp_client.Connect();
//        tcp_client.setTcpClientConnectListener(new Tcp_Client.TcpClientConnectListener() {
//            @Override
//            public void onConnect() {
//                System.out.println("连接成功");
////                tcp_client.Send(new byte[]{0x02,0x03,0x00,0x00,0x00,0x08,0x44,0x3f});
//
//                tcp_client.setDataReceiveListener(new Tcp_Client.TCPClientDataReceiveListener() {
//                    @Override
//                    public void onDataReceive(byte[] var1) {
//                        bytes = var1;
//                        System.out.println("串口监听接收的数据:"+DataTools.formatByteArray(bytes));
//                        receive(bytes);
//                    }
//                });
//            }
//        });
//    }

    public byte[] RequestCommand() {
        byte[] Command = new byte[8];
        byte[] dataBytes = new byte[]{(byte) address, 0x03, 0x00, 0x00, 0x00, 0x08};
        byte[] crc16 = DataTools.calculateCrc16(dataBytes);
        System.arraycopy(dataBytes, 0, Command, 0, dataBytes.length);
        Command[6] = crc16[1];
        Command[7] = crc16[0];
        return Command;
    }


    public int[] getVin() {
        return Vin;
    }



    public void receive(byte[] data){
        if (data.length>19){
            Vin[0] = DataTools.parseUnSignData(data[3],data[4]);
            Vin[1] = DataTools.parseUnSignData(data[5],data[6]);
            Vin[2] = DataTools.parseUnSignData(data[7],data[8]);
            Vin[3] = DataTools.parseUnSignData(data[9],data[10]);
            Vin[4] = DataTools.parseUnSignData(data[11],data[12]);
            Vin[5] = DataTools.parseUnSignData(data[13],data[14]);
            Vin[6] = DataTools.parseUnSignData(data[15],data[16]);
            Vin[7] = DataTools.parseUnSignData(data[17],data[18]);
        }


//        vin1 = DataTools.parseUnSignData(data[5],data[6]);
//        vin2 = DataTools.parseUnSignData(data[7],data[8]);
//        vin3 = DataTools.parseUnSignData(data[9],data[10]);
//        vin4 = DataTools.parseUnSignData(data[11],data[12]);
//        vin5 = DataTools.parseUnSignData(data[13],data[14]);
//        vin6 = DataTools.parseUnSignData(data[15],data[16]);
//        vin7 = DataTools.parseUnSignData(data[17],data[18]);
    }







//    public static void main(String[] args) {
//        Md4017 md4017 = new Md4017(2);
//
//        Tcp_Client tcp_client1 = new Tcp_Client("192.168.0.200",6001);
//
//        tcp_client1.Connect();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    try
//                    {
//                        tcp_client1.Send(md4017.RequestCommand());
//                        Thread.sleep(1000);
//                        int[] datas = md4017.getVin();
//
//                        Md4017Val md4017Val = new Md4017Val();
//                        float light = md4017Val.getRealValByType(Md4017VIN.LIGHT,datas[5]);
//                        System.out.println(String.format("当前光照:%.2fLx",light));
//                        Thread.sleep(2000);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//
//
//        tcp_client1.setDataReceiveListener(new Tcp_Client.TCPClientDataReceiveListener() {
//            @Override
//            public void onDataReceive(byte[] var1) {
//                md4017.receive(var1);
//            }
//        });
//    }

}
