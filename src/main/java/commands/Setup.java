package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Setup implements ICommand
{

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        String[] commandVar;

        boolean mod = true;

        int size;

        if( queue.modRole == null )
        {
            mod = false;
        }

        if( !mod && ( !msg.getAuthor().hasRole( queue.modRole ) || !msg.getAuthor().equals( queue.owner ) ) )
        {
            msg.reply("Your power is weak");
            return;
        }

        try
        {
            commandVar = args.split("\\s+", 2);
        }
        catch ( NullPointerException e )
        {
            msg.reply("you didnt enter a text ID/no arguments");
            return;
        }

        IChannel channel;

        IRole role;

        String subCommand = commandVar[0].toLowerCase();

        try
        {
            if (subCommand.equals("mod"))
            {

                role = msg.getGuild().getRoleByID( Long.parseLong(commandVar[ 1 ] ) );

                if( role == null )
                {
                    msg.reply("Invalid role");

                    return;
                }

                if( permLevel > 2)

                cfg.setProp( "mod", commandVar[ 1 ] );

                msg.reply("mod role set to " +  role);
            }

            if (subCommand.equals("queuetext"))
            {

                channel = msg.getGuild().getChannelByID( Long.parseLong(commandVar[ 1 ] ) );

                if( channel == null )
                {
                    msg.reply("Invalid text channel");

                    return;
                }

                cfg.setProp( "queuetext", commandVar[ 1 ] );

                msg.reply("queue text channel set to " +  channel);
            }

            if (subCommand.equals("sort"))
            {
                channel = msg.getGuild().getVoiceChannelByID( Long.parseLong(commandVar[ 1 ] ) );

                if( channel == null )
                {
                    msg.reply("Invalid voice channel");

                    return;
                }

                cfg.setProp( "sort", commandVar[ 1 ] );

                msg.reply("Sort voice channel set to " +  channel);
            }

            if (subCommand.equals("lobby"))
            {
                channel = msg.getGuild().getVoiceChannelByID( Long.parseLong(commandVar[ 1 ] ) );

                if( channel == null )
                {
                    msg.reply("Invalid voice channel");

                    return;
                }

                cfg.setProp( "lobby", commandVar[ 1 ] );

                msg.reply("lobby voice channel set to " +  channel);
            }

            if (subCommand.equals("onechannel"))
            {
                channel = msg.getGuild().getVoiceChannelByID( Long.parseLong(commandVar[ 1 ] ) );

                if( channel == null )
                {
                    msg.reply("Invalid voice channel");

                    return;
                }

                cfg.setProp( "onechannel", commandVar[ 1 ] );

                msg.reply("Team one voice channel set to " +  channel);
            }

            if (subCommand.equals("twochannel"))
            {
                channel = msg.getGuild().getVoiceChannelByID( Long.parseLong(commandVar[ 1 ] ) );

                if( channel == null )
                {
                    msg.reply("Invalid voice channel");

                    return;
                }

                cfg.setProp( "twochannel", commandVar[ 1 ] );

                msg.reply("Team two voice channel set to " +  channel);
            }

            //prefix setting
            if (subCommand.equals("prefix"))
            {
                cfg.setProp( "prefix", commandVar[ 1 ] );

                msg.reply("prefix set to " +  commandVar[1]);
            }

        }
        catch ( NumberFormatException e )
        {
            msg.reply("Invalid ID, must be only numbers");
        }

        try
        {
            if (subCommand.equals("size"))
            {

                size = Integer.parseInt( commandVar[ 1 ] );

                if( size <= 0  )
                {
                    msg.reply("invalid team size");

                    return;
                }

                cfg.setProp( "teamsize", commandVar[ 1 ] );

                msg.reply("Team size set to " +  size);
            }
        }
        catch ( NumberFormatException e )
        {
            msg.reply("Invalid size, must be whole number");
        }

        queue.check();

    }

    @Override
    public int getRank()
    {
        return mod;
    }


    @Override
    public String help( int permLevel )
    {
        String rtnStr = "setup: \n" +
                "       Used to set 10 man roles and channels\n" +
                "   Sub Commands: \n";

        if( permLevel >= 2 )
            rtnStr +=
                    "       mod (ID): \n" +
                            "           sets mod role for the bot\n";

        rtnStr +=
                "       10mantext (ID): \n" +
                        "           sets the text channel for ten mans\n" +
                        "       sort (ID): \n" +
                        "           sets voice channel to sort with\n" +
                        "       lobby (ID):\n" +
                        "           sets voice channel to move to after game \n" +
                        "       onechannel (ID): \n" +
                        "           sets voice channel for team 1\n" +
                        "       twochannel (ID): \n" +
                        "           sets voice channel for team 2\n" +
                        "       size (size):" +
                        "           sets size for each team";

        return rtnStr;
    }

}
