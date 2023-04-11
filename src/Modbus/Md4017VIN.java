package Modbus;

public enum Md4017VIN {
    TEM("温度", -10, 60, "°C"),
    HUM("湿度", 50, 100, "%RH"),
    LIGHT("光照", 0, 20000, "lx"),
    WIN("风速", 0, 70, "m/s"),
    PRE("大气压力", 0, 110, "KPa"),
    CO2("二氧化碳", 0, 5000, "PPM"),
    AIR("空气质量", 0, 100, "指数"),
    SOIL_TEM("土壤温度", -10, 60, "°C"),
    SOIL_WATER("土壤水分", 50, 100, "%RH"),
    WATER_TEM("水温", -50, 100, "°C"),
    WATER_LEV("水位", 0, 1, "m"),
    NOISE("噪音", 30, 120, "db");

    private String displayName;
    private int min;
    private int max;
    private String unit;

    private Md4017VIN(String displayName, int min, int max, String unit) {
        this.displayName = displayName;
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public String getUnit() {
        return this.unit;
    }
}