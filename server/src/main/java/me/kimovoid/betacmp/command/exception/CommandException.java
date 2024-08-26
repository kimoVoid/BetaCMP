package me.kimovoid.betacmp.command.exception;

public class CommandException extends RuntimeException {
	private Object[] args;

	public CommandException(String reason, Object... args) {
		super(reason);
		this.args = args;
	}

	public Object[] getArgs() {
		return this.args;
	}
}
