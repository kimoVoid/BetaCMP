package me.kimovoid.betacmp.mixin.access;

import net.minecraft.server.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerInteractionManager.class)
public interface InteractionManagerAccessor {

    @Accessor("wasMiningBlock")
    public boolean wasMiningBlock();

    @Accessor("wasMiningBlock")
    public void setWasMiningBlock(boolean b);
}
