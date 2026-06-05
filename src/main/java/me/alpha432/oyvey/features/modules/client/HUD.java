package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.OyVey;

public class HUD extends Module {
    
    public Setting<Boolean> arrayList = register(new Setting<>("ArrayList", true));
    public Setting<Integer> x = register(new Setting<>("X", 10, 0, 1000));
    public Setting<Integer> y = register(new Setting<>("Y", 10, 0, 1000));
    public Setting<Boolean> watermark = register(new Setting<>("Watermark", true));
    
    public HUD() {
        super("HUD", "Shows info on screen", Module.Category.CLIENT);
        setEnabled(true);
    }
    
    @Override
    public void onRender2D() {
        if (arrayList.getValue()) {
            int i = 0;
            for (Module module : OyVey.moduleManager.getModules()) {
                if (module.isEnabled() && module != this) {
                    String text = module.getName();
                    mc.textRenderer.draw(text, x.getValue(), y.getValue() + i * 10, 0xFFFFFF);
                    i++;
                }
            }
        }
        
        if (watermark.getValue()) {
            String watermark = "oyvey-ported | " + mc.player.getName().getString();
            mc.textRenderer.draw(watermark, 5, 5, 0xFFFF5555);
        }
    }
}
