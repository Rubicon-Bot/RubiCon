/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.commands.admin.CommandAutochannel;
import fun.rubicon.commands.admin.CommandGiveaway;
import fun.rubicon.commands.admin.CommandPermission;
import fun.rubicon.commands.fun.CommandRoulette;
import fun.rubicon.commands.general.CommandMusic;
import fun.rubicon.commands.general.CommandStatistics;

/**
 * Old command registration script.
 *
 * @author Yannick Seeger / ForYaSee
 * @see fun.rubicon.RubiconBot
 * @deprecated Register commands in RubiconBot.registerCommandHandlers() instead.
 */
@Deprecated
public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
        CommandHandler.addCommand(new CommandPermission("permission", CommandCategory.ADMIN).addAliases("permission", "perm", "perms"));
        CommandHandler.addCommand(new CommandGiveaway("giveaway", CommandCategory.ADMIN).addAliases("g"));
        CommandHandler.addCommand(new CommandAutochannel("autochannel", CommandCategory.ADMIN).addAliases("ac", "autoc"));
        CommandHandler.addCommand(new CommandRoulette("roulette", CommandCategory.BOT_OWNER).addAliases("roulete", "rulette", "roullete"));
        CommandHandler.addCommand(new CommandStatistics("statistics", CommandCategory.GENERAL).addAliases("stats"));
        CommandHandler.addCommand(new CommandMusic("music", CommandCategory.GENERAL).addAliases("m"));
    }
}
