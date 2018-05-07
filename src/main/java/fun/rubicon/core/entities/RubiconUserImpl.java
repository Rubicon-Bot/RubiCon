/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.rethink.RethinkHelper;
import fun.rubicon.rethink.Rethink;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yannick Seeger / ForYaSee
 */
public abstract class RubiconUserImpl extends RethinkHelper {

    protected User user;
    private Rethink rethink;
    private final Filter dbUser;

    RubiconUserImpl(User user) {
        this.user = user;
        this.rethink = RubiconBot.getRethink();
        dbUser = rethink.db.table("users").filter(rethink.rethinkDB.hashMap("userId", user.getId()));
        createIfNotExist();
    }

    public User getUser() {
        return user;
    }

    public void setBio(String bio) {
        dbUser.update(rethink.rethinkDB.hashMap("bio", bio)).run(rethink.getConnection());
    }

    public String getBio() {
        Map map = (Map) retrieve().toList().get(0);
        return (String) map.get("bio");
    }

    public void setMoney(long amount) {
        dbUser.update(rethink.rethinkDB.hashMap("money", amount)).run(rethink.getConnection());
    }

    public long getMoney() {
        Map map = (Map) retrieve().toList().get(0);
        return (long) map.get("money");
    }

    public void addMoney(long amount) {
        setMoney(getMoney() + amount);
    }

    public void removeMoney(long amount) {
        setMoney(getMoney() - amount);
    }

    public void setPremium(long time) {
        dbUser.update(rethink.rethinkDB.hashMap("premium", time)).run(rethink.getConnection());
    }

    public long getPremiumRaw() {
        Map map = (Map) retrieve().toList().get(0);
        return (long) map.get("premium");
   }

    public boolean isPremium() {
        if (getPremiumRaw() > new Date().getTime())
            return true;
        else
            dbUser.update(rethink.rethinkDB.hashMap("premium", 0)).run(rethink.getConnection());
        return false;
    }

    public void setLanguage(String languageKey) {
        dbUser.update(rethink.rethinkDB.hashMap("language", languageKey)).run(rethink.getConnection());
    }

    public String getLanguage() {
        Map map = (Map) retrieve().toList().get(0);
        return (String) map.get("language");
    }


    public void setAFKState(String afk) {
        dbUser.update(rethink.rethinkDB.hashMap("afk", afk)).run(rethink.getConnection());
    }

    public String getAFKState() {
        Map map = (Map) retrieve().toList().get(0);
        return (String) map.get("afk");
    }

    public boolean isAFK() {
        try {
            return !getAFKState().equals("");
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Date getPremiumExpiryDate() {
        if (!this.isPremium())
            return null;
        return new Date(this.getPremiumRaw());
    }

    public String formatExpiryDate() {
        if (!this.isPremium())
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(TranslationUtil.translate(user, "date.format"));
        return sdf.format(this.getPremiumExpiryDate());
    }

    public void unban(Guild guild) {
        rethink.db.table("punishments").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId()).with("type", "ban")).delete().run(rethink.getConnection());

        if (guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            guild.getController().unban(user).queue();
        } else
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to unban user `" + user.getName() + "`! Please give Rubicon `BAN_MEMBERS` permission in order to use the unban command").queue();
    }

    public void saveMusicPlaylist(List<String> links, String name) {
        HashMap<String, List<String>> pl = getMusicPlaylists();
        if(pl == null)
            pl = new HashMap<>();
        pl.put(name, links);
        saveMusicPlaylist(pl);
    }

    public void saveMusicPlaylist(HashMap<String, List<String>> list) {
        if(list == null)
            return;
        dbUser.update(rethink.rethinkDB.hashMap("playlists", null)).run(rethink.getConnection());
        dbUser.update(rethink.rethinkDB.hashMap("playlists", list)).run(rethink.getConnection());
    }

    public HashMap<String, List<String>> getMusicPlaylists() {
        Cursor cursor = retrieve();
        HashMap<?, ?> root = (HashMap<?, ?>) cursor.toList().get(0);
        return (HashMap<String, List<String>>) root.get("playlists");
    }

    private boolean exist() {
        return exist(retrieve());
    }

    private void createIfNotExist() {
        if (exist())
            return;
        rethink.db.table("users").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("userId", user.getId()))).run(rethink.getConnection());
    }

    public void delete() {
        dbUser.delete().run(rethink.getConnection());
    }

    private Cursor retrieve() {
        return dbUser.run(rethink.getConnection());
    }

    public static RubiconUser fromUser(User user) {
        return new RubiconUser(user);
    }
}