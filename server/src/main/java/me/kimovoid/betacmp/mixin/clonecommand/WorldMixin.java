package me.kimovoid.betacmp.mixin.clonecommand;

import me.kimovoid.betacmp.helpers.ServerWorldHelper;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(World.class)
public class WorldMixin implements ServerWorldHelper {

    @Shadow
    private TreeSet<ScheduledTick> scheduledTicksInOrder;

    @Shadow
    private Set<ScheduledTick> scheduledTicks;

    public List<ScheduledTick> collectScheduledTicks(StructureBox bounds) {
        ArrayList<ScheduledTick> ticks = null;

        collectScheduledTicks(this.scheduledTicksInOrder, ticks, bounds);
        collectScheduledTicks(this.scheduledTicks, ticks, bounds);

        return ticks;
    }

    private static void collectScheduledTicks(Iterable<ScheduledTick> src, Collection<ScheduledTick> dst, StructureBox bounds) {
        for (ScheduledTick tick : src) {
            if (tick.x >= bounds.minX && tick.x <= bounds.maxX && tick.y >= bounds.minY && tick.y <= bounds.maxY && tick.z >= bounds.minZ && tick.z <= bounds.maxZ) {
                dst.add(tick);
            }
        }
    }
}
