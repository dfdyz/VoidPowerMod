package com.dfdyz.void_power.utils;

import org.joml.Matrix4dc;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

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



}
