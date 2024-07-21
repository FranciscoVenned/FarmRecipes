package com.venned.farmrecipes.util;

import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ChatUtil
{
    public static NumberFormat getNumberFormat(final Locale inLocale) {
        return NumberFormat.getInstance(inLocale);
    }

    public static String parseEnumString(String input, final String... replace) {
        for (int i = 0; i < replace.length; i += 2) {
            input = input.replace(replace[i], replace[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String parseString(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> parseList(final List<String> input, final String... replace) {
        final List<String> newList = new ArrayList<>();
        final int[] i = {0};
        input.forEach(string -> {
            for (i[0] = 0; i[0] < replace.length; i[0] += 2) {
                string = string.replace(replace[i[0]], replace[i[0] + 1]);
            }
            newList.add(parseString(string));
        });
        return newList;
    }

    public static List<String> parseList(final List<String> input) {
        return input.stream()
                .map(ChatUtil::parseString)
                .collect(Collectors.toList());
    }

    public static String parseEnumString(final String input) {
        final StringJoiner stringJoiner = new StringJoiner(" ");
        final String[] split;
        final String[] args = split = input.split("_");
        for (final String word : split) {
            stringJoiner.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
        }
        return stringJoiner.toString();
    }

    public static String fixPluralName(final String input) {
        if (input == null || input.isEmpty()) {
            return "";  // O cualquier valor por defecto que tenga sentido en tu contexto
        }

        final String vowels = "aeiouAEIOU";
        final char lastChar = input.charAt(input.length() - 1);
        if (vowels.indexOf(lastChar) != -1) {
            return input.substring(0, input.length() - 1) + "es";
        }
        return input + "s";
    }
}
