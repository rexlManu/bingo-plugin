package de.rexlmanu.bingo.shared.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Message {

    public static final Component PREFIX = Component
            .text("Bingo")
            .color(TextColor.fromCSSHexString("#10B981"))
            .append(Component.text("∙ ").color(TextColor.fromCSSHexString("#334155")));

    public static final TextColor COLOR = TextColor.fromCSSHexString("#E2E8F0");

}
