package fun.rubicon.listener;

import fun.rubicon.core.Main;
import fun.rubicon.util.Cooldown;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class Leveler extends ListenerAdapter{


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()){ return;}
        if (Cooldown.has(event.getAuthor().getId())){ return;}
        MySQL LVL = Main.getMySQL();
        if (LVL.ifUserExist(event.getAuthor())) {
            //Point System
            int current = Integer.parseInt(LVL.getUserValue(event.getAuthor(), "points"));
            int randomNumber = (int) ((Math.random() * 10) + 10);
            String point = String.valueOf(current + randomNumber);
            int points = current + randomNumber;
            LVL.updateUserValue(event.getAuthor(), "points", point);
            //Cooldown
            Cooldown.add(event.getAuthor().getId());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Cooldown.remove(event.getAuthor().getId());
                }
            }, 30000);

            String lvlnow = LVL.getUserValue(event.getAuthor(), "level");
            int dann = Integer.parseInt(lvlnow);
            int req = dann * 30;

            if (points > req) {
                dann++;
                String fina = String.valueOf(dann);
                LVL.updateUserValue(event.getAuthor(), "level", fina);
                LVL.updateUserValue(event.getAuthor(), "points", "0");
                String l = (LVL.getUserValue(event.getAuthor(), "level"));
                int foo = Integer.parseInt(l);
                //Level Up
                Message msg = event.getChannel().sendMessage(new EmbedBuilder()
                        .setDescription(event.getAuthor().getAsMention() + " ,wow you got a Level up to Level **" + LVL.getUserValue(event.getAuthor(), "level") + "** !")
                        .build()
                ).complete();
                Random r = new Random();
                int Low = 10;
                int High = 100;
                int Result = r.nextInt(High - Low) + Low;
                int ran = Math.round(Result);
                int foa = foo * 2 / 3 + ran;
                String m = String.valueOf(foa);
                LVL.updateUserValue(event.getAuthor(), "money", m);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        msg.delete().queue();
                    }
                }, 3000);

            }
        }else LVL.createUser(event.getAuthor());
    }
}
