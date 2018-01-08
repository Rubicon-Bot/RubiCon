package fun.rubicon.commands.fun;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.GuildMusicManager;
import fun.rubicon.core.music.MusicSearchResult;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.permission.PermissionTarget;
import fun.rubicon.sql.GuildMusicSQL;
import fun.rubicon.sql.UserMusicSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandMusic extends CommandHandler {

    private static List<MusicSearchResult> musicChoose = new ArrayList<>();

    private GuildMusicSQL guildMusicSQL;
    private UserMusicSQL userMusicSQL;
    private Guild guild;
    private String[] args;
    private fun.rubicon.permission.UserPermissions userPermissions;
    private CommandManager.ParsedCommandInvocation parsedCommandInvocation;

    private final int PLAYLIST_MAXIMUM_DEFAULT = 1;
    private final int PLAYLIST_MAXIMUM_VIP = 5;
    private final int QUEUE_MAXIMUM = 50;
    private final int DEFAULT_VOLUME = 40;
    private final int SKIP_MAXIMUM = 10;

    private final AudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers;
    private final Map<String, fun.rubicon.permission.PermissionRequirements> permissionRequirementsMap;

    //TODO Parameter Usage
    public CommandMusic() {
        super(new String[]{
                "play",
                "skip",
                "join",
                "leave",
                "shuffle"
        }, CommandCategory.MUSIC, new PermissionRequirements(PermissionLevel.EVERYONE, "command.music"), "Chill with your friends and listen to music.", "");

        permissionRequirementsMap = new HashMap<>();
        permissionRequirementsMap.put("play", new PermissionRequirements(PermissionLevel.EVERYONE, "command.play"));
        permissionRequirementsMap.put("skip", new PermissionRequirements(PermissionLevel.EVERYONE, "command.skip"));
        permissionRequirementsMap.put("join", new PermissionRequirements(PermissionLevel.EVERYONE, "command.join"));
        permissionRequirementsMap.put("leave", new PermissionRequirements(PermissionLevel.EVERYONE, "command.leave"));
        permissionRequirementsMap.put("shuffle", new PermissionRequirements(PermissionLevel.EVERYONE, "command.shuffle"));

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        //playerManager.registerSourceManager(new SoundCloudAudioSourceManager()); //TODO Soundcloud support?

        musicManagers = new HashMap<>();
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        this.parsedCommandInvocation = parsedCommandInvocation;
        this.guild = parsedCommandInvocation.invocationMessage.getGuild();
        this.args = parsedCommandInvocation.args;
        this.userMusicSQL = new UserMusicSQL(parsedCommandInvocation.invocationMessage.getAuthor());
        this.guildMusicSQL = new GuildMusicSQL(guild);
        this.userPermissions = new fun.rubicon.permission.UserPermissions(parsedCommandInvocation.invocationMessage.getAuthor().getIdLong(), parsedCommandInvocation.invocationMessage.getGuild().getIdLong());
        switch (parsedCommandInvocation.invocationCommand) {
            case "join":
                if (permissionRequirementsMap.get("join").coveredBy(userPermissions))
                    return joinInVoiceChannel();
            case "leave":
                if (permissionRequirementsMap.get("leave").coveredBy(userPermissions))
                    return leaveVoiceChannel();
            case "shuffle":
                if (permissionRequirementsMap.get("shuffle").coveredBy(userPermissions))
                    return handleShuffle();
            case "play":
                if (permissionRequirementsMap.get("play").coveredBy(userPermissions))
                    return playMusic();
            case "skip":
                if (permissionRequirementsMap.get("skip").coveredBy(userPermissions))
                    return handleSkip();
        }
        return createHelpMessage();
    }

    private Message joinInVoiceChannel() {
        if (!isMemberInVoiceChannel())
            return message(error("Error!", "To use this command you have to be in a voice channel."));
        VoiceChannel voiceChannel;
        if (isChannelLockActivated()) {
            voiceChannel = getLockedChannel();
            if (voiceChannel == null)
                return message(error("Error!", "Predefined channel doesn't exist."));
        } else {
            voiceChannel = parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel();
            if (isBotInVoiceChannel()) {
                if (getBotsVoiceChannel() == voiceChannel)
                    return message(error("Error!", "Bot is already in your voice channel."));
            }
        }
        guild.getAudioManager().setSendingHandler(getMusicManager(guild).getSendHandler());
        try {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                return message(error("Error!", "I need the VOICE_CONNECT permissions to join a channel."));
            }
        }
        guild.getAudioManager().setSelfDeafened(true);
        return EmbedUtil.message(success("Joined channel", "Joined `" + voiceChannel.getName() + "`"));
    }

    private Message leaveVoiceChannel() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));

        guild.getAudioManager().setSendingHandler(null);
        guild.getAudioManager().closeAudioConnection();
        getCurrentMusicManager().getPlayer().destroy();
        return EmbedUtil.message(success("Channel Left", "Left the channel.").setColor(Colors.COLOR_NOT_IMPLEMENTED));
    }

    private Message playMusic() {
        if (!isMemberInVoiceChannel())
            return message(error("Error!", "To use this command you have to be in a voice channel."));
        if (!isBotInVoiceChannel())
            joinInVoiceChannel();
        AudioPlayer player = getCurrentMusicManager().getPlayer();
        if (player.isPaused()) {
            player.setPaused(false);
        }
        loadSong();
        return null;
    }

    private void loadSong() {
        TextChannel textChannel = parsedCommandInvocation.invocationMessage.getTextChannel();
        boolean isURL = false;
        StringBuilder searchParam = new StringBuilder();
        for (int i = 0; i < args.length; i++)
            searchParam.append(args[i]);
        if (searchParam.toString().startsWith("http://") || searchParam.toString().startsWith("https://"))
            isURL = true;

        if (!isURL)
            searchParam.insert(0, "ytsearch: ");

        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final boolean isURLFinal = isURL;
        playerManager.loadItemOrdered(getCurrentMusicManager(), searchParam.toString(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                String trackName = audioTrack.getInfo().title;
                String trackAuthor = audioTrack.getInfo().author;
                String trackURL = audioTrack.getInfo().uri;
                boolean isStream = audioTrack.getInfo().isStream;
                long trackDuration = audioTrack.getDuration();

                getCurrentMusicManager().getScheduler().queue(audioTrack);

                embedBuilder.setAuthor("Added a new song to queue", trackURL, null);
                embedBuilder.addField("Title", trackName, true);
                embedBuilder.addField("Author", trackAuthor, true);
                embedBuilder.addField("Duration", (isStream) ? "Stream" : getTimestamp(trackDuration), false);
                embedBuilder.setColor(Colors.COLOR_PRIMARY);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();
                List<AudioTrack> playlistTracks = audioPlaylist.getTracks();
                playlistTracks = playlistTracks.stream().limit(QUEUE_MAXIMUM).collect(Collectors.toList());

                if (firstTrack == null)
                    firstTrack = playlistTracks.get(0);
                if (isURLFinal) {
                    playlistTracks.forEach(getCurrentMusicManager().getScheduler()::queue);

                    embedBuilder.setTitle("Added playlist to queue");
                    embedBuilder.setDescription("Added `" + playlistTracks.size() + "` songs from `" + audioPlaylist.getName() + "` to queue.\n" +
                            "\n" +
                            "**Now playing** `" + firstTrack.getInfo().title + "`");
                    embedBuilder.addField("Author", firstTrack.getInfo().author, true);
                    embedBuilder.addField("Duration", (firstTrack.getInfo().isStream) ? "Stream" : getTimestamp(firstTrack.getDuration()), false);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                } else {
                    MusicSearchResult musicSearchResult = new MusicSearchResult(parsedCommandInvocation.invocationMessage.getAuthor(), guild, getCurrentMusicManager());
                    audioPlaylist.getTracks().stream().limit(5).collect(Collectors.toList()).forEach(track -> {
                        try {
                            musicSearchResult.addTrack(track);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    musicSearchResult.setMessage(textChannel.sendMessage(musicSearchResult.generateEmbed().build()).complete());
                    musicChoose.add(musicSearchResult);
                }
            }

            @Override
            public void noMatches() {
                embedBuilder.setTitle("No matches!");
                embedBuilder.setDescription("There are no matches.");
                embedBuilder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                embedBuilder.setTitle(":warning: Error!");
                embedBuilder.setDescription("Could not play this song: " + e.getMessage());
                embedBuilder.setColor(Colors.COLOR_ERROR);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }
        });
    }

    public Message handleShuffle() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));

        getCurrentMusicManager().getScheduler().shuffle();
        return message(success("Shuffled!", "Successfully shuffled queue."));
    }

    public Message handleSkip() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        int amount = 1;
        if (parsedCommandInvocation.args.length == 1) {
            if (StringUtil.isNumeric(parsedCommandInvocation.args[0])) {
                amount = Integer.parseInt(parsedCommandInvocation.args[0]);
            }
        }
        if (amount > SKIP_MAXIMUM)
            return message(error("Error!", "You can only skip " + SKIP_MAXIMUM + " tracks."));
        skipTrack(amount);
        return message(success("Skipped!", "Successfully skipped " + amount + " tracks."));
    }

    public void skipTrack(int x) {
        for (int i = 0; i < x; i++) {
            getCurrentMusicManager().getScheduler().nextTrack();
        }
    }

    public static void handleTrackChoose(MessageReceivedEvent event) {
        List<MusicSearchResult> storage = musicChoose.stream().filter(musicSearchResult -> musicSearchResult.getUser() == event.getAuthor()).collect(Collectors.toList());
        if (storage.size() == 0) {
            return;
        }
        String respone = event.getMessage().getContentDisplay();
        if (!StringUtil.isNumeric(respone)) {
            return;
        }
        int ans = Integer.parseInt(respone);
        if (ans < 1 || ans > 5) {
            return;
        }
        ans--;
        AudioTrack track = storage.get(0).getTrack(ans);
        storage.get(0).getMusicManager().getScheduler().queue(track);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Added a new song to queue", track.getInfo().uri, null);
        embedBuilder.addField("Title", track.getInfo().title, true);
        embedBuilder.addField("Author", track.getInfo().author, true);
        embedBuilder.addField("Duration", (track.getInfo().isStream) ? "Stream" : getTimestamp(track.getDuration()), false);
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        event.getTextChannel().sendMessage(embedBuilder.build()).queue();
        storage.get(0).getMessage().delete().queue();
        musicChoose.remove(storage.get(0));
        event.getMessage().delete().queue();
    }

    private boolean isMemberInVoiceChannel() {
        if (parsedCommandInvocation.invocationMessage.getMember().getVoiceState().inVoiceChannel())
            return true;
        return false;
    }

    private boolean isBotInVoiceChannel() {
        if (guild.getSelfMember().getVoiceState().inVoiceChannel())
            return true;
        return false;
    }

    private VoiceChannel getBotsVoiceChannel() {
        if (!isBotInVoiceChannel())
            return null;
        return guild.getSelfMember().getVoiceState().getChannel();
    }

    private boolean isDJ(Member member) {
        Role role = getDJRole();
        if (role == null)
            return true; //TODO or false?
        if (member.getRoles().contains(role))
            return true;
        return false;
    }

    private boolean isDJEnabled() {
        if (guildMusicSQL.get("dj").equalsIgnoreCase("false"))
            return false;
        return true;
    }

    private boolean isChannelLockActivated() {
        if (guildMusicSQL.get("locked_channel").equalsIgnoreCase("false"))
            return false;
        return true;
    }

    private VoiceChannel getLockedChannel() {
        if (!isChannelLockActivated())
            return null;
        String entry = guildMusicSQL.get("locked_channel");
        try {
            VoiceChannel channel = RubiconBot.getJDA().getVoiceChannelById(entry);
            return channel;
        } catch (NullPointerException ignored) {

        }
        return null;
    }

    private Role getDJRole() {
        if (!isDJEnabled())
            return null;
        String entry = guildMusicSQL.get("guildid");
        try {
            Role role = RubiconBot.getJDA().getRoleById(entry);
            return role;
        } catch (NullPointerException ignored) {

        }
        return null;
    }

    private GuildMusicManager getMusicManager(Guild guild) {
        String guildId = guild.getId();
        GuildMusicManager musicManager = musicManagers.get(guildId);
        if (musicManager == null) {
            synchronized (musicManagers) {
                musicManager = musicManagers.get(guildId);
                if (musicManager == null) {
                    musicManager = new GuildMusicManager(playerManager);
                    musicManager.getPlayer().setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, musicManager);
                }
            }
        }
        return musicManager;
    }

    private GuildMusicManager getCurrentMusicManager() {
        return getMusicManager(parsedCommandInvocation.invocationMessage.getGuild());
    }

    private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}
