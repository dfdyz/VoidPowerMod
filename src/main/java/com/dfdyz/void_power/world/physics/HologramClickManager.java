package com.dfdyz.void_power.world.physics;

import com.dfdyz.void_power.world.blocks.hologram.HologramTE;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class HologramClickManager {
    /*
    public static final Map<ChunkPos, Set<HologramTE>> ManagedTE = Maps.newHashMap();

    public static void AddHologram(HologramTE hologramTE){
        if(hologramTE.getLevel() != null){
            ChunkPos cp = hologramTE.getLevel().getChunkAt(hologramTE.getBlockPos()).getPos();
            Set<HologramTE> s;
            if(!ManagedTE.containsKey(cp)){
                s = Sets.newHashSet();
                ManagedTE.put(cp, s);
            }
            else {
                s = ManagedTE.get(cp);
            }
            s.add(hologramTE);
        }
    }

    public static void Clean(){
        List<ChunkPos> should_remove = Lists.newLinkedList();
        ManagedTE.forEach((k, v) -> {
            v.removeIf(BlockEntity::isRemoved);
            if(v.isEmpty()){
                should_remove.add(k);
            }
        });
        should_remove.forEach(ManagedTE::remove);
    }

    public static void RayCastHologram(Player player){
        if (player.level() == null) return;
        Vec3 from = player.getEyePosition();
        Vec3 to = player.getForward().multiply(64, 64, 64).add(from);


        BlockHitResult bhr = RaycastUtilsKt.clipIncludeShips(player.level(),
                new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player), true);

        double distance = 64;
        if(bhr.getType() != HitResult.Type.MISS){
            distance = bhr.getLocation().distanceTo(from);
        }


        // for minecraft
        RCH rch0 = RayCastHologramAroundChunk(player.chunkPosition(), player, distance);
        // for vs
        AABBdc clipAABB = (new AABBd(VectorConversionsMCKt.toJOML(from),
                VectorConversionsMCKt.toJOML(to))).correctBounds();

        Iterator<LoadedShip> ships = VSGameUtilsKt.getShipObjectWorld(player.level())
                .getLoadedShips().getIntersecting(clipAABB).iterator();

        // todo



    }

    static final int[][] map = new int[][]{
            {0, 0}, {0, 1}, {1, 0}, {0, -1}, {-1, 0},
            {-1, 1}, {1, -1}, {-1, -1}, {1, 1}
    };

    public record RCH(HologramTE te, Vec3 pos, double d){
        public RCH Lower(RCH a, RCH b){
            return a.d < b.d ? a : b;
        }
    }

    public static RCH RayCastHologramAroundChunk(ChunkPos pos, Player player, double d){
        AtomicReference<HologramTE> te = new AtomicReference<>();
        AtomicReference<Vec3> a_clicked = new AtomicReference<>();
        AtomicReference<Double> d_ = new AtomicReference<>(d);
        for (int i = 0; i < map.length; i++) {
            ChunkPos cp = new ChunkPos(pos.x + map[i][0], pos.z + map[i][1]);
            if(ManagedTE.containsKey(cp)){
                Set<HologramTE> s = ManagedTE.get(cp);
                s.forEach((e) -> {
                    Vec3 clicked = e.TryClick(player);
                    if(clicked != null){
                        //todo

                        // check
                        if(clicked.distanceTo(player.getEyePosition()) < d_.get()){
                            d_.set(d);
                            te.set(e);
                            a_clicked.set(clicked);
                        }
                    }
                });
            }
        }
        return new RCH(te.get(), a_clicked.get(), d_.get());
    }
     */

}
