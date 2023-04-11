package Modbus;

public class Md4017Val {

    public float getRealValByType(Md4017VIN md4017VIN, int val) {
        return (float) (md4017VIN.getMax() - md4017VIN.getMin()) / 65535.0F * (float)val + (float)md4017VIN.getMin();
    }


}
