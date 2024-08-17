package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.world.blocks.engine_controller.EngineControllerBlock;
import com.dfdyz.void_power.world.blocks.glass_screen.GlassScreenBlock;
import com.dfdyz.void_power.world.blocks.hologram.HologramBlock;
import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.dfdyz.void_power.world.blocks.void_engine.VoidEngineBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;

import static com.dfdyz.void_power.VoidPowerMod.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

@SuppressWarnings("removal")
public class VPBlocks {

    static {
        REGISTRATE.setCreativeTab(VPCreativeTabs.TAB);
    }

    public static final BlockEntry<VoidEngineBlock> VOID_ENGINE = REGISTRATE.block(VoidEngineBlock.ID, VoidEngineBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(TagGen.axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            //.addLayer(() -> RenderType::translucent)
            //.transform(BlockStressDefaults.setImpact(8.0))
            .item()
            .transform(customItemModel())
            .register();

    //Blocks.GLASS

    public static final BlockEntry<EngineControllerBlock> ENGINE_CONTROLLER_BLOCK = REGISTRATE.block(EngineControllerBlock.ID, EngineControllerBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(TagGen.axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            //.addLayer(() -> RenderType::translucent)
            //.transform(BlockStressDefaults.setImpact(8.0))
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<GlassScreenBlock> GLASS_SCREEN_BLOCK = REGISTRATE.block(GlassScreenBlock.ID, GlassScreenBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(TagGen.axeOrPickaxe())
            //.blockstate(BlockStateGen.)
            //.addLayer(() -> RenderType::solid)
            .addLayer(() -> RenderType::translucent)
            //.transform(BlockStressDefaults.setImpact(8.0))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<HologramBlock> HOLOGRAM_BLOCK = REGISTRATE.block(HologramBlock.ID, HologramBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(TagGen.axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .addLayer(() -> RenderType::translucent)
            //.transform(BlockStressDefaults.setImpact(8.0))
            .item()
            .transform(customItemModel())
            .register();

    public static void register(){

    }

}
