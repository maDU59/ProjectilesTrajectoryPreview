package fr.madu59.ptp.platform;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformHelper {

    public static String getPlatformName(){
        return "Fabric";
    }
    
    public static boolean isModLoaded(String modId){
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Path getConfigDir(){
        return FabricLoader.getInstance().getConfigDir();
    }
}
