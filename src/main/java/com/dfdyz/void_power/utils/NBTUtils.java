package com.dfdyz.void_power.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public class NBTUtils {

    public static Tag NBT(BlockPos bp){
        return new IntArrayTag(new int[]{
                bp.getX(),bp.getY(),bp.getZ()
        });
    }


    public static @Nullable BlockPos BlockPos(Tag tag){
        if(tag instanceof IntArrayTag){
            int[] a = ((IntArrayTag)tag).getAsIntArray();
            return new BlockPos(a[0], a[1], a[2]);
        }
        else {
            return null;
        }
    }


}
