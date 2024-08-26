package me.kimovoid.betacmp.mixin.fix;

import net.minecraft.server.network.handler.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin {

    @ModifyConstant(
            method = "tick()V",
            constant = @Constant(intValue = 600)
    )
    private int setLoginTimeout(int old) {
        return 600 * 50;
    }
}
