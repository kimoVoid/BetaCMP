package me.kimovoid.betacmp.mixin.fakeplayer;

import net.minecraft.command.source.CommandSource;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements CommandSource {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketHandler;)V"
            )
    )
    private void redirect(Connection connection, PacketHandler handler) {
        if (connection != null) connection.setListener(handler);
    }
}