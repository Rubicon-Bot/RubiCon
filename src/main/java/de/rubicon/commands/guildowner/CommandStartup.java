package de.rubicon.commands.guildowner;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import de.rubicon.core.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 17.11.2017 21:06
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
 */
public class CommandStartup extends Command{
    public CommandStartup(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        //Permission
        Guild g = e.getGuild();
        if(!Main.getMySQL().ifGuildExits(g)) {
            Main.getMySQL().createGuildServer(g);
            System.out.println("[Amme]System started on: " + g.getName());
            e.getChannel().sendMessage("System Online!").queue();
        }else {
            Main.getMySQL().createGuildServer(g);
            e.getChannel().sendMessage("System Reset!").queue();
        }

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}