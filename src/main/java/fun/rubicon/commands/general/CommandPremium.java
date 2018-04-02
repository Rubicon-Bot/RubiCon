package fun.rubicon.commands.general;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.message;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandPremium extends CommandHandler {

    public CommandPremium() {
        super(new String[]{"premium"}, CommandCategory.GENERAL, new PermissionRequirements("premium", false, true), "See your premium state or buy premium.","| Shows current premium state\n"+
        "buy | Buy premium");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        RubiconUser author = new RubiconUser(invocation.getAuthor());

        if(args.length == 0){
            EmbedBuilder em = new EmbedBuilder();
                    em.setAuthor(
                            invocation.getAuthor().getName(), null, invocation.getAuthor().getAvatarUrl()
                    );
                    em.setColor(
                            Colors.COLOR_SECONDARY
                    );
            if ((author.isPremium())) {
                em.setDescription("Premium is activated until " + author.formatExpiryDate());
            } else {
                em.setDescription("No premium. \n\nBuy premium with `rc!premium buy` for 500k rubys.");
            }
           return message(em);
        }else if (args[0].equalsIgnoreCase("buy")){
            if ((author.isPremium())) {
                return message(EmbedUtil.info("Already activated!", "You already have premium until " + author.getPremiumExpiryDate()));
            }
            int userMoney = author.getMoney();
        }
    }

}