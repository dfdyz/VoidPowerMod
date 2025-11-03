package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.client.gui.ChannelModifierGUI;
import com.dfdyz.void_power.menu.ChannelModifierMenu;
import com.dfdyz.void_power.client.renderer.tileentities.glass_screen.GlassScreenInstance;
import com.dfdyz.void_power.client.renderer.tileentities.glass_screen.ScreenRenderer;
import com.dfdyz.void_power.client.renderer.tileentities.hologram.HologramInstance;
import com.dfdyz.void_power.client.renderer.tileentities.hologram.HologramRenderer;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenBlock;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenTE;
import com.dfdyz.void_power.world.blocks.hologram.HologramBlock;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.dfdyz.void_power.world.blocks.key_board.KeyBoardBlock;
import com.dfdyz.void_power.world.blocks.key_board.KeyBoardTE;
import com.dfdyz.void_power.world.blocks.redstone_link.*;
import com.dfdyz.void_power.world.items.ChannelModifierItem;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.dfdyz.void_power.VoidPowerMod.REGISTRATE;

public class VPTileEntities {


  /*  public static final BlockEntityEntry<VoidEngineTE> VOID_ENGINE_TE = REGISTRATE
            .blockEntity(VoidEngineBlock.ID, VoidEngineTE::new)
            .visual(() -> VoidEngineTEInstance::new, false)
            .validBlock(VPBlocks.VOID_ENGINE)
            .renderer(() -> VoidEngineTERenderer::new)
            .register();*/

    /*public static final BlockEntityEntry<EngineControllerTE> ENGINE_CONTROLLER_TE = REGISTRATE
            .blockEntity(EngineControllerBlock.ID, EngineControllerTE::new)
            .validBlock(VPBlocks.ENGINE_CONTROLLER_BLOCK)
            .register();*/

    public static final BlockEntityEntry<GlassScreenTE> GLASS_SCREEN_TE = REGISTRATE
            .blockEntity(GlassScreenBlock.ID, GlassScreenTE::new)
            .visual(() -> GlassScreenInstance::new, true)
            .validBlock(VPBlocks.GLASS_SCREEN_BLOCK)
            .renderer(() -> ScreenRenderer::new)
            .register();

    public static final BlockEntityEntry<HologramTE> HOLOGRAM_TE = REGISTRATE
            .blockEntity(HologramBlock.ID, HologramTE::new)
            .visual(() -> HologramInstance::new, true)
            .validBlock(VPBlocks.HOLOGRAM_BLOCK)
            .renderer(() -> HologramRenderer::new)
            .register();

    public static final BlockEntityEntry<KeyBoardTE> KEYBOARD_TE = REGISTRATE
            .blockEntity(KeyBoardBlock.ID, KeyBoardTE::new)
            .validBlock(VPBlocks.KEYBOARD_BLOCK)
            .register();


    public static final BlockEntityEntry<RSBroadcasterTE> RS_BROADCASTER_TE = REGISTRATE
            .blockEntity(RSBroadcasterBlock.ID, RSBroadcasterTE::new)
            //.instance(() -> HologramInstance::new, true)
            .validBlock(VPBlocks.RS_BROADCASTER_BLOCK)
            //.renderer(() -> HologramRenderer::new)
            .register();

    public static final BlockEntityEntry<RSReceiverTE> RS_RECEIVER_TE = REGISTRATE
            .blockEntity(RSReceiverBlock.ID, RSReceiverTE::new)
            //.instance(() -> HologramInstance::new, true)
            .validBlock(VPBlocks.RS_RECEIVER_BLOCK)
            //.renderer(() -> HologramRenderer::new)
            .register();


    public static final BlockEntityEntry<RSRouterTE> RS_ROUTER_TE = REGISTRATE
            .blockEntity(RSRouterBlock.ID, RSRouterTE::new)
            //.instance(() -> HologramInstance::new, true)
            .validBlock(VPBlocks.RS_ROUTER_BLOCK)
            //.renderer(() -> HologramRenderer::new)
            .register();

    public static final MenuEntry<ChannelModifierMenu> CHANNEL_MODIFIER_GUI = REGISTRATE
            .menu(ChannelModifierItem.ID, ChannelModifierMenu::new, () -> ChannelModifierGUI::new).register();

    public static void register(){

    }




}
