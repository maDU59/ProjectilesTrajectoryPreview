package com.example.config.configScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<ptpConfigScreen> getModConfigScreenFactory() {
        return ptpConfigScreen::new;
    }
}
