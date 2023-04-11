package Modbus;

public class RGB_Light {

    private int address;

    public RGB_Light(int address){
        this.address = address;
    }

    public byte[] RGB_Color(int r,int g,int b){
        byte[] bytes = new byte[9];
        bytes[0] = (byte) 0xA5;
        bytes[1] = 0x06;
        bytes[2] = (byte) address;
        bytes[3] = (byte) 0xA0;
        bytes[4] = (byte) r;
        bytes[5] = (byte) g;
        bytes[6] = (byte) b;
        bytes[7] = (byte) ((bytes[1]+bytes[2]+bytes[3]+bytes[4]+bytes[5]+bytes[6]) & 0xff);
        bytes[8] = 0x5A;
        return bytes;
    }

}
