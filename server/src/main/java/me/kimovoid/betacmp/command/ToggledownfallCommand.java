package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.BetaCMP;
import net.minecraft.command.source.CommandSource;

public class ToggledownfallCommand extends Command {

	@Override
	public String getName() {
		return "toggledownfall";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/toggledownfall";
	}

	@Override
	public void run(CommandSource source, String[] args) {
		sendSuccess(source.getSourceName(), "Toggled downfall");
		BetaCMP.SERVER.getWorld(0).getData().setRainTime(1);
		if (BetaCMP.SERVER.getWorld(0).getData().isThundering()) {
			BetaCMP.SERVER.getWorld(0).getData().setThunderTime(1);
		}
	}
}