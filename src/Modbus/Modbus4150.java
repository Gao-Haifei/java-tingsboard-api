package Modbus;

import DataTools.DataTools;
import TcpClient.Tcp_Client;

import java.util.Arrays;

public class Modbus4150 {

    private int address;

    public Modbus4150(int address) {
        this.address = address;
    }


    private byte[] DI_Datas = new byte[7];

    private byte[] DO_Datas = new byte[8];









//    public static byte[] getCRC(byte[] bytes) {
//        int CRC = 0xffff;
//        int POLYNOMIAL = 0x0000a001;
//
//        int i, j;
//        for (i = 0; i < bytes.length; i++) {
//            CRC ^= bytes[i];
//            for (j = 0; j < 8; j++) {
//                if ((CRC & 0x00000001) != 0) {
//                    CRC >>= 1;
//                    CRC ^= POLYNOMIAL;
//                } else {
//                    CRC >>= 1;
//                }
//            }
//        }
//        CRC = (CRC & '\uff00') >> 8 | (CRC & 255) << 8;
//        byte high = (byte) (CRC & 0xff);
//        byte low = (byte) ((CRC & 0xff00) >> 8);
//        return new byte[]{high, low};
//    }



    public byte[] RequestCommand() {
        byte[] Command = new byte[8];
        byte[] dataBytes = new byte[]{(byte) address, 0x01, 0x000, 0x00, 0x00, 0x24};
        byte[] crc16 = DataTools.calculateCrc16(dataBytes);
        System.arraycopy(dataBytes, 0, Command, 0, dataBytes.length);
        Command[6] = crc16[1];
        Command[7] = crc16[0];
        return Command;
    }


    public byte[][] receiveMsg(byte[] data) {

        byte DIdata = data[3];
        byte DOdata = data[5];

        byte[][] datas = new byte[2][];
        datas[0] = new byte[7];
        datas[1] = new byte[8];

        for (int i = 0; i < 8; i++) {
            int t = (new Double(Math.pow(2,i))).intValue();
            datas[1][i] = (byte) ((DOdata & t)>>i);

        }

        for (int i = 0; i < 7; i++) {
            int t = (new Double(Math.pow(2,i))).intValue();
            datas[0][i]= (byte) ((DIdata & t)>>i);
        }
        return datas;






//        DI0Val = (byte) (DIdata & 1);
//        DI1Val = (byte) ((DIdata & 2) >> 1);
//        DI2Val = (byte) ((DIdata & 4) >> 2);
//        DI3Val = (byte) ((DIdata & 8) >> 3);
//        DI4Val = (byte) ((DIdata & 16) >> 4);
//        DI5Val = (byte) ((DIdata & 32) >> 5);
//        DI6Val = (byte) ((DIdata & 64) >> 6);
//
//        DO0VAL = (byte) (DOdata & 1);
//        DO1VAL = (byte) ((DOdata & 2) >> 1);
//        DO2VAL = (byte) ((DOdata & 4) >> 2);
//        DO3VAL = (byte) ((DOdata & 8) >> 3);
//        DO4VAL = (byte) ((DOdata & 16) >> 4);
//        DO5VAL = (byte) ((DOdata & 32) >> 5);
//        DO6VAL = (byte) ((DOdata & 64) >> 6);
//        DO7VAL = (byte) ((DOdata & 128) >> 7);


    }
}
