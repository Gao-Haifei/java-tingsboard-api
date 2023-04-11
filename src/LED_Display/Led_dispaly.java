package LED_Display;

import java.io.UnsupportedEncodingException;

public class Led_dispaly {

    private int address;

    public Led_dispaly(int address){
        this.address = address;
//        AA 01 BB 51 54 72 01 00 00 02 63 41 42 BB B6 D3 AD B9 E2 C1 D9 31 32 FF
    }


    public byte[] RequestCommond(String str){
        byte[] res;
        try {
             res = str.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        byte[] Commond = new byte[128];
        Commond[0] = (byte) 0xAA;
        Commond[1] = (byte) address;
        Commond[2] = (byte) 0xBB;
        Commond[3] = 0x51;
        Commond[4] = 0x54;

        Commond[6] = 0x01;
        Commond[7] = 0x00;
        Commond[8] = 0x00;
        Commond[9] = 0x02;
        Commond[10] = 0x63;



        for (int i = 0; i < res.length; i++) {
            Commond[11+i] = res[i];
        }

        int size = res.length+11;

        for (int i = 0; i < 10; i++) {
            Commond[size+i] = 0x00;
            size++;
        }

        byte x = 0;
        for (int i = 6; i < size; i++) {
            x +=Commond[i];
        }
        Commond[5] = x;
        Commond[size] = (byte) 0xFF;

        return Commond;

    }


}
