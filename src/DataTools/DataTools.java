package DataTools;

public class DataTools {

    public static byte[] calculateCrc16(byte[] bytes) {
        int crc = 65535;

        for (byte aByte : bytes) {
            crc ^= aByte & 255;

            for (int j = 0; j < 8; ++j) {
                if ((crc & 1) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else {
                    crc >>= 1;
                }
            }
        }

        crc = (crc & '\uff00') >> 8 | (crc & 255) << 8;
        byte high = (byte) (crc & 255);
        byte low = (byte) ((crc & 0xff00) >> 8);
        return new byte[]{high, low};



    }





    public static String oneByte2HexStr(byte b) {
        String hexString = Integer.toHexString(b & 255);
        hexString = hexString.length() == 1 ? "0" + hexString : hexString;
        return hexString.toUpperCase();
    }

    public static String formatByteArray(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] var2 = bytes;
        int var3 = bytes.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte aByte = var2[var4];
            stringBuilder.append(oneByte2HexStr(aByte)).append(" ");
        }

        return stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "").toString().toUpperCase();
    }


    public static int parseUnSignData(byte high, byte low) {
        return ((high & 255) << 8) + (low & 255);
    }


}
