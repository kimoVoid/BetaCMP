package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.BetaCMP;
import me.kimovoid.betacmp.command.exception.CommandException;
import me.kimovoid.betacmp.command.exception.IncorrectUsageException;
import me.kimovoid.betacmp.fakeplayer.FakePlayerEntity;
import net.minecraft.command.source.CommandSource;
import net.minecraft.network.packet.ChatMessagePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.HitResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlayerCommand extends Command {

    private final Set<String> actions = new HashSet<>(Arrays.asList("spawn", "respawn", "kill", "dig", "use", "sneak", "jump", "tick", "drop", "look", "hotbar", "chat", "stop"));

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "/player [name] [action]";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void run(CommandSource source, String[] args) {
        MinecraftServer server = BetaCMP.SERVER;

        if (args.length < 2) {
            throw new IncorrectUsageException(this.getUsage(source));
        }

        String name = args[0];
        String action = args[1];

        if (!this.actions.contains(action.toLowerCase())) {
            throw new CommandException(String.format("Invalid action. Valid actions are: §f%s", String.join(", ", actions)));
        }

        /* Spawn or respawn fake player */
        switch (action.toLowerCase()) {
            case "spawn":
                if (source.getSourceName().equalsIgnoreCase("console")) {
                    throw new CommandException("Sender must be a player.");
                }

                if (server.playerManager.get(name) != null) {
                    throw new CommandException(String.format("Player %s is already connected.", name));
                }

                if (name.length() > 16) {
                    throw new CommandException("Name cannot be longer than 16 characters");
                }

                ServerPlayerEntity player = server.playerManager.get(source.getSourceName());
                double x = player.x;
                double y = player.y;
                double z = player.z;
                int dimension = player.dimensionId;
                float yaw = player.yaw;
                float pitch = player.pitch;

                if (args.length > 2) {
                    String usage1 = "/player <name> spawn <dimension> <x> <y> <z>";

                    if (args.length < 6) {
                        throw new IncorrectUsageException(usage1);
                    }

                    try {
                        dimension = args[2].equalsIgnoreCase("overworld") ? 0 : (args[2].equalsIgnoreCase("nether") ? -1 : 2);
                        x = parseCoordinate(x, args[3]);
                        y = parseCoordinate(y, args[4]);
                        z = parseCoordinate(z, args[5]);

                        if (dimension == 2) {
                            throw new CommandException(String.format("\"%s\" is not a valid dimension", args[2]));
                        }
                    } catch (NumberFormatException var19) {
                        throw new IncorrectUsageException(usage1);
                    }
                }

                FakePlayerEntity.createFakePlayer(name, server, x, y, z, yaw, pitch, dimension);
                break;

            case "respawn":
                if (server.playerManager.get(name) != null) {
                    throw new CommandException(String.format("Player %s is already connected", name));
                }

                if (name.length() > 16) {
                    throw new CommandException("Name cannot be longer than 16 characters");
                }

                FakePlayerEntity.respawnFakePlayer(name, server);
                break;
        }

        /* Fake player actions */
        if (server.playerManager.get(name) == null) {
            throw new IncorrectUsageException(String.format("%s. Player %s is not connected", this.getUsage(source), name));
        }

        if (!(server.playerManager.get(name) instanceof FakePlayerEntity)) {
            throw new IncorrectUsageException(String.format("%s. Player %s is a real player", this.getUsage(source), name));
        }

        FakePlayerEntity fakePlayer = (FakePlayerEntity) server.playerManager.get(name);

        switch (action.toLowerCase()) {
            case "kill":
                fakePlayer.stopAllActions();
                fakePlayer.networkHandler.onDisconnect("Disconnected", new Object[]{});
                break;

            case "dig":
                if (args.length >= 3 && args[2].equalsIgnoreCase("once")) {
                    HitResult target = fakePlayer.rayTrace(4.5D);

                    if (target != null) {
                        fakePlayer.swingHand();
                        fakePlayer.interactionManager.startMiningBlock(target.x, target.y, target.z, target.face);
                        fakePlayer.interactionManager.finishMiningBlock(target.x, target.y, target.z);
                    }

                    return;
                }

                sendSuccess(source.getSourceName(), "Player " + fakePlayer.name + " is " + (fakePlayer.digging ? "no longer" : "now") + " digging");
                fakePlayer.digging = !fakePlayer.digging;

                if (!fakePlayer.digging) {
                    HitResult target = fakePlayer.rayTrace(4.5D);

                    if (target != null) {
                        fakePlayer.setDigging(false);
                    }

                    fakePlayer.setDigging(false);
                    fakePlayer.targetX = -1;
                    fakePlayer.targetY = -1;
                    fakePlayer.targetZ = -1;
                }
                break;

            case "jump":
                sendSuccess(source.getSourceName(), "Player " + fakePlayer.name + " is " + (fakePlayer.jumping ? "no longer" : "now") + " jumping");
                fakePlayer.jumping = !fakePlayer.jumping;
                break;

            case "sneak":
                sendSuccess(source.getSourceName(), "Player " + fakePlayer.name + " is " + (fakePlayer.isSneaking() ? "no longer" : "now") + " sneaking");
                fakePlayer.setSneaking(!fakePlayer.isSneaking());
                break;

            case "tick":
                sendSuccess(source.getSourceName(), "Player " + fakePlayer.name + " is " + (fakePlayer.ticking ? "no longer" : "now") + " ticking");
                fakePlayer.ticking = !fakePlayer.ticking;
                break;

            case "use":
                if (args.length >= 3 && args[2].equalsIgnoreCase("once")) {
                    boolean canUse = false;
                    HitResult target = fakePlayer.rayTrace(4.5D);

                    if (target != null) {
                        canUse = fakePlayer.interactionManager.useBlock(fakePlayer, fakePlayer.world, fakePlayer.inventory.getMainHandStack(), target.x, target.y, target.z, target.face);
                    }

                    if (!canUse && fakePlayer.inventory.getMainHandStack() != null) {
                        canUse = fakePlayer.interactionManager.useItem(fakePlayer, fakePlayer.world, fakePlayer.inventory.getMainHandStack());
                    }

                    if (canUse) {
                        fakePlayer.swingHand();
                    }

                    return;
                }

                sendSuccess(source.getSourceName(), "Player " + fakePlayer.name + " is " + (fakePlayer.using ? "no longer" : "now") + " right-clicking");
                fakePlayer.using = !fakePlayer.using;
                break;

            case "drop":
                if (args.length >= 3) {
                    switch (args[2].toLowerCase()) {
                        case "all":
                            for (int i = 0; i < fakePlayer.inventory.inventorySlots.length; ++i) {
                                if (fakePlayer.inventory.inventorySlots[i] == null) continue;
                                fakePlayer.dropItem(fakePlayer.inventory.removeStack(i, fakePlayer.inventory.inventorySlots[i].size), false);
                            }

                            for (int i = 0; i < fakePlayer.inventory.armorSlots.length; ++i) {
                                if (fakePlayer.inventory.armorSlots[i] == null) continue;
                                fakePlayer.dropItem(fakePlayer.inventory.armorSlots[i], false);
                                fakePlayer.inventory.inventorySlots[i] = null;
                            }
                            break;
                        case "one":
                            fakePlayer.dropItem();
                            break;
                        default:
                            throw new IncorrectUsageException("/player <name> drop [all/one]");
                    }
                    return;
                }

                fakePlayer.dropItem(fakePlayer.inventory.removeStack(fakePlayer.inventory.selectedSlot, fakePlayer.getMainHandStack().size), false);
                break;

            case "look":
                String lookUsage = "/player <name> look <direction> | /player <name> look at <x> <y> <z>";

                if (args.length < 3) {
                    throw new IncorrectUsageException(lookUsage);
                }

                String dir = args[2].toLowerCase();

                switch (dir) {
                    case "up":
                        fakePlayer.pitch = -90.0F;
                        break;
                    case "down":
                        fakePlayer.pitch = 90.0F;
                        break;
                    case "north":
                        fakePlayer.pitch = 0.0F;
                        fakePlayer.yaw = -180.0F;
                        break;
                    case "east":
                        fakePlayer.pitch = 0.0F;
                        fakePlayer.yaw = -90.0F;
                        break;
                    case "south":
                        fakePlayer.pitch = 0.0F;
                        fakePlayer.yaw = 0.0F;
                        break;
                    case "west":
                        fakePlayer.pitch = 0.0F;
                        fakePlayer.yaw = 90.0F;
                        break;
                    case "at":
                        if (source.getSourceName().equalsIgnoreCase("console")) {
                            throw new CommandException("Sender must be a player");
                        }

                        if (args.length < 6) {
                            throw new IncorrectUsageException(lookUsage);
                        }

                        ServerPlayerEntity player = server.playerManager.get(source.getSourceName());
                        double x = parseCoordinate(player.x, args[3]);
                        double y = parseCoordinate(player.y, args[4]);
                        double z = parseCoordinate(player.z, args[5]);
                        fakePlayer.lookAt(x, y, z);
                        break;
                    default:
                        throw new IncorrectUsageException(lookUsage);
                }
                break;

            case "hotbar":
                String hotbarUsage = "/player <name> hotbar <slot>";

                try {
                    int slot = Integer.parseInt(args[2]);

                    if (slot < 1 || slot > 9) {
                        throw new IncorrectUsageException(String.format("%s. Type a valid slot from 1-9", hotbarUsage));
                    }

                    fakePlayer.inventory.selectedSlot = slot - 1;
                } catch (NumberFormatException ex) {
                    throw new IncorrectUsageException(hotbarUsage);
                }
                break;

            case "stop":
                fakePlayer.stopAllActions();
                sendSuccess(source.getSourceName(), "Stopped all actions for player " + fakePlayer.name);
                break;

            case "chat":
                if (args.length < 3) {
                    throw new IncorrectUsageException("/player <name> chat <message>");
                }

                String msg = String.join(" ", args).split(" ", 3)[2];
                fakePlayer.networkHandler.handleChatMessage(new ChatMessagePacket(msg));
        }
    }
}
