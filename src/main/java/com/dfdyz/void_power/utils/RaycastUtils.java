package com.dfdyz.void_power.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RaycastUtils {


    public static BlockPos GetBlockPos(Vec3 pos){
        int i = Mth.floor(pos.x);
        int j = Mth.floor(pos.y);
        int k = Mth.floor(pos.z);
        return new BlockPos(i,j,k);
    }


}
