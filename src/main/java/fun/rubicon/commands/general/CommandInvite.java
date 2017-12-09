package fun.rubicon.commands.general;

<<<<<<< HEAD
=======
import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
>>>>>>> master
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.general
 */

public class CommandInvite extends CommandHandler {
    public CommandInvite() {
<<<<<<< HEAD
        super(new String[]{"invite", "inv"}, CommandCategory.GENERAL, new PermissionRequirements(0, "command.invite"), "Gives you the invite-link of the bot.", "invite");
=======
        super(new String[]{},CommandCategory.GENERAL,new PermissionRequirements(0,"command.invite"),"Gives you the Invite-Link of the bot.","invite");
>>>>>>> master
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
<<<<<<< HEAD
=======
        //Create EmbedBuilder
>>>>>>> master
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_SECONDARY);
<<<<<<< HEAD
        builder.setAuthor(Info.BOT_NAME + " - Invite", null, parsedCommandInvocation.invocationMessage.getJDA().getSelfUser().getAvatarUrl());
        builder.setDescription("[Invite Rubicon Bot](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=1898982486)\n" +
                "[Join Rubicon Server](https://discord.gg/UrHvXY9)");
=======
        builder.setAuthor(Info.BOT_NAME + " - Invite", null, RubiconBot.getJDA().getSelfUser().getAvatarUrl());
        builder.setDescription("[Invite Rubicon Bot](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=-1)\n" +
                "[Join Rubicon Server](http://disco.gg/rubicon)");
        //Send Message with Embed
>>>>>>> master
        return new MessageBuilder().setEmbed(builder.build()).build();
    }
}
