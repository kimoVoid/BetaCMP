package me.kimovoid.betacmp.mixin.blocktick;

import com.llamalad7.mixinextras.sugar.Local;
import me.kimovoid.betacmp.settings.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.world.ScheduledTick;
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
	private void disableRandomTicks(
		Block block,
		World world, int x, int y, int z, Random random
	) {
		// TODO: Consider reworking this if we want to extend this feature to more blocks.
		if ((!Settings.disableRailRandomTick        || !(block instanceof DetectorRailBlock))
			&& (!Settings.disableLiquidRandomTick   || !(block instanceof LiquidBlock))
			&& (!Settings.disableRedstoneRandomTick || !(block instanceof RedstoneTorchBlock))) {

			block.tick(world, x, y, z, random);
		}
	}

	@Redirect(
		method = "scheduleTick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/World;doTicksImmediately:Z"
		)
	)
	private boolean makeTicksInstant(World world, @Local ScheduledTick var6) {
		Block block = Block.BY_ID[var6.blockId];

		// TODO: Consider reworking this if we want to extend this feature to more blocks.
		return world.doTicksImmediately
			|| (Settings.liquidInstantTick && block instanceof LiquidBlock)
			|| (Settings.redstoneInstantTick && (block instanceof RedstoneTorchBlock
												 || block instanceof RepeaterBlock));
	}
}
