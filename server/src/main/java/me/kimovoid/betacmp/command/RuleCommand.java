package me.kimovoid.betacmp.command;

import me.kimovoid.betacmp.command.exception.CommandException;
import me.kimovoid.betacmp.command.exception.IncorrectUsageException;
import me.kimovoid.betacmp.settings.ParsedRule;
import me.kimovoid.betacmp.settings.RuleCategory;
import me.kimovoid.betacmp.settings.SettingsManager;
import net.minecraft.command.source.CommandSource;

import java.util.Arrays;

public class RuleCommand extends Command {

    @Override
    public String getName() {
        return "rule";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "/rule <rule> <value>";
    }

    @Override
    public void run(CommandSource source, String[] args) {
        if (SettingsManager.locked) {
            printRules(source, "Locked with the following rules", SettingsManager.findNonDefault());
            return;
        }

        String tag = null;

        if (args.length == 0) {
            printRules(source, "Current rules", SettingsManager.findNonDefault());
            /* Ugly as hell but b1.8.1 has no formatting ¯\_(ツ)_/¯ */
            source.sendMessage("§7| View all rules with /rule list");
            source.sendMessage("§7| View defaults with /rule defaults");
            source.sendMessage("§7| View rules by category with /rule <category>");
            source.sendMessage("§7| Set rules with /rule <rule> <value>");
            source.sendMessage("§7| Set defaults with /rule setDefault <rule> <value>");
            return;
        }

        if (args.length == 1 && "list".equalsIgnoreCase(args[0])) {
            printRules(source, "All rules", SettingsManager.findAll(null));
            return;
        }

        if (args.length == 1 && "use".equalsIgnoreCase(args[0])) {
            throw new IncorrectUsageException("/rule use <preset>");
        }

        if ("defaults".equalsIgnoreCase(args[0])) {
            printRules(source, "Default rules", SettingsManager.findStartupOverrides());
            return;
        }

        if ("use".equalsIgnoreCase(args[0])) {
            if ("default".equalsIgnoreCase(args[1])) {
                SettingsManager.resetToConf();
                sendSuccess(source.getSourceName(), "Set all rules to user defaults");
                return;
            }

            if ("vanilla".equalsIgnoreCase(args[1])) {
                SettingsManager.resetToVanilla();
                sendSuccess(source.getSourceName(), "Set all rules to vanilla");
                return;
            }

            if ("survival".equalsIgnoreCase(args[1])) {
                SettingsManager.resetToSurvival();
                sendSuccess(source.getSourceName(), "Set all rules to survival defaults");
                return;
            }

            if ("creative".equalsIgnoreCase(args[1])) {
                SettingsManager.resetToCreative();
                sendSuccess(source.getSourceName(), "Set all rules to creative defaults");
                return;
            }

            if ("bugfixes".equalsIgnoreCase(args[1])) {
                SettingsManager.resetToBugFix();
                sendSuccess(source.getSourceName(), "Set all rules to bugfix defaults");
                return;
            }

            throw new IncorrectUsageException("/rule use <preset>");
        }

        if (args.length >= 2 && "list".equalsIgnoreCase(args[0])) {
            tag = args[1].toLowerCase();
            args = Arrays.copyOfRange(args, 2, args.length);
        }

        if (args.length == 0) {
            printRules(source, "Rules matching \"" + tag + "\"", SettingsManager.findAll(tag));
            return;
        }

        if ("setDefault".equalsIgnoreCase(args[0])) {
            if (args.length == 2 && "current".equalsIgnoreCase(args[1])) {
                for (String override : SettingsManager.findStartupOverrides()) {
                    SettingsManager.removeOverride(override);
                }
                for (String current : SettingsManager.findNonDefault()) {
                    SettingsManager.addOrSetOverride(current, SettingsManager.getRule(current));
                }

                sendSuccess(source.getSourceName(), "All current rules will be set upon restart");
                return;
            }

            if (args.length >= 2 && !SettingsManager.hasRule(args[1])) {
                throw new CommandException("Unknown rule: " + args[1]);
            }

            if (args.length != 3) {
                throw new IncorrectUsageException("/rule setDefault <rule|current> [value]");
            }

            boolean success = SettingsManager.addOrSetOverride(args[1], args[2]);

            if (success) {
                sendSuccess(source.getSourceName(), SettingsManager.getParsedRule(args[1]).name + " will default to: " + args[2]);
            } else {
                throw new CommandException(args[2] + " is not a legal value for " + SettingsManager.getParsedRule(args[1]).name);
            }

            return;
        }

        if ("removeDefault".equalsIgnoreCase(args[0])) {
            if (args.length != 2) {
                throw new IncorrectUsageException("/rule removeDefault <rule|all>");
            }

            if ("all".equalsIgnoreCase(args[1])) {
                for (String override : SettingsManager.findStartupOverrides()) {
                    SettingsManager.removeOverride(override);
                }

                sendSuccess(source.getSourceName(), "All rules will not be set upon restart");
                return;
            }

            boolean success = SettingsManager.removeOverride(args[1]);

            if (success) {
                sendSuccess(source.getSourceName(), SettingsManager.getParsedRule(args[1]).name + " will not be set upon restart");
            } else {
                throw new CommandException("Unknown rule: " + args[1]);
            }

            return;
        }

        if (!SettingsManager.hasRule(args[0])) {
            throw new CommandException("Unknown rule: " + args[0]);
        }

        if (args.length == 2) {
            boolean success = SettingsManager.set(args[0], args[1]);

            if (!success) {
                throw new IncorrectUsageException(getUsage(source));
            }

            sendSuccess(source.getSourceName(), String.format("Set %s to %s", SettingsManager.getParsedRule(args[0]).name, SettingsManager.getRule(args[0])));
            source.sendMessage("§7(To change permanently, use /rule setDefault <rule> <value>)");
            return;
        }

        ParsedRule<?> rule = SettingsManager.getParsedRule(args[0]);
        source.sendMessage("§e" + rule.name);
        source.sendMessage(rule.desc);
        for (String info : rule.extra) {
            source.sendMessage("&7" + info);
        }

        source.sendMessage(" ");
        StringBuilder tags = new StringBuilder("Tags: ");
        for (RuleCategory ctgy : rule.category) {
            tags.append("&b[").append(ctgy).append("] ");
        }
        source.sendMessage(tags.toString());

        source.sendMessage(String.format("Current value: %s%s (%s value)", rule.getValueString().equalsIgnoreCase("true") ? "&a" : "&c", rule.getValueString(), rule.getValueString().equalsIgnoreCase(rule.def) ? "default" : "modified"));
        StringBuilder options = new StringBuilder("Options: &e[ ");
        for (String opt : rule.options) {
            String color = rule.getValueString().equalsIgnoreCase(opt) ? "§e" : "§7";
            options.append(color).append("[").append(opt).append("] ");
        }
        options.append("&e]");
        source.sendMessage(options.toString());
    }

    public void printRules(CommandSource source, String title, String[] ruleList) {
        source.sendMessage("§e" + title);
        if (ruleList.length < 1) {
            source.sendMessage("§7None found :(");
            return;
        }

        for (String ruleName : ruleList) {
            ParsedRule<?> rule = SettingsManager.getParsedRule(ruleName);
            String name = rule.name;
            String def = rule.def;
            String val = rule.getValueString();

            StringBuilder str = new StringBuilder(String.format(" §7- §f%s: ", name));
            for (String option : rule.options) {
                String color = val.equalsIgnoreCase(option) ? "§e" : "§7";
                str.append(color).append(String.format("[%s] ", option));
            }
            str.append("§7(default: ").append(def).append(")");

            source.sendMessage(str.toString());
        }
    }
}