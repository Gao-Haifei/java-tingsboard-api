package Modbus;

import DataTools.DataTools;
import TcpClient.Tcp_Client;

public class MoubusCtrl {



    private int address;

    public MoubusCtrl(int address){
        this.address = address;
    }


    public byte[] Ctrl(int DO,boolean is_open){
        if (is_open){
            return RequestCommand_Open(DO);
        }
        else {
            return RequestCommand_Close(DO);
        }
    }


    public byte[] RequestCommand_Open(int DO) {
        byte channle = 0;
        switch (DO){
            case 0:
                channle = 0x10;
                break;
            case 1:
                channle = 0x11;
                break;
            case 2:
                channle = 0x12;
                break;
            case 3:
                channle = 0x13;
                break;
            case 4:
                channle = 0x14;
                break;
            case 5:
                channle = 0x15;
                break;
            case 6:
                channle = 0x16;
                break;
            case 7:
                channle = 0x17;
                break;
            case 8:
                channle = 0x18;
                break;

        }


        byte[] Command = new byte[8];


        byte[] dataBytes = new byte[]{(byte) address, 0x05, 0x00, channle, (byte) 0xFF, 0x00};
        byte[] crc16 = DataTools.calculateCrc16(dataBytes);
        System.arraycopy(dataBytes, 0, Command, 0, dataBytes.length);
        Command[6] = crc16[1];
        Command[7] = crc16[0];
//        System.out.println("!!!!!!!!!!!!!!"+DataTools.formatByteArray(Command));
        return Command;
    }

    public byte[] RequestCommand_Close(int DO) {
        byte channle = 0;
        switch (DO){
            case 0:
                channle = 0x10;
                break;
            case 1:
                channle = 0x11;
                break;
            case 2:
                channle = 0x12;
                break;
            case 3:
                channle = 0x13;
                break;
            case 4:
                channle = 0x14;
                break;
            case 5:
                channle = 0x15;
                break;
            case 6:
                channle = 0x16;
                break;
            case 7:
                channle = 0x17;
                break;
            case 8:
                channle = 0x18;
                break;

        }
        byte[] Command = new byte[8];
        byte[] dataBytes = new byte[]{(byte) address, 0x05, 0x00, channle, (byte) 0x00, 0x00};
        byte[] crc16 = DataTools.calculateCrc16(dataBytes);
        System.arraycopy(dataBytes, 0, Command, 0, dataBytes.length);
        Command[6] = crc16[1];
        Command[7] = crc16[0];
//        System.out.println("!!!!!!!!!!!!!!"+DataTools.formatByteArray(Command));
        return Command;
    }

//    public static void main(String[] args) {
//        MoubusCtrl moubusCtrl = new MoubusCtrl(1,0,false);
//
//        Tcp_Client tcp_client = new Tcp_Client("192.168.0.200",6005);
//        tcp_client.Connect();
//
//
//
//
//        tcp_client.setTcpClientConnectListener(new Tcp_Client.TcpClientConnectListener() {
//            @Override
//            public void onConnect() {
//                System.out.println("连接成功");
//                tcp_client.Send(moubusCtrl.Ctrl());
//            }
//        });
//
//        tcp_client.setDataReceiveListener(new Tcp_Client.TCPClientDataReceiveListener() {
//            @Override
//            public void onDataReceive(byte[] var1) {
//                System.out.println(DataTools.formatByteArray(var1));
//            }
//        });
//    }

}
