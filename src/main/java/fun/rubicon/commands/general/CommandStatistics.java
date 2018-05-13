package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandStatistics extends CommandHandler {

    public CommandStatistics() {
        super(new String[]{"statistics", "stats"}, CommandCategory.GENERAL, new PermissionRequirements("statistics", false, true), "Shows some Rubicon statistics.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Colors.COLOR_SECONDARY);
        embedBuilder.setAuthor(invocation.translate("command.stats.title"), null, invocation.getSelfMember().getUser().getAvatarUrl());
        embedBuilder.addField(invocation.translate("command.stats.shards"), RubiconBot.getMaximumShardCount() + " Total", true);
        embedBuilder.addField(invocation.translate("command.stats.guilds"), String.valueOf(RubiconBot.getShardManager().getGuilds().size()), true);
        embedBuilder.addField(invocation.translate("command.stats.users"), String.valueOf(RubiconBot.getShardManager().getUsers().size()), true);
        embedBuilder.addField(invocation.translate("command.stats.roles"), String.valueOf(RubiconBot.getShardManager().getRoles().size()), true);
        embedBuilder.addField(invocation.translate("command.stats.textchannels"), String.valueOf(RubiconBot.getShardManager().getTextChannels().size()), true);
        embedBuilder.addField(invocation.translate("command.stats.voicechannels"), String.valueOf(RubiconBot.getShardManager().getVoiceChannels().size()), true);
        embedBuilder.addField(invocation.translate("command.stats.categories"), String.valueOf(RubiconBot.getShardManager().getCategories().size()), true);
        embedBuilder.addField(invocation.translate("command.stats.emotes"), String.valueOf(RubiconBot.getShardManager().getEmotes().size()), true);
        embedBuilder.addField(invocation.translate("command.stats.music"), String.valueOf(RubiconBot.getGuildMusicPlayerManager().getPlayerStorage().size()), true);
        embedBuilder.addBlankField(true);
        return EmbedUtil.message(embedBuilder);
    }
}
