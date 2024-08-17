package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.client.gui.HologramGUI;
import com.dfdyz.void_power.menu.HologramMenu;
import com.dfdyz.void_power.client.renderer.tileentities.glass_screen.GlassScreenInstance;
import com.dfdyz.void_power.client.renderer.tileentities.glass_screen.ScreenRenderer;
import com.dfdyz.void_power.client.renderer.tileentities.hologram.HologramInstance;
import com.dfdyz.void_power.client.renderer.tileentities.hologram.HologramRenderer;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerBlock;
import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerTE;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenBlock;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.dfdyz.void_power.world.blocks.hologram.HologramBlock;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineBlock;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineTE;
import com.dfdyz.void_power.client.renderer.tileentities.void_engine.VoidEngineTEInstance;
import com.dfdyz.void_power.client.renderer.tileentities.void_engine.VoidEngineTERenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.dfdyz.void_power.VoidPowerMod.REGISTRATE;

public class VPTileEntities {


    public static final BlockEntityEntry<VoidEngineTE> VOID_ENGINE_TE = REGISTRATE
            .blockEntity(VoidEngineBlock.ID, VoidEngineTE::new)
            .instance(() -> VoidEngineTEInstance::new, false)
            .validBlock(VPBlocks.VOID_ENGINE)
            .renderer(() -> VoidEngineTERenderer::new)
            .register();

    public static final BlockEntityEntry<EngineControllerTE> ENGINE_CONTROLLER_TE = REGISTRATE
            .blockEntity(EngineControllerBlock.ID, EngineControllerTE::new)
            .validBlock(VPBlocks.ENGINE_CONTROLLER_BLOCK)
            .register();

    public static final BlockEntityEntry<GlassScreenTE> GLASS_SCREEN_TE = REGISTRATE
            .blockEntity(GlassScreenBlock.ID, GlassScreenTE::new)
            .instance(() -> GlassScreenInstance::new, true)
            .validBlock(VPBlocks.GLASS_SCREEN_BLOCK)
            .renderer(() -> ScreenRenderer::new)
            .register();

    public static final BlockEntityEntry<HologramTE> HOLOGRAM_TE = REGISTRATE
            .blockEntity(HologramBlock.ID, HologramTE::new)
            .instance(() -> HologramInstance::new, true)
            .validBlock(VPBlocks.HOLOGRAM_BLOCK)
            .renderer(() -> HologramRenderer::new)
            .register();


    public static final MenuEntry<HologramMenu> HOLOGRAM_GUI = REGISTRATE
            .menu(HologramBlock.ID, HologramMenu::new, () -> HologramGUI::new).register();;



    public static void register(){

    }




}
