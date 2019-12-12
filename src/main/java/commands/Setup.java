package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import util.Config;
import util.MessageBuild;
import util.QueueHandler;

import java.util.Map;

public class Setup implements ICommand
{

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        String[] commandVar;

        int size;

        try
        {
            commandVar = args.split("\\s+", 2);
        }
        catch ( NullPointerException e )
        {
            MessageBuild.reply( msg, "you didnt enter a text ID/no arguments");
            return;
        }

        VoiceChannel voiceTemp;

        MessageChannel textTemp;

        Role role;

        String subCommand = commandVar[0].toLowerCase();

        if (subCommand.equals("mod"))
        {
            try
            {
                role = msg.getGuild().getRoleById( Long.parseLong(commandVar[ 1 ] ) );
            }
            catch ( NumberFormatException e )
            {
                role = msg.getMentionedRoles().get(0);
            }

            if( role == null )
            {
                MessageBuild.reply( msg, "Invalid role");

                return;
            }

            cfg.setProp( "mod", role.getId() );

            MessageBuild.reply( msg, "mod role set to " +  role);
        }

        if (subCommand.equals("admin"))
        {
            try
            {
                role = msg.getGuild().getRoleById( Long.parseLong(commandVar[ 1 ] ) );
            }
            catch ( NumberFormatException e )
            {
                role = msg.getMentionedRoles().get(0);
            }

            if( role == null )
            {
                MessageBuild.reply( msg, "Invalid role");

                return;
            }

            cfg.setProp( "admin", role.getId() );

            MessageBuild.reply( msg, "admin role set to " +  role);
        }

        if (subCommand.equals("queue"))
        {

            textTemp = msg.getChannel();

            if( textTemp == null )
            {
                MessageBuild.reply( msg, "Invalid text channel");

                return;
            }

            cfg.setProp( "queuetext", commandVar[ 1 ] );

            queue.queueChannel = textTemp;

            MessageBuild.reply( msg, "queue text channel set to " +  textTemp);
        }

        if (subCommand.equals("sort"))
        {
            voiceTemp = msg.getGuild().getVoiceChannelById( Long.parseLong(commandVar[ 1 ] ) );

            if( voiceTemp == null )
            {
                MessageBuild.reply( msg, "Invalid voice channel");

                return;
            }

            cfg.setProp( "sort", commandVar[ 1 ] );

            MessageBuild.reply( msg, "Sort voice channel set to " +  voiceTemp );
        }

        if (subCommand.equals("lobby"))
        {
            voiceTemp = msg.getGuild().getVoiceChannelById( Long.parseLong(commandVar[ 1 ] ) );

            if( voiceTemp == null )
            {
                MessageBuild.reply( msg, "Invalid voice channel");

                return;
            }

            cfg.setProp( "lobby", commandVar[ 1 ] );

            MessageBuild.reply( msg, "lobby voice channel set to " +  voiceTemp );
        }

        if (subCommand.equals("onechannel"))
        {
            voiceTemp = msg.getGuild().getVoiceChannelById( Long.parseLong(commandVar[ 1 ] ) );

            if( voiceTemp == null )
            {
                MessageBuild.reply( msg, "Invalid voice channel");

                return;
            }

            cfg.setProp( "onechannel", commandVar[ 1 ] );

            MessageBuild.reply( msg, "Team one voice channel set to " +  voiceTemp );
        }

        if (subCommand.equals("twochannel") )
        {
            voiceTemp = msg.getGuild().getVoiceChannelById( Long.parseLong(commandVar[ 1 ] ) );

            if( voiceTemp == null )
            {
                MessageBuild.reply( msg, "Invalid voice channel");

                return;
            }

            cfg.setProp( "twochannel", commandVar[ 1 ] );

            MessageBuild.reply( msg, "Team two voice channel set to " +  voiceTemp );
        }

        //prefix setting
        if ( subCommand.equals("prefix") )
        {
            cfg.setProp( "prefix", commandVar[ 1 ] );

            MessageBuild.reply( msg, "prefix set to " +  commandVar[1]);
        }
        try
        {
            if ( subCommand.equals("teamsize") )
            {

                size = Integer.parseInt( commandVar[ 1 ] );

                if( size <= 0  )
                {
                    MessageBuild.reply( msg, "invalid team size");

                    return;
                }

                cfg.setProp( "teamsize", commandVar[ 1 ] );

                queue.teamSize = size;

                queue.size = size*2;

                MessageBuild.reply( msg, "Team size set to " +  size);
            }
        }
        catch ( NumberFormatException e )
        {
            MessageBuild.reply( msg, "Invalid size, must be a whole/positive number");
        }

        queue.check();

    }

    @Override
    public int getRank()
    {
        return admin;
    }


    @Override
    public String help( int permLevel )
    {
        return  "setup: \n" +
                "       Used to set 10 man settings\n" +
                "   Sub Commands: \n" +
                "       mod (ID): \n" +
                "           sets mod role for the guild\n" +
                "       admin (ID): \n" +
                "           sets admin role for guild\n" +
                "       queue (ID): \n" +
                "           sets the text channel for queue messages\n" +
                "       sort (ID): \n" +
                "           sets voice channel to sort with\n" +
                "       lobby (ID):\n" +
                "           sets voice channel to move to after game \n" +
                "       onechannel (ID): \n" +
                "           sets voice channel for team 1\n" +
                "       twochannel (ID): \n" +
                "           sets voice channel for team 2\n" +
                "       teamsize (size):\n" +
                "           sets size for each team";

    }

}
