package me.kimovoid.betacmp.command.exception;

public class IncorrectUsageException extends CommandSyntaxException {
	public IncorrectUsageException(String string, Object... objects) {
		super(string, objects);
	}
}
