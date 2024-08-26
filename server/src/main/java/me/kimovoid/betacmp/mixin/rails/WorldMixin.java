package me.kimovoid.betacmp.mixin.rails;

import me.kimovoid.betacmp.settings.Settings;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(World.class)
public class WorldMixin {
	@Redirect(
		method = "tickChunks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/Block;tick(Lnet/minecraft/world/World;IIILjava/util/Random;)V"
		)
	)
	private void disableRailRandomTick(
		Block block,
		World world, int x, int y, int z, Random random
	) {
		int blockId = block.id;
		if (!Settings.disableRailRandomTick || blockId != Block.DETECTOR_RAIL.id) {
			block.tick(world, x, y, z, random);
		}
	}
}
