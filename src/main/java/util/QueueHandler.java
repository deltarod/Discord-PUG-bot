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

    private IUser owner;

    public long startTime;

    public int teamSize, size;

    private LinkedList<IUser> nextQueue, players, teamOne, teamTwo;

    private LinkedList<String> maps, current;

    private boolean selectionMode = true;

    public IRole modRole, adminRole;

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

        startTime = System.currentTimeMillis();

        loadMaps();

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

            //AdminRole check
            temp = cfg.getProp("admin");

            if (temp == null) {

                message = "Admin role not set, use " + prefix
                        + "setup admin (role ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            adminRole = guild.getRoleByID(Long.parseLong(temp));


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


            //mode load
            temp = cfg.getProp("mode");

            if (temp == null)
            {
                cfg.setProp("mode", "veto" );
            }
            else
            {
                if( temp.equals("veto"))
                {
                    selectionMode = true;
                }
                selectionMode = false;

            }


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

        //shuffles current map pool
        Collections.shuffle(current);

        //should generate an index to get the captains from
        int index = ((int)( Math.random() * teamSize ) % teamSize);

        isRunning = true;

        StringBuilder str = new StringBuilder( "Game starting:\n");

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

        if( selectionMode )
        {
            str.append("Team One:\n");

            //team 1 captain
            str.append("Captain: ");

            str.append( teamOne.get( index ) );

            str.append("\n Team Two: \n");

            //team 2 captain
            str.append("Captain: ");

            str.append( teamTwo.get( index ) );
        }
        else
        {

            str.append("Map: ");

            str.append(current.removeFirst());

            str.append("\n");

            //readds maps if no more are left for next game
            if( current.size() <= 0 )
            {
                resetMaps();
            }
        }

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

    public void addToTeam( boolean team, IUser user, IMessage msg )
    {
        if( team )
        {
            if( !teamOne.contains( user ) || teamTwo.contains( user ))
            {
                teamOne.add(user);

                msg.reply(user.mention() + " added to team 1");
            }
            else
            {
                msg.reply( "user already on team");
            }
        }
        else
        {
            if( !teamTwo.contains( user ) || teamOne.contains( user ))
            {
                teamTwo.add(user);

                msg.reply(user.mention() + " added to team 2");
            }
            else
            {
                msg.reply( "user already on team");
            }
        }
    }

    public void removeFromTeam( boolean team, IUser user, IMessage msg )
    {
        if( team )
        {
            if( teamOne.contains( user ) )
            {
                teamOne.remove(user);

                msg.reply(user.mention() + " removed from team 1");
            }
            else
            {
                msg.reply( "user not on team");
            }
        }
        else
        {
            if( teamTwo.contains( user ) )
            {
                teamTwo.remove(user);

                msg.reply(user.mention() + " removed from team 2");
            }
            else
            {
                msg.reply( "user not on team");
            }
        }
    }

    private void loadMaps()
    {
        maps = new LinkedList<>();

        current = new LinkedList<>();

        String mapStr = cfg.getProp("maps");

        int start = 0;

        if( mapStr == null )
        {
            return;
        }

        for( int i = 0; i < mapStr.length(); i++ )
        {
            if( mapStr.charAt(i) == ':' )
            {
                    current.add( mapStr.substring( start, i));

                    maps.add( mapStr.substring( start, i));

                    start = i+1;
            }
        }
    }

    public void listMaps( IMessage msg, boolean arg )
    {
        LinkedList<String> list;

        StringBuilder str = new StringBuilder("Maps:\n");

        if( arg )
        {
            list = current;
        }
        else
        {
            list = maps;
        }

        for (String map : list )
        {
            str.append(map);

            str.append("\n");
        }

        msg.reply(str.toString());
    }

    public void addMap( IMessage msg, String map )
    {
        if( maps.contains(map) )
        {
            msg.reply("already in pool");
        }
        else
        {
            maps.add( map );

            current.add( map );

            cfg.setProp("maps", mapsToString(maps));

            msg.reply(map + " added to pool");
        }

    }

    public void removeMap( IMessage msg, String map )
    {
        if( maps.contains(map) )
        {
            maps.remove(map);

            cfg.setProp("maps", mapsToString(maps));

            if( current.contains(map) )
            {
                current.remove(map);
            }
            msg.reply(map + " removed from pool");
        }
        else
        {
            msg.reply(map + " not in pool");
        }
    }

    public void resetMaps()
    {
        current = maps;
    }

    public void toggleMode( IMessage msg )
    {
        selectionMode = !selectionMode;

        if( selectionMode )
        {
            msg.reply("mode set to veto pick");

            cfg.setProp("mode", "veto");
        }
        else
        {
            msg.reply("mode set to random map");

            cfg.setProp("mode", "random");
        }
    }

    public void viewMode( IMessage msg )
    {
        if( selectionMode )
        {
            msg.reply("Mode: Veto");
        }
        else
        {
            msg.reply("Mode: Random Map");
        }
    }

    ///////////////////////UTILS///////////////////////
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

    private String mapsToString( LinkedList<String> list )
    {
        StringBuilder str = new StringBuilder();

        for( String map : maps )
        {
            str.append(map);

            str.append(":");
        }

        return str.toString();
    }
}
