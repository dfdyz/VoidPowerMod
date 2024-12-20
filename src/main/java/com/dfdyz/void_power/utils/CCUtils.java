package com.dfdyz.void_power.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.joml.*;
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCUtils {

    public static ServerContext context;


    public static Map<Integer, ServerComputer> computers = Maps.newConcurrentMap();

    public static ServerComputer getComputerById(int id){
        return computers.getOrDefault(id, null);
    }


    public static Map<String, Double> dumpVec3(Vector3dc vec){
        return Map.of(
                "x", vec.x(),
                "y", vec.y(),
                "z", vec.z()
        );
    }

    public static Map<String, Double> dumpVec3(double x, double y, double z){
        return Map.of(
                "x", x,
                "y", y,
                "z", z
        );
    }

    public static Map<String, Double> dumpVec4(Vector4dc vec){
        return Map.of(
                "x", vec.x(),
                "y", vec.y(),
                "z", vec.z(),
                "w", vec.w()
        );
    }

    public static Map<String, Double> dumpVec4(Quaterniondc vec){
        return Map.of(
                "x", vec.x(),
                "y", vec.y(),
                "z", vec.z(),
                "w", vec.w()
        );
    }

    public static Map<String, Double> dumpVec4(double x, double y, double z, double w){
        return Map.of(
                "x", x,
                "y", y,
                "z", z,
                "w", w
        );
    }

    public static List<List<Double>> dumpMat3(Matrix3dc mat3){
        List<List<Double>> mat = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            Vector3d row = mat3.getRow(i, new Vector3d());
            mat.add(List.of(row.x, row.y, row.z));
        }
        return mat;
    }

    public static List<List<Double>> dumpMat3(double[][] m){
        return List.of(
                List.of(m[0][0], m[0][1], m[0][2]),
                List.of(m[1][0], m[1][1], m[1][2]),
                List.of(m[2][0], m[2][1], m[2][2])
        );
    }

}
