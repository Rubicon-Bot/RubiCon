package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandUptime extends CommandHandler {

    private String getTime(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    private String getTimeDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays + " d, " + parseTimeNumbs(diffHours) + " h, " + parseTimeNumbs(diffMinutes) + " min, " + parseTimeNumbs(diffSeconds) + " sec";
    }

    private String parseTimeNumbs(long time) {
        String timeString = time + "";
        if (timeString.length() < 2)
            timeString = "0" + time;
        return timeString;
    }


    public CommandUptime() {
        super(new String[]{"uptime"}, CommandCategory.GENERAL, new PermissionRequirements("uptime", false, true), "Get the Uptime of the Bot", "", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        invocation.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(255, 71, 0))
                        .setDescription(":alarm_clock:   **UPTIME**")
                        .addField(invocation.translate("command.uptime.restart"), getTime(Info.lastRestart, "dd.MM.yyyy - HH:mm:ss (z)"), false)
                        .addField(invocation.translate("command.uptime.online"), getTimeDiff(new Date(), Info.lastRestart), false)
                        .build()
        ).queue();
        return null;
    }
}
