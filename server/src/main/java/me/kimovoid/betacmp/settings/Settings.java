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

	// TODO: Consider extending this to buttons and pressure plates.
	@Rule(
		desc = "Disables the random ticking of redstone torches",
		category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
	)
	public static boolean disableRedstoneRandomTick = false;

	@Rule(
			desc = "Enables instant execution of scheduled ticks for liquids",
			category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
	)
	public static boolean liquidInstantTick = false;

	// TODO: Consider extending this to buttons and pressure plates.
	@Rule(
		desc = "Enables instant execution of scheduled ticks for repeaters and redstone torches",
		category = {RuleCategory.CREATIVE, RuleCategory.FEATURE}
	)
	public static boolean redstoneInstantTick = false;

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
