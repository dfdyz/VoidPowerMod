package com.dfdyz.void_power.events;

import com.dfdyz.void_power.registry.VPKeyBinds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dfdyz.void_power.registry.VPKeyBinds.kbs;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class InputEvents {

    @SubscribeEvent
    public static void KeyEvent(InputEvent.Key event){
        for (VPKeyBinds.KeyBind kb : kbs){
            if(event.getKey() == kb.km.getKey().getValue()){
                kb.callback.accept(event);
            }
        }
    }

}
