package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.BetaCMP;
import me.kimovoid.betacmp.command.exception.IncorrectUsageException;
import net.minecraft.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class PingCommand extends Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "/ping [player]";
    }

    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public void run(CommandSource source, String[] args) {
        if (args.length < 1 && BetaCMP.SERVER.playerManager.get(source.getSourceName()) == null)
            throw new IncorrectUsageException(String.format("%s. Sender must be a player.", getUsage(source)));

        ServerPlayerEntity target;

        if (args.length < 1) {
            target = BetaCMP.SERVER.playerManager.get(source.getSourceName());
        } else {
            if (BetaCMP.SERVER.playerManager.get(args[0]) == null)
                throw new IncorrectUsageException(String.format("%s. Player %s is not online.", getUsage(source), args[0]));

            target = BetaCMP.SERVER.playerManager.get(args[0]);
        }

        int ping = target.ping;
        source.sendMessage((target.name.equals(source.getSourceName()) ? "Your" : target.name + "'s") + " ping is: " + ping + " ms");
    }
}
