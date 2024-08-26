package me.kimovoid.betacmp.fakeplayer;

import net.minecraft.network.Connection;
import net.minecraft.network.packet.ChatMessagePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;

public class FakeNetworkHandler extends ServerPlayNetworkHandler {

	private final ServerPlayerEntity player;

	public FakeNetworkHandler(MinecraftServer server, Connection connection, ServerPlayerEntity player) {
		super(server, connection, player);
		this.player = player;
	}

	@Override
	public void tick() {}

	@Override
	public void sendPacket(Packet packet) {}

	@Override
	public int getBlockDataSendQueueSize() {
		return 0;
	}

	@Override
	public void disconnect(String reason) {
		this.player.server.playerManager.sendPacket(new ChatMessagePacket("§e" + this.player.name + " left the game."));
		this.player.server.playerManager.remove(this.player);
		this.disconnected = true;
	}
}
