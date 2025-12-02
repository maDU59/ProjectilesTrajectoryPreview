package fr.madu59.ptp.config.configScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<PtpConfigScreen> getModConfigScreenFactory() {
        return PtpConfigScreen::new;
    }
}
