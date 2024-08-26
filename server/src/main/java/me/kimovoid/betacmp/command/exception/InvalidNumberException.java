package me.kimovoid.betacmp.command.exception;

public class InvalidNumberException extends CommandException {
	public InvalidNumberException() {
		this("'%s' is not a valid number");
	}

	public InvalidNumberException(String string, Object... objects) {
		super(string, objects);
	}
}
