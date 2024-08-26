package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.BetaCMP;
import me.kimovoid.betacmp.command.exception.CommandException;
import me.kimovoid.betacmp.command.exception.IncorrectUsageException;
import net.minecraft.command.source.CommandSource;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class SummonCommand extends Command {

	@Override
	public String getName() {
		return "summon";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/summon <entity> [x] [y] [z]";
	}

	@Override
	public void run(CommandSource source, String[] args) {
		if (args.length < 1) {
			throw new IncorrectUsageException(getUsage(source));
		}

		if (source.getSourceName().equalsIgnoreCase("console")) {
			throw new CommandException("Sender must be a player");
		}

		ServerPlayerEntity p = BetaCMP.SERVER.playerManager.get(source.getSourceName());

		double x = p.x;
		double y = p.y;
		double z = p.z;

		if (args.length > 1) {
			if (args.length < 4) {
				throw new IncorrectUsageException(getUsage(source));
			}
			x = parseCoordinate(p.x, args[1]);
			y = parseCoordinate(p.y, args[2]);
			z = parseCoordinate(p.z, args[3]);
		}

		try {
			Entity entity = Entities.createSilently(args[0], p.world);
			entity.setPosition(x, y, z);
			p.world.addEntity(entity);
			sendSuccess(p.name, "Summoned " + Entities.getKey(entity));
		} catch (Exception ex) {
			throw new CommandException("Invalid entity");
		}
	}
}
