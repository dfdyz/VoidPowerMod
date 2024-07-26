package com.dfdyz.void_power;

import com.dfdyz.void_power.network.PacketManager;
import com.dfdyz.void_power.registry.VPBlocks;
import com.dfdyz.void_power.registry.VPCreativeTabs;
import com.dfdyz.void_power.registry.VPItems;
import com.dfdyz.void_power.registry.VPTileEntities;
import com.dfdyz.void_power.utils.CCUtils;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dan200.computercraft.shared.computer.core.ServerContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VoidPowerMod.MODID)
public class VoidPowerMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "void_power";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(VoidPowerMod.MODID);


    public VoidPowerMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        if(FMLEnvironment.dist == Dist.CLIENT){
            modEventBus.addListener(this::clientSetup);
        }

        VPCreativeTabs.register(modEventBus);
        VPItems.register();
        VPBlocks.register();
        VPTileEntities.register();
        modEventBus.register(PacketManager.CHANNEL);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        event.enqueueWork(PacketManager::Init);
        //LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void clientSetup(FMLClientSetupEvent event)
    {
        // Some client setup code
        //LOGGER.info("HELLO FROM CLIENT SETUP");
        //LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        //if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS);
            //event.accept(EXAMPLE_BLOCK_ITEM);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event){
        LOGGER.error("ServerStarted.");
        CCUtils.context = ServerContext.get(event.getServer());
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    //@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
}
