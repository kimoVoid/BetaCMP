package me.kimovoid.betacmp;

import me.kimovoid.betacmp.command.*;
import me.kimovoid.betacmp.settings.SettingsManager;
import net.minecraft.server.MinecraftServer;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetaCMP implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static MinecraftServer SERVER;
	public static BetaCMP INSTANCE;

	public static final Map<String, ICommand> commandsByName = new HashMap<>();
	public static List<String> opCommands = new ArrayList<>();

	@Override
	public void init() {
		INSTANCE = this;
		LOGGER.info("Initializing b1.8.1 CMP");
		registerCommands();
	}

	/* Called from mixin when the server instance is stored */
	public void mcInit() {
		LOGGER.info("Applying rules from betacmp.conf");
		SettingsManager.parseRules();
		SettingsManager.applyConf();
	}

	public static void registerCommands() {
		registerCommand(new ClearCommand());
		registerCommand(new GiveCommand());
		registerCommand(new PingCommand());
		registerCommand(new PlayerCommand());
		registerCommand(new RuleCommand());
		registerCommand(new SetBlockCommand());
		registerCommand(new SummonCommand());
		registerCommand(new ToggledownfallCommand());
	}

	public static void registerCommand(ICommand command) {
		commandsByName.put(command.getName(), command);
		if (command.requiresOp()) opCommands.add(command.getName());
	}
}