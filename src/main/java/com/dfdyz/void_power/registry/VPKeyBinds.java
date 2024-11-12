package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.client.renderer.hud.VRGlassesHUDRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VPKeyBinds {

    public static final List<KeyBind> kbs = new ArrayList<>();
    public static final KeyBind TOGGLE_VR_GLASSES = new KeyBind("vr_glasses", GLFW.GLFW_KEY_I, (event) -> {
        boolean pressed = !(event.getAction() == 0);
        if(pressed){
            VRGlassesHUDRenderer.On = !VRGlassesHUDRenderer.On;
        }
    });


    public static class KeyBind{
        static final String TAB_TITLE = "Void Power";
        public final KeyMapping km;
        public final Consumer<InputEvent.Key> callback;
        public KeyBind(String name, int default_key, Consumer<InputEvent.Key> callback){
            this.callback = callback;
            km = new KeyMapping("key_bind." + name, default_key, TAB_TITLE);
            kbs.add(this);
        }
    }


    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        kbs.forEach((e)->{
            event.register(e.km);
        });
    }
}
