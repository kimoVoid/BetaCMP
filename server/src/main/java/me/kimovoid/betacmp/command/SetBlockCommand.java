package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.BetaCMP;
import me.kimovoid.betacmp.command.exception.CommandException;
import me.kimovoid.betacmp.command.exception.IncorrectUsageException;
import net.minecraft.block.Block;
import net.minecraft.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class SetBlockCommand extends Command {

    @Override
    public String getName() {
        return "setblock";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "/setblock <x> <y> <z> <id> [data]";
    }

    @Override
    public void run(CommandSource source, String[] args) {
        if (source.getSourceName().equalsIgnoreCase("console")) {
            throw new CommandException("Sender must be a player");
        }

        ServerPlayerEntity player = BetaCMP.SERVER.playerManager.get(source.getSourceName());

        if (args.length < 4) {
            throw new IncorrectUsageException(getUsage(source));
        }

        int x = MathHelper.floor(parseCoordinate(player.x, args[0]));
        int y = MathHelper.floor(parseCoordinate(player.y, args[1]));
        int z = MathHelper.floor(parseCoordinate(player.z, args[2]));

        int blockId;
        try {
            blockId = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            blockId = nameToBlockId(args[3]);
        }

        if (blockId == -1) {
            throw new CommandException("Block not found");
        }

        int data = 0;
        if (args.length >= 5) {
            data = parseInt(args[4], 0, 15);
        }

        if (!player.world.isChunkLoaded(x, y, z)) {
            throw new CommandException(String.format("Chunk at position [%s, %s, %s] is not loaded", x, y, z));
        }

        if (!player.world.setBlockWithMetadata(x, y, z, blockId, data)) {
            throw new CommandException(String.format("Block [%s, %s, %s] was not changed", x, y, z));
        } else {
            sendSuccess(source.getSourceName(), String.format("Set block at position [%s, %s, %s] to %s%s",
                    x, y, z, Block.BY_ID[blockId].getName(), data != 0 ? ":" + data : ""));
        }
    }

    public static int nameToBlockId(String n) {
        String name = n.replace("_", "");
        for (int i = 0; i < Block.BY_ID.length; i++) {
            if (Block.BY_ID[i] == null)
                continue;
            String translatedName = Block.BY_ID[i].getName().replace(" ", ""); // Remove spaces
            if (translatedName.equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
}
