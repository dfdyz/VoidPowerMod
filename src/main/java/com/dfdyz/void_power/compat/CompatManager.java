package com.dfdyz.void_power.compat;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.HashMap;
import java.util.Map;

public class CompatManager {


    static final Map<String, String> mixin_modid = Maps.newHashMap();
    static final Map<String, Boolean> needed = Maps.newHashMap();

    static {
        mixin_modid.put("MixinTweakedControllerTE", "create_tweaked_controllers");
    }


    public static void ModCheck(){
        mixin_modid.forEach((k,v) -> {
            if(!needed.containsKey(v)){
                needed.put(v, FMLLoader.getLoadingModList().getModFileById(v) != null);
            }
        });
    }


    public static boolean ShouldLoadMixin(String mixinClass, String targetClass){
        if(mixin_modid.containsKey(mixinClass)){
            return needed.getOrDefault(mixin_modid.getOrDefault(mixinClass, ""), false);
        }
        return true;
    }


}
