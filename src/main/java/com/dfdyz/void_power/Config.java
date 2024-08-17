package com.dfdyz.void_power;

import com.dfdyz.void_power.utils.ReflectionUtils;
import dan200.computercraft.core.computer.computerthread.ComputerThread;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@SuppressWarnings("CallToPrintStackTrace")
@Mod.EventBusSubscriber(modid = VoidPowerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue STRESS_PER_RPM = BUILDER
            .comment("Engine cost stress per rpm.")
            .defineInRange("EngineStressPerRPM", 8, 0., Float.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue MASS_PER_STRESS = BUILDER
            .comment("The weight that can be driven of per stress*rpm cost.")
            .defineInRange("EngineMassPerStress", 2500, 0, Float.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue CC_DEFAULT_MIN_PERIOD_FACTOR = BUILDER
            .comment("Time factor of CC‘s Computer can run max per time(default time is 5ms). If your CPU's clock speed is not high enough, don't change this")
            .defineInRange("CC_DEFAULT_MIN_PERIOD_FACTOR", 1, 0.5, 8);

    private static final ForgeConfigSpec.BooleanValue SCREEN_FORCED_USE_VANILLA_SHADER = BUILDER
            .comment("Force glass screen render use vanilla shader. If you use shader pack and has some render issue with glass screen, try turn on this.")
            .define("SCREEN_FORCED_USE_VANILLA_SHADER", false);

    private static final ForgeConfigSpec.BooleanValue RESET_CONTROLLER_WHEN_LEFT = BUILDER
            .comment("Reset input state of tweaked controller block after player left.")
            .define("RESET_CONTROLLER_WHEN_LEFT", true);


    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double StressPerRPM;
    public static double MassPerStress;
    public static double DefaultMinPeriodFactor = 1;
    public static boolean ForceUseVanillaShader = false;
    public static boolean ResetControllerWhileLeft = true;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        StressPerRPM =  STRESS_PER_RPM.get().doubleValue();
        MassPerStress = MASS_PER_STRESS.get().doubleValue();
        DefaultMinPeriodFactor = CC_DEFAULT_MIN_PERIOD_FACTOR.get().doubleValue();
        ForceUseVanillaShader = SCREEN_FORCED_USE_VANILLA_SHADER.get().booleanValue();
        ResetControllerWhileLeft = RESET_CONTROLLER_WHEN_LEFT.get().booleanValue();
    }

}
