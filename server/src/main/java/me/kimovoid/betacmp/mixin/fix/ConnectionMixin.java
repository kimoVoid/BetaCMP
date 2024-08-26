package me.kimovoid.betacmp.mixin.fix;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Socket;
import java.net.SocketException;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/net/Socket;setSoTimeout(I)V"))
    public void setTcpNoDelay(Socket socket, String name, PacketHandler listener, CallbackInfo ci) {
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException ignored) {}
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 1200))
    private int setTimeoutTime(int time) {
        return time * 50;
    }
}
