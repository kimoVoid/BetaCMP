package me.kimovoid.betacmp.settings;

public class Settings {

    @Rule(
            desc = "Disables detector rail random ticking",
            category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
    )
    @RuleDefaults.Creative
    public static boolean disableRailTick = false;

    @Rule(
            desc = "Makes all detector rails powered by default",
            extra = "Acts similarly to redstone blocks",
            category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
    )
    @RuleDefaults.Creative
    public static boolean lazyRails = false;

    static class PositiveValidator extends Validator<Integer> {
        @Override
        boolean validate(Integer value) {
            return value > 0;
        }
    }

    static class NonNegativeValidator extends Validator<Integer> {
        @Override
        boolean validate(Integer value) {
            return value >= 0;
        }
    }
}
