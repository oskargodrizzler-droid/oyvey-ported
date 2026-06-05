package me.alpha432.oyvey.gui;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.io.*;
import java.nio.file.*;

public class AimbotGui extends Screen {
    private static AimbotGui instance;
    private int panelX = 50, panelY = 50;
    private final int panelW = 340;
    private int panelH = 560;
    private boolean dragging = false;
    private int dragX, dragY;

    // Aimbot settings
    private boolean aimbotEnabled = true;
    private int aimbotConditions = 0;
    private int boneAimbot = 0;
    private int hitboxes = 0;
    private float fieldOfView = 90.0f;
    private float smoothing = 50.0f;
    private int reactionTime = 30;
    private int targetSwitchDelay = 250;
    private int firstBulletDelay = 0;
    private boolean recoilControl = true;

    // Triggerbot settings
    private boolean triggerbotEnabled = true;
    private int triggerConditions = 0;
    private float hitChance = 100.0f;
    private int triggerReactionTime = 20;
    private int burstTime = 100;
    private boolean quickScope = true;

    private float currentRange = 0.0f;
    private int hitChanceDisplay = 100;

    private int activeSlider = -1;
    private int activeDropdown = -1;
    private boolean showDropdown = false;
    private int mouseX, mouseY;

    public AimbotGui() {
        super(Text.literal("OyVey Aimbot Config"));
        instance = this;
        loadSettings();
    }

    public static void open() {
        MinecraftClient.getInstance().setScreen(new AimbotGui());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        context.fill(0, 0, width, height, 0xAA000000);

        context.fill(panelX+3, panelY+3, panelX+panelW+3, panelY+panelH+3, 0x66000000);
        context.fill(panelX, panelY, panelX+panelW, panelY+panelH, 0xDD111111);
        context.drawBorder(panelX, panelY, panelW, panelH, 0xFFFF5555);
        context.fill(panelX, panelY, panelX+panelW, panelY+26, 0xFF1A1A1A);
        context.drawText(textRenderer, "OYVEY | AIMBOT CONFIG", panelX+10, panelY+8, 0xFFFF5555, false);
        context.drawText(textRenderer, "X", panelX+panelW-18, panelY+7, 0xFFFF5555, false);

        int currentY = panelY + 40;
        context.drawText(textRenderer, "§l> AIMBOT", panelX+12, currentY, 0xFFFF5555, false);
        currentY += 16;
        currentY = drawToggle(context, "Enable aimbot", panelX+15, currentY, aimbotEnabled);
        currentY = drawDropdown(context, "Conditions", panelX+15, currentY, new String[]{"Always", "Holding weapon", "Target visible"}, aimbotConditions, 0);
        currentY = drawDropdown(context, "Bone aimbot", panelX+15, currentY, new String[]{"Head", "Chest", "Neck", "Pelvis"}, boneAimbot, 1);
        currentY = drawDropdown(context, "Hitboxes", panelX+15, currentY, new String[]{"All", "Head only", "Chest+Head"}, hitboxes, 2);
        currentY = drawSlider(context, "Field of view", panelX+15, currentY, 0, 360, fieldOfView, 0);
        currentY = drawSlider(context, "Smoothing", panelX+15, currentY, 0, 100, smoothing, 1);
        currentY = drawSlider(context, "Reaction time (ms)", panelX+15, currentY, 0, 500, reactionTime, 2);
        currentY = drawSlider(context, "Target switch delay (ms)", panelX+15, currentY, 0, 1000, targetSwitchDelay, 3);
        currentY = drawSlider(context, "First bullet delay (ms)", panelX+15, currentY, 0, 500, firstBulletDelay, 4);
        currentY = drawToggle(context, "Recoil control", panelX+15, currentY, recoilControl);

        currentY += 10;
        context.drawText(textRenderer, "§l> TRIGGERBOT", panelX+12, currentY, 0xFFFF5555, false);
        currentY += 16;
        currentY = drawToggle(context, "Enable triggerbot", panelX+15, currentY, triggerbotEnabled);
        currentY = drawDropdown(context, "Conditions", panelX+15, currentY, new String[]{"Always", "Holding weapon", "Looking at target"}, triggerConditions, 3);
        currentY = drawSlider(context, "Hit chance (%)", panelX+15, currentY, 0, 100, hitChance, 5);
        currentY = drawSlider(context, "Reaction time (ms)", panelX+15, currentY, 0, 300, triggerReactionTime, 6);
        currentY = drawSlider(context, "Burst time (ms)", panelX+15, currentY, 0, 500, burstTime, 7);
        currentY = drawToggle(context, "Quick scope", panelX+15, currentY, quickScope);

        currentY += 14;
        context.fill(panelX+10, currentY, panelX+panelW-10, currentY+1, 0xFF333333);
        currentY += 12;
        String meters = String.format("§a%d%%§f  §b%.0fm§f  §b%.0fm§f  §b%.0fm§f  §b%.0fm§f  §b%.0fm",
                hitChanceDisplay, currentRange, currentRange, currentRange, currentRange, currentRange);
        context.drawText(textRenderer, meters, panelX+15, currentY, 0xFFFFFFFF, false);
        currentY += 28;
        panelH = currentY - panelY + 10;

        if (mouseX >= panelX+panelW-25 && mouseX <= panelX+panelW-5 && mouseY >= panelY+5 && mouseY <= panelY+22) {
            context.fill(panelX+panelW-25, panelY+5, panelX+panelW-5, panelY+22, 0x44FF5555);
        }
    }

    private int drawToggle(DrawContext ctx, String label, int x, int y, boolean state) {
        int boxX = x + 160;
        int boxW = 28;
        int boxH = 14;
        ctx.fill(boxX, y, boxX+boxW, y+boxH, state ? 0xFFFF5555 : 0xFF444444);
        int knobX = state ? boxX+boxW-10 : boxX+2;
        ctx.fill(knobX, y+2, knobX+8, y+boxH-2, 0xFFFFFFFF);
        ctx.drawText(textRenderer, label, x, y+3, 0xFFCCCCCC, false);
        if (mouseX >= boxX && mouseX <= boxX+boxW && mouseY >= y && mouseY <= y+boxH && MinecraftClient.getInstance().mouse.wasLeftButtonClicked()) {
            state = !state;
            if (label.equals("Enable aimbot")) aimbotEnabled = state;
            if (label.equals("Recoil control")) recoilControl = state;
            if (label.equals("Enable triggerbot")) triggerbotEnabled = state;
            if (label.equals("Quick scope")) quickScope = state;
            saveSettings();
        }
        return y + 20;
    }

    private int drawSlider(DrawContext ctx, String label, int x, int y, float min, float max, float value, int id) {
        String display = label + ": " + (int)value;
        ctx.drawText(textRenderer, display, x, y+3, 0xFFCCCCCC, false);
        int sliderX = x + 160;
        int sliderW = 140;
        int sliderY = y + 5;
        int sliderH = 6;
        ctx.fill(sliderX, sliderY, sliderX+sliderW, sliderY+sliderH, 0xFF333333);
        float percent = (value - min) / (max - min);
        int fillW = (int)(percent * sliderW);
        ctx.fill(sliderX, sliderY, sliderX+fillW, sliderY+sliderH, 0xFFFF5555);
        int handleX = sliderX + fillW - 5;
        ctx.fill(handleX, sliderY-3, handleX+10, sliderY+sliderH+3, 0xFFFFFFFF);
        if (activeSlider == id && MinecraftClient.getInstance().mouse.wasLeftButtonClicked()) {
            float newPercent = (float)(mouseX - sliderX) / sliderW;
            float newVal = min + newPercent * (max - min);
            newVal = Math.max(min, Math.min(max, newVal));
            switch(id) {
                case 0: fieldOfView = newVal; break;
                case 1: smoothing = newVal; break;
                case 2: reactionTime = (int)newVal; break;
                case 3: targetSwitchDelay = (int)newVal; break;
                case 4: firstBulletDelay = (int)newVal; break;
                case 5: hitChance = newVal; hitChanceDisplay = (int)hitChance; break;
                case 6: triggerReactionTime = (int)newVal; break;
                case 7: burstTime = (int)newVal; break;
            }
            saveSettings();
        }
        if (mouseX >= sliderX && mouseX <= sliderX+sliderW && mouseY >= sliderY-3 && mouseY <= sliderY+sliderH+3) {
            activeSlider = id;
        } else if (activeSlider == id && !MinecraftClient.getInstance().mouse.wasLeftButtonClicked()) {
            activeSlider = -1;
        }
        return y + 22;
    }

    private int drawDropdown(DrawContext ctx, String label, int x, int y, String[] options, int selected, int id) {
        String display = label + ": " + options[selected];
        ctx.drawText(textRenderer, display, x, y+3, 0xFFCCCCCC, false);
        int boxX = x + 160;
        int boxW = 140;
        int boxH = 16;
        ctx.fill(boxX, y, boxX+boxW, y+boxH, 0xFF222222);
        ctx.drawBorder(boxX, y, boxW, boxH, 0xFFFF5555);
        ctx.drawText(textRenderer, "▼", boxX+boxW-14, y+3, 0xFFFFFFFF, false);
        if (mouseX >= boxX && mouseX <= boxX+boxW && mouseY >= y && mouseY <= y+boxH && MinecraftClient.getInstance().mouse.wasLeftButtonClicked()) {
            showDropdown = true;
            activeDropdown = id;
        }
        if (showDropdown && activeDropdown == id) {
            int dropdownY = y + boxH;
            int dropdownH = options.length * 16;
            ctx.fill(boxX, dropdownY, boxX+boxW, dropdownY+dropdownH, 0xDD000000);
            ctx.drawBorder(boxX, dropdownY, boxW, dropdownH, 0xFFFF5555);
            for (int i = 0; i < options.length; i++) {
                int itemY = dropdownY + i * 16;
                int itemColor = (i == selected) ? 0xFFFF5555 : 0xFFCCCCCC;
                ctx.drawText(textRenderer, options[i], boxX+6, itemY+4, itemColor, false);
                if (mouseX >= boxX && mouseX <= boxX+boxW && mouseY >= itemY && mouseY <= itemY+16 && MinecraftClient.getInstance().mouse.wasLeftButtonClicked()) {
                    switch(id) {
                        case 0: aimbotConditions = i; break;
                        case 1: boneAimbot = i; break;
                        case 2: hitboxes = i; break;
                        case 3: triggerConditions = i; break;
                    }
                    showDropdown = false;
                    activeDropdown = -1;
                    saveSettings();
                }
            }
            if (!(mouseX >= boxX && mouseX <= boxX+boxW && mouseY >= y && mouseY <= dropdownY+dropdownH)) {
                showDropdown = false;
                activeDropdown = -1;
            }
        }
        return y + 20;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= panelX+panelW-25 && mouseX <= panelX+panelW-5 && mouseY >= panelY+5 && mouseY <= panelY+22) {
            saveSettings();
            MinecraftClient.getInstance().setScreen(null);
            return true;
        }
        if (mouseX >= panelX && mouseX <= panelX+panelW && mouseY >= panelY && mouseY <= panelY+26) {
            dragging = true;
            dragX = (int)mouseX - panelX;
            dragY = (int)mouseY - panelY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            panelX = (int)mouseX - dragX;
            panelY = (int)mouseY - dragY;
            panelX = Math.max(0, Math.min(width - panelW, panelX));
            panelY = Math.max(0, Math.min(height - panelH, panelY));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    private void saveSettings() {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("aimbotEnabled", aimbotEnabled);
            obj.addProperty("fieldOfView", fieldOfView);
            obj.addProperty("smoothing", smoothing);
            obj.addProperty("triggerbotEnabled", triggerbotEnabled);
            obj.addProperty("hitChance", hitChance);
            obj.addProperty("panelX", panelX);
            obj.addProperty("panelY", panelY);
            Files.createDirectories(Paths.get("config/oyvey/"));
            Files.writeString(Paths.get("config/oyvey/aimbot_settings.json"), obj.toString());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadSettings() {
        try {
            String content = Files.readString(Paths.get("config/oyvey/aimbot_settings.json"));
            JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
            aimbotEnabled = obj.get("aimbotEnabled").getAsBoolean();
            fieldOfView = obj.get("fieldOfView").getAsFloat();
            smoothing = obj.get("smoothing").getAsFloat();
            triggerbotEnabled = obj.get("triggerbotEnabled").getAsBoolean();
            hitChance = obj.get("hitChance").getAsFloat();
            panelX = obj.get("panelX").getAsInt();
            panelY = obj.get("panelY").getAsInt();
            hitChanceDisplay = (int)hitChance;
        } catch (IOException e) { /* first run */ }
    }

    // Getters for modules
    public boolean isAimbotEnabled() { return aimbotEnabled; }
    public float getFieldOfView() { return fieldOfView; }
    public float getSmoothing() { return smoothing; }
    public boolean isTriggerbotEnabled() { return triggerbotEnabled; }
    public float getHitChance() { return hitChance; }
}
