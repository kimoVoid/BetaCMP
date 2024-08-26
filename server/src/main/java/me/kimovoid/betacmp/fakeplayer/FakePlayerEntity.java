package me.kimovoid.betacmp.fakeplayer;

import me.kimovoid.betacmp.mixin.access.InteractionManagerAccessor;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.ChatMessagePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HitResult;

import java.io.File;
import java.util.logging.LogManager;

public class FakePlayerEntity extends ServerPlayerEntity {

	public boolean jumping = false;
	public boolean ticking = true;
	public boolean digging = false;
	public boolean using = false;
	public int targetX = -1;
	public int targetY = -1;
	public int targetZ = -1;

	public FakePlayerEntity(MinecraftServer server, ServerWorld world, String name) {
		super(server, world, name, new ServerPlayerInteractionManager(world));
	}

	public static void createFakePlayer(String name, MinecraftServer server, double x, double y, double z, float yaw, float pitch, int dimension) {
		String playerName = name;
		final File allPlayerNames = new File( server.properties.getString("level-name", "world") + "/players");
		final String[] names = allPlayerNames.list();
		if (names != null) {
			for (final String n : names) {
				final String existing = n.replaceAll(".dat", "");
				if (existing.equalsIgnoreCase(playerName)) {
					playerName = existing;
					break;
				}
			}
		}
		final ServerWorld world = server.getWorld(dimension);
		FakePlayerEntity fakePlayer = new FakePlayerEntity(server, world, playerName);

		server.playerManager.load(fakePlayer);
		fakePlayer.dimensionId = dimension;
		fakePlayer.setPosition(x, y, z);
		fakePlayer.yaw = yaw;
		fakePlayer.pitch = pitch;
		fakePlayer.velocityX = 0.0;
		fakePlayer.velocityY = 0.0;
		fakePlayer.velocityZ = 0.0;

		fakePlayer.networkHandler = new FakeNetworkHandler(server, null, fakePlayer);
		server.playerManager.add(fakePlayer);

		fakePlayer.health = 20;
		fakePlayer.sleeping = false;
		fakePlayer.stepHeight = 0.6f;
		fakePlayer.refreshPositionAndAngles(fakePlayer.x, fakePlayer.y, fakePlayer.z, fakePlayer.yaw, fakePlayer.pitch);

		sendLogin(fakePlayer);
	}

	public static void respawnFakePlayer(String name, MinecraftServer server) {
		String playerName = name;
		final File allPlayerNames = new File( server.properties.getString("level-name", "world") + "/players");
		final String[] names = allPlayerNames.list();
		if (names != null) {
			for (final String n : names) {
				final String existing = n.replaceAll(".dat", "");
				if (existing.equalsIgnoreCase(playerName)) {
					playerName = existing;
					break;
				}
			}
		}
		final ServerWorld world = server.getWorld(0);
		FakePlayerEntity fakePlayer = new FakePlayerEntity(server, world, playerName);
		server.playerManager.load(fakePlayer);

		fakePlayer.networkHandler = new FakeNetworkHandler(server, null, fakePlayer);
		server.playerManager.add(fakePlayer);

		fakePlayer.health = 20;
		fakePlayer.sleeping = false;
		fakePlayer.stepHeight = 0.6f;
		fakePlayer.refreshPositionAndAngles(fakePlayer.x, fakePlayer.y, fakePlayer.z, fakePlayer.yaw, fakePlayer.pitch);

		sendLogin(fakePlayer);
	}

	public static void sendLogin(FakePlayerEntity fakePlayer) {
		LogManager.getLogManager().getLogger("Minecraft").info(fakePlayer.name + " [BOT] logged in with entity id " + fakePlayer.networkId + " at (" + fakePlayer.x + ", " + fakePlayer.y + ", " + fakePlayer.z + ")");
		fakePlayer.server.playerManager.sendPacket(new ChatMessagePacket("§e" + fakePlayer.name + " joined the game."));
	}

	public void tick() {
		if (!this.ticking) {
			return;
		}

		super.tick();
		this.tickPlayer(true);

		if (this.jumping) {
			if (this.onGround) this.jump();
		}

		if (this.isDigging()) {
			this.swingHand();
		}

		if (this.digging || this.using) {
			final HitResult block = this.rayTrace(4.5);

			if (block != null) {
				/* Dig block */
				if (this.digging) {
					if (!this.isDigging() || block.x != this.targetX || block.y != this.targetY || block.z != this.targetZ) {
						this.targetX = block.x;
						this.targetY = block.y;
						this.targetZ = block.z;
						this.setDigging(false);
						this.interactionManager.startMiningBlock(block.x, block.y, block.z, block.face);
						this.interactionManager.finishMiningBlock(block.x, block.y, block.z);
						this.setDigging(true);
					}
				}

				/* Use block */
				if (this.using) {
					boolean canUse = this.interactionManager.useBlock(this, this.world, this.inventory.getMainHandStack(), block.x, block.y, block.z, block.face);

					if (!canUse && this.inventory.getMainHandStack() != null) {
						canUse = this.interactionManager.useItem(this, this.world, this.inventory.getMainHandStack());
					}

					if (canUse) {
						this.swingHand();
					}
				}
			} else {
				/* Use item */
				if (this.using) {
					boolean canUse = false;

					if (this.inventory.getMainHandStack() != null) {
						canUse = this.interactionManager.useItem(this, this.world, this.inventory.getMainHandStack());
					}

					if (canUse) {
						this.swingHand();
					}
				}

				this.targetX = -1;
				this.targetY = -1;
				this.targetZ = -1;
				this.setDigging(false);
			}
		}
	}

	public boolean isDigging() {
		return ((InteractionManagerAccessor) this.interactionManager).wasMiningBlock();
	}

	public void setDigging(final boolean digging) {
		((InteractionManagerAccessor) this.interactionManager).setWasMiningBlock(digging);
	}

	public void onKilled(DamageSource src) {
		super.onKilled(src);
		this.onDisconnect();
		this.server.playerManager.remove(this);
		this.networkHandler.disconnected = true;
	}

	public HitResult rayTrace(double distance) {
		final Vec3d look = this.getCameraRotation().normalize();
		final Vec3d from = Vec3d.of(this.x, this.y + this.getEyeHeight(), this.z);
		final Vec3d to = from.add(look.x * distance, look.y * distance, look.z * distance);
		return this.world.rayTrace(from, to);
	}

	public void swingHand() {
		this.m_4519917();
	}

	public void lookAt(final double dx, final double dy, final double dz) {
		final Vec3d from = Vec3d.of(this.x, this.y + 1.0, this.z);
		final Vec3d to = Vec3d.of(dx, dy, dz);
		final Vec3d res = to.add(-from.x, -from.y, -from.z);
		final double x = res.x;
		final double z = res.z;

		if (x == 0.0 && z == 0.0) {
			this.pitch = res.y > 0.0 ? -90.0f : 90.0f;
			return;
		}

		final double theta = Math.atan2(-x, z);
		this.yaw = (float) Math.toDegrees((theta + 6.283185307179586) % 6.283185307179586);
		final double x2 = x * x;
		final double z2 = z * z;
		final double xz = Math.sqrt(x2 + z2);
		this.pitch = (float) Math.toDegrees(Math.atan(-res.y / xz));
	}

	public void stopAllActions() {
		this.jumping = false;
		this.setSneaking(false);
		this.setDigging(false);
		this.digging = false;
		this.using = false;
		this.targetX = -1;
		this.targetY = -1;
		this.targetZ = -1;
	}
}
