package me.kimovoid.betacmp.mixin.rails;

import me.kimovoid.betacmp.settings.Settings;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin extends RailBlock {

    protected DetectorRailBlockMixin(int id, int sprite, boolean alwaysStraight) {
        super(id, sprite, alwaysStraight);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/DetectorRailBlock;setTicksRandomly(Z)Lnet/minecraft/block/Block;"
            )
    )
    private boolean removeRandomTick(boolean b) {
        return !Settings.disableRailRandomTick;
    }

    @Override
    public void updateMetadataOnPlaced(World world, int x, int y, int z, int metadata) {
        if (Settings.placeActivatedRails) {
            world.setBlockMetadata(x, y, z, 8);
        }
    }
}
