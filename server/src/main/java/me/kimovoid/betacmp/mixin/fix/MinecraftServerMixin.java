package me.kimovoid.betacmp.mixin.fix;

import net.minecraft.server.network.ListenThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Shadow public ListenThread connections;

	/*
	 * Move connection ticks outside the game ticks
	 * Saves 50ms of latency
	 */
	@Inject(
			method = "run()V", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V", shift = At.Shift.BEFORE)
	)
	private void tickConnections(CallbackInfo ci) {
		this.connections.tick();
	}

	/* Avoid unnecessary connection ticks */
	@Redirect(
			method = "tick()V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ListenThread;tick()V")
	)
	private void removeConnectionTick(ListenThread thread) {}
}
