package com.dfdyz.void_power;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = VoidPowerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue STRESS_PER_RPM = BUILDER
            .comment("Engine cost stress per rpm.")
            .defineInRange("EngineStressPerRPM", 8, 0., Float.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue MASS_PER_STRESS = BUILDER
            .comment("The weight that can be driven of per stress*rpm cost.")
            .defineInRange("EngineMassPerStress", 10000, 0, Float.MAX_VALUE);


    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double StressPerRPM;
    public static double MassPerStress;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        StressPerRPM =  STRESS_PER_RPM.get().doubleValue();
        MassPerStress = MASS_PER_STRESS.get().doubleValue();
    }
}
