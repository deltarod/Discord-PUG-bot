package util;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class QueueHandler
{
    private IGuild guild;

    private Config cfg;

    private IDiscordClient client;

    public IUser owner;

    public int teamSize, size;

    private LinkedList<IUser> nextQueue, players, teamOne, teamTwo;

    public IRole modRole;

    public IChannel queueChannel;

    public IVoiceChannel teamOneChannel, teamTwoChannel, sortChannel, lobby;

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

        String temp = cfg.getProp("queuetext");

        if( temp != null )
        {
            queueChannel = guild.getChannelByID( Long.parseLong( temp ) );
        }

        check();
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


            if ( queueChannel == null )
            {

                message = "queue text channel not set, use " + prefix
                    + "setup queuetext (text Channel ID) to fix this";

                Message.builder(client, guild.getDefaultChannel(), message);

                return;


            }



            //sort channel check
            temp = cfg.getProp("sort");

            if (temp == null)
            {

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

            if (temp == null)
            {

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

            if (temp == null)
            {

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
                        + "setup teamsize (size) to fix this";

                sendMessage( message );

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

        IUser user = msg.getAuthor();

        //if no current game
        if( !isRunning )
        {
            if( players.contains( user ) )
            {
                str.append("Already in queue");
            }
            else
            {
                players.add( user );

                str.append( "You have been added to queue, there is currently ");

                str.append( players.size() );

                str.append( " in queue");
            }

            sendMessage( str );


            if( players.size() >= size )
            {
                setupGame();
            }
        }

        //if ongoing game
        else
        {
            if( players.contains( user ) )
            {
                str.append( "Already in current game");

                sendMessage( str );

                return;
            }

            if( nextQueue.contains( user ) )
            {
                str.append( "Already in queue for next game");
            }
            else
            {
                nextQueue.add( user );

                str.append("Already a game running, there is ");

                str.append(nextQueue.size());

                str.append(" in queue");
            }

            sendMessage(str);
        }
    }

    public void clear( IMessage msg )
    {
        players.clear();

        nextQueue.clear();

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

            if(!nextQueue.remove( remove ))
            {
                msg.reply("you were not in the next queue, there is " + inQueue + " in queue");

                return;
            }

            inQueue = nextQueue.size();

            msg.reply("you have been removed from the next queue, there is now " + inQueue + " in queue");

        }
        else
        {
            remove = msg.getAuthor();

            inQueue = players.size();

            if(!players.remove( remove ))
            {
                msg.reply("you were not in queue, there is " + inQueue + " in queue");

                return;
            }

            inQueue = players.size();

            msg.reply("you have been removed from the queue, there is now " + inQueue + " in queue");

        }
    }

    public void finish( IDiscordClient client, IMessage msg )
    {
        if( !isRunning )
        {
            if( queueChannel == null )
            {
                Message.builder(client, guild.getDefaultChannel(), "no ongoing game");
            }
            else
            {
                Message.builder(client, queueChannel, "no ongoing game");
            }
        }
        else
            {
            int increment, nextSize;

            players.clear();

            isRunning = false;

            for (IUser player : teamOneChannel.getUsersHere() )
            {
                player.moveToVoiceChannel(lobby);
            }

            for (IUser player : teamTwoChannel.getUsersHere() )
            {
                player.moveToVoiceChannel(lobby);
            }

            //clear the teams

            teamOne.clear();

            teamTwo.clear();

            sendMessage( "Current game finished" );

            nextSize = nextQueue.size();

            for( increment = 0; increment < nextSize; increment++ )
            {
                IUser current = nextQueue.removeFirst();

                players.add( current );

                if( increment >= size )
                {
                    setupGame();

                    return;
                }
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
            if(!nextQueue.remove( remove ))
            {
                inQueue = nextQueue.size();

                msg.reply( current + " was not in queue, there is " + inQueue + " in queue");
            }
            else
            {
                inQueue = nextQueue.size();

                msg.reply(remove + " has been removed from the queue, there is now " + inQueue + " in queue");
            }
        }
        else
        {
            if( !players.remove( remove ) )
            {
                inQueue = players.size();

                msg.reply(current + " was not in queue, there is " + inQueue + " in queue");
            }
            else
            {
                inQueue = players.size();

                msg.reply(remove + " has been removed from the queue, there is now " + inQueue + " in queue");
            }

        }
    }


    //sets up game with random teams
    private void setupGame()
    {
        boolean team = false;

        //shuffles users since they are added in order
        Collections.shuffle(players);

        //should generate an index to get the captains from
        int index = ((int)( Math.random() * teamSize ) % teamSize);

        isRunning = true;

        StringBuilder str = new StringBuilder( "Game starting:\n Team One: \n");

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

        //team 1 captain
        str.append("Captain: ");

        str.append( teamOne.get( index ) );

        str.append("\n Team Two: \n");

        //team 2 captain
        str.append("Captain: ");

        str.append( teamTwo.get( index ) );

        str.append( "\nPlayers: \n");

        for( IUser user : players )
        {
            str.append( user );

            str.append("\n");
        }

        sendMessage( str );
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
                build.append( user.getDisplayName( guild ) );

                build.append("\n");
            }
        }
        else
        {
            build.append( players.size() );

            build.append("\n Users currently in queue:\n");

            for( IUser user : players )
            {
                build.append(user.getDisplayName( guild ));

                build.append("\n");
            }
        }

        sendMessage( build );
    }

    private void sendMessage( StringBuilder str )
    {
        if( queueChannel == null )
        {
            Message.builder(client, guild.getDefaultChannel(), str.toString());
            return;
        }

        Message.builder(client, queueChannel, str.toString());
    }

    private void sendMessage( String str )
    {
        if( queueChannel == null )
        {
            Message.builder( client, guild.getDefaultChannel(), str );
            return;
        }

        Message.builder( client, queueChannel, str );
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
