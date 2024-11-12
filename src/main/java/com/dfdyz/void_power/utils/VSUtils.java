package com.dfdyz.void_power.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

public class VSUtils {

    //VSGameUtilsKt.class

    public static double[][] getRotationMatrixRaw(ServerShip ship) {
        Matrix4dc transform = ship.getTransform().getShipToWorld();
        double[][] matrix = new double[4][4];

        for (int i = 0; i < 4; i++) {
            Vector4d row = transform.getRow(i, new Vector4d());
            matrix[i][0] = row.x;
            matrix[i][1] = row.y;
            matrix[i][2] = row.z;
            matrix[i][3] = row.w;
        }

        return matrix;
    }

    public static double[][] getRotationMatrixRaw(PhysShipImpl ship){
        Matrix4dc transform = ship.getTransform().getShipToWorld();
        double[][] matrix = new double[4][4];

        for (int i = 0; i < 4; i++) {
            Vector4d row = transform.getRow(i, new Vector4d());
            matrix[i][0] = row.x;
            matrix[i][1] = row.y;
            matrix[i][2] = row.z;
            matrix[i][3] = row.w;
        }

        return matrix;
    }


    public static Vector3d Vec3_Vector3d(Vec3 vec3){
        return new Vector3d(vec3.x, vec3.y, vec3.z);
    }

    public static Vector4d Vec3_Vector4d(Vec3 vec3){
        return new Vector4d(vec3.x, vec3.y, vec3.z, 1);
    }

    public static double GetBlockDistanceSqrBetween(@NotNull ServerLevel serverLevel, BlockPos bp1, BlockPos bp2){
        ServerShip ship1, ship2;
        ship1 = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, bp1);
        ship2 = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, bp2);
        boolean flag = false;
        Vector4d p1 = VSUtils.Vec3_Vector4d(bp1.getCenter()),
                p2 = VSUtils.Vec3_Vector4d(bp2.getCenter());
        if(ship1 != null){
            p1 = ship1.getShipToWorld().transform(p1);
            flag = true;
        }

        if(ship2 != null){
            p2 = ship2.getShipToWorld().transform(p2);
            flag = true;
        }
        if(flag){
            return (p2.x - p1.x) * (p2.x - p1.x) +
                    (p2.y - p1.y) * (p2.y - p1.y) +
                    (p2.z - p1.z) * (p2.z - p1.z);
        }

        else return bp1.distSqr(bp2);
    }
}
