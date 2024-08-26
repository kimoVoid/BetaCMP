package me.kimovoid.betacmp.command.exception;

public class CommandSyntaxException extends CommandException {
	public CommandSyntaxException() {
		this("Invalid command syntax");
	}

	public CommandSyntaxException(String string, Object... objects) {
		super(string, objects);
	}
}
