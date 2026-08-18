package fr.madu59.ptp.compat;

import fr.madu59.ptp.platform.PlatformHelper;

public class ModCompat {
    private static boolean IS_VIVECRAFT_LOADED = PlatformHelper.isModLoaded("vivecraft");

    public static boolean isVivecraftLoaded(){
        return IS_VIVECRAFT_LOADED;
    }
}
