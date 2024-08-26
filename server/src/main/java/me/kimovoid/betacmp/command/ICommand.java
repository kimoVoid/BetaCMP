package me.kimovoid.betacmp.command;

import net.minecraft.command.source.CommandSource;

public interface ICommand {

	String getName();

	String getUsage(CommandSource source);

	void run(CommandSource source, String[] args);

	boolean requiresOp();
}
