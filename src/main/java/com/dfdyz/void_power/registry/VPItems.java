package com.dfdyz.void_power.registry;

import com.dfdyz.void_power.VoidPowerMod;
import com.dfdyz.void_power.world.items.VRGlassesItem;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.resources.ResourceLocation;

import static com.dfdyz.void_power.VoidPowerMod.REGISTRATE;
import static com.simibubi.create.AllTags.forgeItemTag;

public class VPItems {
    static {
        REGISTRATE.setCreativeTab(VPCreativeTabs.TAB);
    }

    public static final ItemEntry<VRGlassesItem> VR_GLASSES = REGISTRATE
            .item(VRGlassesItem.ID,
                    p -> new VRGlassesItem(AllArmorMaterials.COPPER, p, VoidPowerMod.getRL(VRGlassesItem.ID)))
            .tag(forgeItemTag("armors/helmets"))
            .register();

    public static void register(){

    }
}
