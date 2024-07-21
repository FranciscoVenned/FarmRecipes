package com.venned.farmrecipes.api;

import com.venned.farmrecipes.manager.DayLimitManager;
import org.bukkit.entity.Player;

public class FarmRecipesAPI {

    private static DayLimitManager dayLimitManager;

    public static void initialize(DayLimitManager manager) {
        dayLimitManager = manager;
    }

    public static int getCurrentDay() {
        return dayLimitManager.getCurrentDay();
    }

    public static int getCurrentLimit() {
        return dayLimitManager.getCurrentLimit();
    }

    public static int getCurrentAmount(Player player) {
        return dayLimitManager.getCurrentAmount(player);
    }

    public static int getCurrentAmountMax(Player player) {
        return dayLimitManager.getCurrentAmountMax(player);
    }
}
