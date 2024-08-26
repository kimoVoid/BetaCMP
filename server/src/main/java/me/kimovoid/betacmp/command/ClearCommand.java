package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.BetaCMP;
import me.kimovoid.betacmp.command.exception.CommandException;
import net.minecraft.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class ClearCommand extends Command {
	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/clear";
	}

	@Override
	public void run(CommandSource source, String[] args) {
		if (source.getSourceName().equalsIgnoreCase("console")) {
			throw new CommandException("Sender must be a player");
		}

		ServerPlayerEntity p = BetaCMP.SERVER.playerManager.get(source.getSourceName());
		int items = 0;

		for(int i = 0; i < p.inventory.inventorySlots.length; ++i) {
			if (p.inventory.inventorySlots[i] != null) {
				p.inventory.inventorySlots[i] = null;
				items++;
			}
		}

		for(int i = 0; i < p.inventory.armorSlots.length; ++i) {
			if (p.inventory.armorSlots[i] != null) {
				p.inventory.armorSlots[i] = null;
				items++;
			}
		}

		sendSuccess(source.getSourceName(), "Cleared " + items + " item(s) from your inventory");
	}
}
