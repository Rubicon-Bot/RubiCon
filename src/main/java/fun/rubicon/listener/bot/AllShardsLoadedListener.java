package fun.rubicon.listener.bot;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.tools.CommandYouTube;
import fun.rubicon.core.entities.RubiconGiveaway;
import fun.rubicon.core.entities.RubiconRemind;
import fun.rubicon.listener.events.RubiconEventAdapter;
import fun.rubicon.util.BotListHandler;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class AllShardsLoadedListener extends RubiconEventAdapter {

    @Override
    public void onAllShardsLoaded(AllShardsLoadedEvent event) {
        RubiconBot.setAllShardsInitialised(true);
        //Load all punishments (bans & mutes)
        RubiconBot.getPunishmentManager().loadPunishments();
        //Post Guild Stats
        BotListHandler.postStats(false);
        //Load all polls
        RubiconBot.getPollManager().loadPolls();
        //Load all YouTube Events
        new CommandYouTube.YouTubeChecker(RubiconBot.getShardManager().getGuilds());
        //Load verification cache
        RubiconBot.getVerificationLoader().loadVerificationCache();
        //Load all reminders
        RubiconRemind.loadReminders();
        //Load all Giveaways
        RubiconGiveaway.loadGiveaways();
    }
}
