package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandAscii extends CommandHandler {

    public CommandAscii() {
        super(new String[]{"ascii", "asciitext"}, CommandCategory.FUN, new PermissionRequirements("ascii", false, true), "Convert text to ASCII art", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if(invocation.getArgs().length<1)
            return createHelpMessage(invocation);
        Request req = new Request.Builder()
                .url("http://artii.herokuapp.com/make?text=" + invocation.getArgsString())
                .build();
        Response response;
        try {
            response = new OkHttpClient().newCall(req).execute();
            String body = response.body().string();
            if (body.toCharArray().length > 1990)
                return createHelpMessage(invocation);
            SafeMessage.sendMessage(invocation.getTextChannel(), "```fix\n" + body + "```");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            return message(EmbedUtil.error(invocation.translate("command.money.give.selferror.title"), invocation.translate("command.ascii.error")));
        }
        return null;
    }

}