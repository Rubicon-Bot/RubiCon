/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
<<<<<<< HEAD
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Message;
import java.util.Arrays;
=======
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;
>>>>>>> master

import static fun.rubicon.util.EmbedUtil.*;

public class CommandMoney extends CommandHandler {
    public CommandMoney() {
<<<<<<< HEAD
        super(new String[]{"money","ruby"}, CommandCategory.GENERAL,
                new PermissionRequirements(0, "command.money"),
=======
        super(new String[]{"money", "ruby"}, CommandCategory.GENERAL,
                new PermissionRequirements(PermissionLevel.EVERYONE, "command.money"),
>>>>>>> master
                "You can donate Ruby's to someone!", "money <give | set | add | remove> <UserAsMention> <amount>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        int user1_has_money = 0;
        int user2_has_money = 0;
        int user_spend_money = 0;
<<<<<<< HEAD
        switch (parsedCommandInvocation.args[0]){
=======
        if (parsedCommandInvocation.args.length == 0) {
            return createHelpMessage();
        }
        switch (parsedCommandInvocation.args[0]) {
>>>>>>> master
            case "give":
                if (parsedCommandInvocation.args.length == 3) {
                    try {
                        user_spend_money = Integer.parseInt(parsedCommandInvocation.args[parsedCommandInvocation.args.length - 1]);
                        user1_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(parsedCommandInvocation.invocationMessage.getAuthor(), "money"));
                        user2_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money"));
                        if (user1_has_money < user_spend_money) {
                            return message(error("Not enough Money", "Sorry " + parsedCommandInvocation.invocationMessage.getAuthor().getAsMention() + ". You only have " + user1_has_money + " Ruby's!"));
                        } else {
<<<<<<< HEAD
                            if((user2_has_money+user_spend_money) <= 2147483647) {
                                RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getAuthor(), "money", String.valueOf(user1_has_money - user_spend_money));
                                RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user2_has_money + user_spend_money));
                                return message(success("Donation completed", parsedCommandInvocation.invocationMessage.getAuthor().getAsMention() + " give " + user_spend_money + " Ruby's to " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + "."));
                            }else{
                                return message(error("Money value to big!", "Money value must be smaller than "+((2147483647-user2_has_money)+1)+"!"));
=======
                            if ((user2_has_money + user_spend_money) <= 2147483647) {
                                RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getAuthor(), "money", String.valueOf(user1_has_money - user_spend_money));
                                RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user2_has_money + user_spend_money));
                                return message(success("Donation completed", parsedCommandInvocation.invocationMessage.getAuthor().getAsMention() + " give " + user_spend_money + " Ruby's to " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + "."));
                            } else {
                                return message(error("Money value to big!", "Money value must be smaller than " + ((2147483647 - user2_has_money) + 1) + "!"));
>>>>>>> master
                            }
                        }
                    } catch (NumberFormatException exception) {
                        return message(error("NaN or money value is to big!", "The money value is not a number or bigger than 2.147.483.647!"));
                    }
<<<<<<< HEAD
                }
                else {
                    return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getUsage()));
                }
            case "set":
                if(new PermissionRequirements(4, "command.money.modify").coveredBy(userPermissions)) {
=======
                } else {
                    return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getUsage()));
                }
            case "set":
                if (new PermissionRequirements(4, "command.money.modify").coveredBy(userPermissions)) {
>>>>>>> master
                    if (parsedCommandInvocation.args.length == 3) {
                        try {
                            user_spend_money = Integer.parseInt(parsedCommandInvocation.args[parsedCommandInvocation.args.length - 1]);
                            if (user_spend_money > -1) {
<<<<<<< HEAD
                                    RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user_spend_money));
                                    return message(success("Money has been set!", "Money of " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has been set to " + user_spend_money + " Ruby's."));
=======
                                RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user_spend_money));
                                return message(success("Money has been set!", "Money of " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has been set to " + user_spend_money + " Ruby's."));
>>>>>>> master
                            } else {
                                return message(error("Money value to small!", "Money value must be 0 or bigger!"));
                            }

                        } catch (NumberFormatException exception) {
                            return message(error("NaN or money value is to big!", "The money value is not a number or bigger than 2.147.483.647!"));
                        }
                    } else {
                        return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getUsage()));
                    }
<<<<<<< HEAD
                }else{
=======
                } else {
>>>>>>> master
                    return message(no_permissions());
                }
            case "add":
                int max_money = 2147483647;
                user2_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money"));
<<<<<<< HEAD
                if(new PermissionRequirements(4, "command.money.modify").coveredBy(userPermissions)) {
=======
                if (new PermissionRequirements(4, "command.money.modify").coveredBy(userPermissions)) {
>>>>>>> master
                    if (parsedCommandInvocation.args.length == 3) {
                        try {
                            user_spend_money = Integer.parseInt(parsedCommandInvocation.args[parsedCommandInvocation.args.length - 1]);
                            if (user_spend_money > 0) {
<<<<<<< HEAD
                                if((user2_has_money + user_spend_money) <= 2147483647 && (user2_has_money + user_spend_money) > 0) {
                                    if(user2_has_money == 2147483647){
                                        return message(error("Too much money!", parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention()+ " has already the money maximum!"));
                                    }else {
                                        RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user2_has_money + user_spend_money));
                                        return message(success("Money has been added!", "Money of " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has been set to " + (user2_has_money + user_spend_money) + " Ruby's."));
                                    }
                                }else{
                                    return message(error("Money value to big!", "Money value must be smaller than "+((max_money-user2_has_money)+1)+"!"));
=======
                                if ((user2_has_money + user_spend_money) <= 2147483647 && (user2_has_money + user_spend_money) > 0) {
                                    if (user2_has_money == 2147483647) {
                                        return message(error("Too much money!", parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has already the money maximum!"));
                                    } else {
                                        RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user2_has_money + user_spend_money));
                                        return message(success("Money has been added!", "Money of " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has been set to " + (user2_has_money + user_spend_money) + " Ruby's."));
                                    }
                                } else {
                                    return message(error("Money value to big!", "Money value must be smaller than " + ((max_money - user2_has_money) + 1) + "!"));
>>>>>>> master
                                }
                            } else {
                                return message(error("Money value to small!", "Money value must be bigger than 0!"));
                            }

                        } catch (NumberFormatException exception) {
                            return message(error("Not a number!", "The money value is not a number!"));
                        }
                    } else {
                        return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getUsage()));
                    }
<<<<<<< HEAD
                }else{
=======
                } else {
>>>>>>> master
                    return message(no_permissions());
                }

            case "remove":
                user2_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money"));
<<<<<<< HEAD
                if(new PermissionRequirements(4, "command.money.modify").coveredBy(userPermissions)) {
=======
                if (new PermissionRequirements(4, "command.money.modify").coveredBy(userPermissions)) {
>>>>>>> master
                    if (parsedCommandInvocation.args.length == 3) {
                        try {
                            user_spend_money = Integer.parseInt(parsedCommandInvocation.args[parsedCommandInvocation.args.length - 1]);
                            if (user_spend_money > 0) {
<<<<<<< HEAD
                                if((Integer.parseInt(RubiconBot.getMySQL().getUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money"))-user_spend_money)>=0)
                                {
                                    RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user2_has_money - user_spend_money));
                                    return message(success("Money has been removed!", "Money of " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has been set to " + (user2_has_money - user_spend_money) + " Ruby's."));
                                }else{
                                    return message(error("Money value to big!", "Money value must be smaller than "+((2147483647-user2_has_money)+1)+"!"));
=======
                                if ((Integer.parseInt(RubiconBot.getMySQL().getUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money")) - user_spend_money) >= 0) {
                                    RubiconBot.getMySQL().updateUserValue(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0), "money", String.valueOf(user2_has_money - user_spend_money));
                                    return message(success("Money has been removed!", "Money of " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getAsMention() + " has been set to " + (user2_has_money - user_spend_money) + " Ruby's."));
                                } else {
                                    return message(error("Money value to big!", "Money value must be smaller than " + ((2147483647 - user2_has_money) + 1) + "!"));
>>>>>>> master
                                }
                            } else {
                                return message(error("Money value to small!", "Money value must be bigger than 0!"));
                            }

                        } catch (NumberFormatException exception) {
                            return message(error("NaN or money value is to big!", "The money value is not a number or bigger than 2.147.483.647!"));
                        }
                    } else {
                        return message(error("Wrong count of arguments", "Three arguments are necessary!\n" + getUsage()));
                    }
<<<<<<< HEAD
                }else{
                    return message(no_permissions());
                }
                default:
                    return message(error("No valid money option", "Only give, set, add, remove are valid money options!"));
        }
    }
}
=======
                } else {
                    return message(no_permissions());
                }
            default:
                return message(error("No valid money option", "Only give, set, add, remove are valid money options!"));
        }
    }
}
>>>>>>> master
