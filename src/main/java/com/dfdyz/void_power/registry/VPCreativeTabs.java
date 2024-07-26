package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.VoidPowerMod;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class VPCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VoidPowerMod.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = REGISTER.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Components.translatable("itemGroup."+ VoidPowerMod.MODID +".main"))
                    .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
                    .icon(() -> {
                        return new ItemStack(VPBlocks.VOID_ENGINE.asItem());
                    })
                    .displayItems((params, output) -> {
                        List<ItemStack> items = VoidPowerMod.REGISTRATE.getAll(Registries.ITEM)
                                .stream()
                                .map((regItem) -> new ItemStack(regItem.get()))
                                .toList();
                        output.acceptAll(items);
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
