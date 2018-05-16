package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGiveaway;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Date;

/**
 * @author Leon Kappes / Lee (0.3%) Schlaubi / Michael Rittmeister (99.7%)
 * @copyright RubiconBot Dev Team 2018
 * @license GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandGiveaway extends CommandHandler {

    public CommandGiveaway() {
        super(new String[]{"giveaway"}, CommandCategory.TOOLS, new PermissionRequirements("giveaway", false, true), "Creates an automated giveaway users can take part in by reacting.", "<time> [WINNER AMOUNT] <prize>\ninfo\ncancel");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        if (args.length == 0)
            return createHelpMessage();
        RubiconGiveaway giveaway = new RubiconGiveaway(invocation.getAuthor());
        switch (args[0]) {
            case "cancel":
                if (!giveaway.isExists())
                    return message(error());
                giveaway.cancel();
                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.giveaway.cancelled.title"), invocation.translate("command.giveaway.cancelled.description"))));
                break;
            case "info":
                if (!giveaway.isExists())
                    return message(error());
                TextChannel channel = invocation.getGuild().getTextChannelById(giveaway.getChannelId());
                if (channel == null) giveaway.cancel();

            default:
                if (args.length < 1)
                    return message(error());
                Date expiry = StringUtil.parseDate(args[0]);
                if (expiry == null)
                    return message(error(invocation.translate("general.punishment.invalidnumber.title"), invocation.translate("general.punishment.invalidnumber.description")));
                int winnerCount = 1;
                String price = invocation.getArgsString().replace(args[0], "");
                if(args.length > 2) {
                    try {
                        winnerCount = Integer.parseInt(args[1]);
                        price = price.replace(args[1], "");
                    } catch (NumberFormatException e) {
                        return message(error(invocation.translate("command.mute.invalidnumber.title"), invocation.translate("command.mute.invalidnumber.description")));
                    }
                }
                giveaway = new RubiconGiveaway(invocation.getAuthor(), expiry, price, invocation.getTextChannel(), winnerCount);
                break;
        }
        return null;
    }


    public static EmbedBuilder formatGiveaway(RubiconGiveaway giveaway) {
        EmbedBuilder emb = new EmbedBuilder()
                .setColor(Colors.COLOR_PRIMARY)
                .setDescription(String.format(TranslationUtil.translate(giveaway.getAuthor(), "giveaway.description"), giveaway.getPrize(), giveaway.getWinnerCount()));
        if(giveaway.getUsers().isEmpty())
                emb.setFooter(String.format(TranslationUtil.translate(giveaway.getAuthor(), "giveaway.footer.nobody"), DateUtil.formatDate(giveaway.getExpirationDate(), TranslationUtil.translate(giveaway.getAuthor(), "date.format"))), null);
        else if(giveaway.getUsers().size() == 1)
            emb.setFooter(String.format(TranslationUtil.translate(giveaway.getAuthor(), "giveaway.footer.user"), DateUtil.formatDate(giveaway.getExpirationDate(), TranslationUtil.translate(giveaway.getAuthor(), "date.format"))), null);
        else
            emb.setFooter(String.format(TranslationUtil.translate(giveaway.getAuthor(), "giveaway.footer.users"), giveaway.getUsers().size(), DateUtil.formatDate(giveaway.getExpirationDate(), TranslationUtil.translate(giveaway.getAuthor(), "date.format"))), null);
        return emb;
    }
}