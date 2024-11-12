package com.dfdyz.void_power.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class FontLoader extends SimpleJsonResourceReloadListener {

    public static FontLoader Instance = new FontLoader();
    static Gson gson = new Gson();

    FontLoader() {
        super(gson, "vp_font_loader");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {

    }
}
