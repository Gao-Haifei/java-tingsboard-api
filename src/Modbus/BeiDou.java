package Modbus;

import DataTools.DataTools;
import Http.ThingsBoard_Http;
import TcpClient.Tcp_Client;
import com.sun.xml.internal.fastinfoset.util.CharArray;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BeiDou {
    private int address;

    public BeiDou(int address) {
        this.address = address;
    }

    public byte[] RequestCommand() {
        byte[] Command = new byte[8];

        byte[] dataBytes = new byte[]{(byte) address, 0x03, 0x00, 0x05, 0x00, 0x23};
        byte[] crc16 = DataTools.calculateCrc16(dataBytes);
        System.arraycopy(dataBytes, 0, Command, 0, dataBytes.length);
        Command[6] = crc16[1];
        Command[7] = crc16[0];
        return Command;
    }


    public String[] DingWei(byte[] datas){

        String str = new String(datas,StandardCharsets.ISO_8859_1);
        System.out.println(str);


        String[] all = str.split(",");


        String data_one = all[3];

        String data_two = all[5];

        return new String[]{data_one,data_two};

    }

    public String[] Data(String[] data){

        String one = data[1];
        String two = data[0];

        int od = Integer.parseInt(one.substring(0,3));
        int len = one.length();
        double oo = Double.parseDouble(one.substring(3,len));
        double dd = oo/60;

        int td = Integer.parseInt(two.substring(0,2));
        int ll = two.length();
        double tt = Double.parseDouble(two.substring(2,ll));
        double ss = tt/60;


        String one_data = String.valueOf(od+dd);
        String two_data = String.valueOf(td+ss);

        System.out.println(one_data);
        System.out.println(two_data);
        return new String[]{one_data,two_data};


    }


}
