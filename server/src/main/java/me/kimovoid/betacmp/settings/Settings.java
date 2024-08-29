package me.kimovoid.betacmp.settings;

public class Settings {
    @Rule(
            desc = "Disables the random ticking of detector rails",
            category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
    )
    @RuleDefaults.Creative
    public static boolean disableRailRandomTick = false;

    @Rule(
            desc = "Makes detector rails activated by default when placed by a player",
            extra = "This should be used together with `disableRailRandomTick` so that detector rails cannot" +
				"deactivate randomly",
            category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
    )
    @RuleDefaults.Creative
    public static boolean placeActivatedRails = false;

	@Rule(
		desc = "Disables the random ticking of liquids",
		category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
	)
	@RuleDefaults.Creative
	public static boolean disableLiquidRandomTick = false;

    @Rule(
            desc = "Volume limit of the fill/clone commands",
            category = RuleCategory.CREATIVE,
            options = {"32768", "250000", "1000000"},
            validator = NonNegativeValidator.class
    )
    @RuleDefaults.Creative("1000000")
    public static int fillLimit = 32768;

    @Rule(
            desc = "Determines whether clone sends block updates",
            category = RuleCategory.CREATIVE
    )
    public static boolean fillUpdates = true;

	@Rule(
			desc = "Enables instant execution of scheduled ticks for liquids",
			category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
	)
	public static boolean liquidInstantTick = false;

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
