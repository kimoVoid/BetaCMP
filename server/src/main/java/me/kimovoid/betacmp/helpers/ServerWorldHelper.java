package me.kimovoid.betacmp.helpers;

import net.minecraft.world.ScheduledTick;
import net.minecraft.world.gen.structure.StructureBox;

import java.util.List;

public interface ServerWorldHelper {
    public List<ScheduledTick> collectScheduledTicks(StructureBox bounds);
}