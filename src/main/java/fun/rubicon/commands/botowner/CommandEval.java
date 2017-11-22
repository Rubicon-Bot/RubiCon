package fun.rubicon.commands.botowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.botowner
 */
public class CommandEval extends Command{
    public CommandEval(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if (e.getAuthor().getId().equals("264048760580079616")) return;

        String[] par = String.join(" ", args).split("\\s+", 2);

        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        try {
            se.eval("var imports = new JavaImporter(" +
                    "java.nio.file," +
                    "Packages.net.dv8tion.jda.core.Permission," +
                    "Packages.net.dv8tion.jda.core," +
                    "java.lang," +
                    "java.lang.management," +
                    "java.text," +
                    "java.sql," +
                    "java.util," +
                    "java.time," +
                    "Packages.com.sun.management" +
                    ");");
        } catch (ScriptException er) {
            er.printStackTrace();
        }
        se.put("e", e);
        se.put("jda", e.getJDA());
        se.put("guild", e.getGuild());
        se.put("channel", e.getChannel());
        se.put("message", e.getMessage());
        se.put("author", e.getAuthor());

        String modified_msg = String.join(" ", args)
                .replace("getToken", "getTextChannelById(channel.getId()).sendMessage(\"UnsupportedOperationException(\\\"Nice try m8!\\\")\").queue").replace("System.exit", "getTextChannelById(channel.getId()).sendMessage(\"UnsupportedOperationException(\\\"Nice try m8!\\\")\").queue").replace("shutdown", "getTextChannelById(channel.getId()).sendMessage(\"UnsupportedOperationException(\\\"Nice try m8!\\\")\").queue").replace("Runtime", "getTextChannelById(channel.getId()).sendMessage(\"UnsupportedOperationException(\\\"Nice try m8!\\\")\").queue").replace("leave", "getTextChannelById(channel.getId()).sendMessage(\\\"UnsupportedOperationException(\\\\\\\"Nice try m8!\\\\\\\")\\\").queue").replace("kick", "SHUT UP SCHLAUBI").replace("while", "FUCK YOU!").replace("getAsMention()", "getAsShutUp()").replace("Thread", "EINSCHEIß");
        //        .replace("ProcessBuilder","throw new UnsupportedOperationException(\"Locked\")");

        //    modified_msg = modified_msg.replaceAll("#", "().");

        try {
            Object out = se.eval(
                    "{" +
                            "with (imports) {" +
                            modified_msg +
                            "}" +
                            "};");

            if (out == null) {
                out = "Your action..";
            }

            e.getChannel().sendMessage(new StringBuilder().append("```Java\n").append(modified_msg)
                    .append("```Evaluated successfully:").toString()).queue();
            new MessageBuilder().appendCodeBlock(out.toString(), "Java").buildAll(MessageBuilder.SplitPolicy.NEWLINE, MessageBuilder.SplitPolicy.SPACE, MessageBuilder.SplitPolicy.ANYWHERE).forEach(message -> e.getTextChannel().sendMessage(message).queue());
        } catch (ScriptException er) {
            e.getTextChannel().sendMessage(new StringBuilder().append("```Java\n").append(modified_msg)
                    .append("``` ```Java\nAn exception was thrown:" + er.toString() + "```").toString()).queue();
        }
    }

    @Override
    public String getDescription() {
        return "Simple evaluation command!";
    }

    @Override
    public String getUsage() {
        return "eval <CODE>";
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
