/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */
package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the 'clear' command.
 *
 * @author Yannick Seeger / ForYaSee
 */
public class CommandClear extends CommandHandler {

    public CommandClear() {
        super(new String[]{"clear", "purge"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.clear"), "Clear the chat.", "<amount of messages> [@User]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length == 0) {
            return createHelpMessage();
        }

        if (!StringUtil.isNumeric(parsedCommandInvocation.args[0])) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "Parameter must be numeric."));
        }

        int messageAmount = Integer.parseInt(parsedCommandInvocation.args[0]);
        User user = (parsedCommandInvocation.invocationMessage.getMentionedUsers().size() == 1) ? parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0) : null;
        if (messageAmount > 100) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "I can't delete more than 100 messages."));
        }

        if (messageAmount < 2) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "I can't delete less than 2 messages."));
        }

        List<Message> messagesToDelete;
        messagesToDelete = parsedCommandInvocation.invocationMessage.getTextChannel().getHistory().retrievePast(messageAmount).complete();
        messagesToDelete = messagesToDelete.stream().filter(message -> !message.getCreationTime().isBefore(OffsetDateTime.now().minusWeeks(2))).collect(Collectors.toList());
        if(user != null)
            messagesToDelete = messagesToDelete.stream().filter(message -> message.getAuthor() == user).collect(Collectors.toList());
        int deletedMessagesSize = messagesToDelete.size();
        parsedCommandInvocation.invocationMessage.getTextChannel().deleteMessages(messagesToDelete).complete();
        return EmbedUtil.message(EmbedUtil.success("Cleared channel!", "Successfully cleared `" + deletedMessagesSize + "` messages"));
    }
}
