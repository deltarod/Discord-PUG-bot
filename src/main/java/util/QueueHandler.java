package util;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

import java.util.LinkedList;
import java.util.List;

public class QueueHandler
{
    private IGuild guild;

    private Config cfg;

    private IDiscordClient client;

    public IUser owner;

    public int teamSize, size;

    private List<IUser> nextQueue, players, teamOne, teamTwo;

    public IRole modRole;

    private static IChannel queueChannel;

    public static IVoiceChannel teamOneChannel, teamTwoChannel, sortChannel, lobby;

    private boolean isRunning = false;

    public QueueHandler(Config cfg, IGuild guild, IDiscordClient client, IUser owner )
    {
        this.guild = guild;

        this.cfg = cfg;

        this.client = client;

        this.owner = owner;

        players = new LinkedList<>();

        teamOne = new LinkedList<>();

        teamTwo = new LinkedList<>();

        nextQueue = new LinkedList<>();

        check();

        // TODO: 3/4/2018 allow different sized teams for other games

    }


    public void check()
    {
        String temp, message, prefix = cfg.getProp("prefix");

        try {

            //probably could simplify this but each one is a little different so i would rather not

            //ModRole check
            temp = cfg.getProp("mod");

            if (temp == null) {

                message = "mod role not set, use " + prefix
                        + "setup mod (role ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            modRole = guild.getRoleByID(Long.parseLong(temp));

            //queueChannel check
            temp = cfg.getProp("queuetext");

            if (temp == null) {

                message = "queue text channel not set, use " + prefix
                        + "setup queuetext (text Channel ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }
            queueChannel = guild.getChannelByID(Long.parseLong(temp));


            //sort channel check
            temp = cfg.getProp("sort");

            if (temp == null) {

                message = "sort voice not set, use " + prefix
                        + "setup sort (voice Channel ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            sortChannel = guild.getVoiceChannelByID(Long.parseLong(temp));


            //lobby channel check
            temp = cfg.getProp("lobby");

            if (temp == null) {

                message = "lobby voice not set, use " + prefix
                        + "setup lobby (voice Channel ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            lobby = guild.getVoiceChannelByID(Long.parseLong(temp));


            //teamOneVoice check
            temp = cfg.getProp("onechannel");

            if (temp == null) {

                message = "Team one voice channel not set, use " + prefix
                        + "setup onechannel (voice Channel ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            teamOneChannel = guild.getVoiceChannelByID(Long.parseLong(temp));


            //teamTwoVoice check
            temp = cfg.getProp("twochannel");

            if (temp == null)
            {

                message = "Team two voice channel not set, use " + prefix
                        + "setup twochannel (voice Channel ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            teamTwoChannel = guild.getVoiceChannelByID(Long.parseLong(temp));


        }
        catch ( NumberFormatException e )
        {
            if( queueChannel == null )
            {
                Message.builder( client, guild.getDefaultChannel(), "Invalid ID");
                return;
            }
            Message.builder( client, queueChannel, "Invalid ID");

            e.printStackTrace();
        }

        try
        {
            //size check
            temp = cfg.getProp("teamsize");

            if (temp == null)
            {

                message = "Team size not set, use " + prefix
                        + "setup size (size) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            teamSize = Integer.parseInt( temp );

            size = teamSize * 2;
        }
        catch ( NumberFormatException e )
        {
            if( queueChannel == null )
            {
                Message.builder( client, guild.getDefaultChannel(), "Invalid size");
                return;
            }
            Message.builder( client, queueChannel, "Invalid size");

            e.printStackTrace();
        }
    }

    public void join(IDiscordClient client, IMessage msg )
    {
        StringBuilder str = new StringBuilder();

        if( isRunning )
        {
            if( nextQueue.contains( msg.getAuthor() ))
            {
                str.append("already in queue");
                return;
            }

            str.append( "There is already a game running, you are in queue for the next game. Your current position is ");

            nextQueue.add( msg.getAuthor() );

            str.append( nextQueue.size() );

            return;
        }

        if( players.contains( msg.getAuthor() ))
        {
            str.append("already in queue");
            return;
        }

        players.add( msg.getAuthor() );

        if( players.size() < size )
        {
            str.append("You have been added to queue, there is currently ");

            str.append( players.size() );

            str.append(" in queue");
        }

        if( queueChannel != null )
        {
            Message.builder( client, queueChannel, str.toString() );
        }
        else
        {
            msg.reply( str.toString() );
        }

        if( players.size() >= size )
        {
            setupGame();
        }
    }

    public void clear( IMessage msg )
    {
        players.clear();

        msg.reply("Queue is cleared");
    }

    public void leave( IMessage msg )
    {
        IUser remove;

        int inQueue;

        if( isRunning )
        {
            remove = msg.getAuthor();

            inQueue = nextQueue.size();

            if(!players.remove( remove ))
            {
                msg.reply("you were not in queue, there is " + inQueue + " in queue");

                inQueue = players.size();
            }

            msg.reply("you have been removed from the queue, there is now " + inQueue + " in queue");

        }
        else
        {
            remove = msg.getAuthor();

            inQueue = players.size();

            if(!players.remove( remove ))
            {
                msg.reply("you were not in queue, there is " + inQueue + " in queue");

                inQueue = players.size();
            }

            msg.reply("you have been removed from the queue, there is now " + inQueue + " in queue");

        }
    }

    public void finish( IDiscordClient client, IMessage msg )
    {
        int increment = 0;

        players.clear();

        isRunning = false;

        for( IUser player : teamOneChannel.getUsersHere() )
        {
            player.moveToVoiceChannel( lobby );
        }

        for ( IUser player : teamTwoChannel.getUsersHere() )
        {
            player.moveToVoiceChannel( lobby );
        }

        //clear the teams

        teamOne = new LinkedList<>();

        teamTwo = new LinkedList<>();

        if( queueChannel == null )
        {
            Message.builder(client, guild.getDefaultChannel(), "Current game finished");
            return;
        }
        else
        {
            Message.builder(client, queueChannel, "Current game finished");
        }



        for( IUser user : nextQueue )
        {
            increment++;

            players.add( user );

            nextQueue.remove( user );


            if( increment >= size )
            {
                setupGame();

                break;
            }
        }

    }


    public void remove( IMessage msg, IUser remove )
    {
        int inQueue;

        IUser current;

        current = remove;

        if( isRunning )
        {
            inQueue = nextQueue.size();

            if(!players.remove( remove ))
            {
                msg.reply( current + " was not in queue, there is " + inQueue + " in queue");

                return;
            }

        }
        else
        {
            inQueue = players.size();

            if(!players.remove( remove ))
            {
                msg.reply(current + " was not in queue, there is " + inQueue + " in queue");

                return;
            }

        }

        msg.reply(remove + " has been removed from the queue, there is now " + inQueue + " in queue");
    }


    //sets up game with random teams
    private void setupGame()
    {
        boolean team = false;

        int size, index;

        StringBuilder str = new StringBuilder( "Game starting:\n Team One: \n");

        IUser current;

        for( IUser user : players )
        {
            if( team )
            {
                teamOne.add( user );
            }
            else
            {
                teamTwo.add( user );
            }

            team = !team;
        }

        //clears linked list
        players = new LinkedList<>();

        index = 0;

        for( IUser user : teamOne )
        {
            if( index == 0 )
            {
                str.append( "Captain: ");

                index++;
            }


            str.append( user );

            str.append( "\n ");
        }

        str.append("\n Team Two: \n");

        index = 0;

        for( IUser user : teamTwo )
        {

            if( index == 0 )
            {
                str.append( "Captain: ");

                index++;
            }


            str.append( user );

            str.append( "\n ");
        }

        if( queueChannel == null )
        {
            Message.builder( client, guild.getDefaultChannel(), str.toString() );
        }

        else
        {
            Message.builder( client, queueChannel, str.toString() );
        }


    }

    public void listUsersInQueue( IMessage msg )
    {
        StringBuilder build = new StringBuilder("Current users in Queue: ");

        if( isRunning )
        {
            build.append( nextQueue.size() );

            build.append("\nUsers currently in queue for next game:\n");

            for( IUser user : nextQueue )
            {
                build.append( user );

                build.append("\n");
            }
        }
        else
        {
            build.append(players.size());

            build.append("\n Users currently in queue:\n");

            for( IUser user : players )
            {
                build.append(user);

                build.append("\n");
            }
        }

        if( queueChannel == null )
        {
            Message.builder(client, guild.getDefaultChannel(), build.toString());
            return;
        }

        Message.builder(client, queueChannel, build.toString());
    }

    public boolean inTeam( int team, IUser user )
    {
        if( team == 1 )
        {
            return teamOne.contains( user );
        }

        else if( team == 2 )
        {
            return teamTwo.contains( user );
        }

        return false;
    }
}
