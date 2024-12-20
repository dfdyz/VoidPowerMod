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

    private static final ForgeConfigSpec.IntValue HOLOGRAM_RES_X_MAX = BUILDER
            .comment("Max width can set for a hologram.")
            .defineInRange("HOLOGRAM_WIDTH_MAX", 1024, 128, 2048);

    private static final ForgeConfigSpec.IntValue HOLOGRAM_RES_Y_MAX = BUILDER
            .comment("Max height can set for a hologram..")
            .defineInRange("HOLOGRAM_HEIGHT_MAX", 1024, 128, 2048);

    private static final ForgeConfigSpec.IntValue HOLOGRAM_BUFFER_COUNT = BUILDER
            .comment("Max count of frame buffer in a hologram.")
            .defineInRange("HOLOGRAM_BUFFER_COUNT", 8, 2, 32);


    private static final ForgeConfigSpec.BooleanValue WIRELESS_HUB_UNLIMITED = BUILDER
            .comment("Remove wireless peripheral hub distance limited.")
            .define("WIRELESS_HUB_UNLIMITED", true);

    private static final ForgeConfigSpec.IntValue HOLOGRAM_FONT_COUNT = BUILDER
            .comment("Max count of font for a hologram.")
            .defineInRange("HOLOGRAM_FONT_COUNT", 2, 0, 8);

    private static final ForgeConfigSpec.IntValue HOLOGRAM_FORCE_FULL_UPDATE_TICK = BUILDER
            .comment("The max ticks between two force full sync(a tick after a call of 'hologram.Flush(true)') of hologram. set zero to disable forced full update.")
            .defineInRange("HOLOGRAM_FORCE_FULL_UPDATE_TICK", 20, -1, 400);


    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double StressPerRPM;
    public static double MassPerStress;
    public static double DefaultMinPeriodFactor = 1;
    public static boolean ForceUseVanillaShader = false;
    public static boolean ResetControllerWhileLeft = true;
    public static int HologramFontCount = 2;

    public static int HologramMaxBufferCount = 8;
    public static int ForceFullUpdateTick = 2;
    public static boolean EnableForceFullUpdate = true;

    public static int holo_w_mx = 1024;
    public static int holo_h_mx = 1024;

    public static boolean UnlimitDistance = true;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        StressPerRPM = STRESS_PER_RPM.get();
        MassPerStress = MASS_PER_STRESS.get();
        DefaultMinPeriodFactor = CC_DEFAULT_MIN_PERIOD_FACTOR.get();
        ForceUseVanillaShader = SCREEN_FORCED_USE_VANILLA_SHADER.get();
        ResetControllerWhileLeft = RESET_CONTROLLER_WHEN_LEFT.get();

        UnlimitDistance = WIRELESS_HUB_UNLIMITED.get();

        holo_w_mx = HOLOGRAM_RES_X_MAX.get();
        holo_h_mx = HOLOGRAM_RES_Y_MAX.get();
        HologramMaxBufferCount = HOLOGRAM_BUFFER_COUNT.get();

        HologramFontCount = HOLOGRAM_FONT_COUNT.get();
        ForceFullUpdateTick = HOLOGRAM_FORCE_FULL_UPDATE_TICK.get();
        EnableForceFullUpdate = ForceFullUpdateTick > 0;
    }

}
