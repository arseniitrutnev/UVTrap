package uv.uranvex.uvtrap.utils;

import uv.uranvex.uvtrap.UVTrap;

public class ConfigUtil {
    public static String getString(String path) {
        return HexUtil.color(UVTrap.instance.getConfig().getString(path));
    }

    public static int getInt(String path) {
        return UVTrap.instance.getConfig().getInt(path);
    }

    public static  Boolean getBoolean(String path){
        return UVTrap.instance.getConfig().getBoolean(path);
    }
}
