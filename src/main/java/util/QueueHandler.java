package util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class QueueHandler
{
    private Guild guild;

    private Config cfg;

    private JDA client;

    private Member owner;

    public long startTime;

    public int teamSize, size;

    private LinkedList<Member> nextQueue, players, teamOne, teamTwo, bannedUser;

    private LinkedList<String> maps, current;

    private boolean selectionMode;

    public Role modRole, adminRole;

    public MessageChannel queueChannel;

    public VoiceChannel teamOneChannel, teamTwoChannel, sortChannel, lobby;

    private boolean isRunning = false;

    /**
     * Handles the queueing for the 10 man bot
     * @param cfg       current guild cfg
     * @param guild     current guild
     * @param client    bot client
     * @param owner     owner user
     */
    public QueueHandler(Config cfg, Guild guild, JDA client, Member owner )
    {
        this.guild = guild;

        this.cfg = cfg;

        this.client = client;

        this.owner = owner;

        players = new LinkedList<>();

        teamOne = new LinkedList<>();

        teamTwo = new LinkedList<>();

        nextQueue = new LinkedList<>();

        bannedUser = new LinkedList<>();

        startTime = System.currentTimeMillis();

        loadMaps();

        String temp = cfg.getProp("queuetext");

        if( temp != null )
        {
            queueChannel = guild.getTextChannelById( temp );
        }

        temp = cfg.getProp("mode");

        if( temp == null )
        {
            temp = "veto";

            cfg.setProp("mode", "veto" );
        }
        else {
            switch (temp) {
                case "veto":
                    selectionMode = true;
                    break;

                case "random":
                    selectionMode = false;
                    break;
                default:
                    selectionMode = true;
                    break;
            }
        }

        check();
    }


    public void check()
    {
        String temp, message, prefix = cfg.getProp("prefix");

        try {

            if( guild.getDefaultChannel() == null )
            {
                System.out.println( "Default channel is null" );

                return;
            }
            if( !guild.getDefaultChannel().canTalk() )
            {
                System.out.println("Cant talk in default channel, setup a queue channel that the bot can talk in");
            }

            //probably could simplify this but each one is a little different so i would rather not

            //AdminRole check
            temp = cfg.getProp("admin");

            if (temp == null) {

                message = "Admin role not set, use " + prefix
                        + "setup admin (role ID/@Role) to fix this";

                if( queueChannel == null )
                {
                    MessageBuild.builder( guild.getDefaultChannel(), message);
                    return;
                }

                MessageBuild.builder( queueChannel, message);

                return;
            }

            adminRole = guild.getRoleById(Long.parseLong(temp));


            //ModRole check
            temp = cfg.getProp("mod");

            if (temp == null) {

                message = "mod role not set, use " + prefix
                        + "setup mod (role ID/@Role) to fix this";

                if( queueChannel == null )
                {
                    MessageBuild.builder( guild.getDefaultChannel(), message);
                    return;
                }

                MessageBuild.builder(  queueChannel, message);

                return;
            }

            modRole = guild.getRoleById(Long.parseLong(temp));


            if ( queueChannel == null )
            {

                message = "queue text channel not set, use " + prefix
                    + "setup queue (text Channel ID/#Channel) to fix this";

                MessageBuild.builder( guild.getDefaultChannel(), message);

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
                    MessageBuild.builder( guild.getDefaultChannel(), message);
                    return;
                }

                MessageBuild.builder( queueChannel, message);

                return;
            }

            sortChannel = guild.getVoiceChannelById(Long.parseLong(temp));


            //lobby channel check
            temp = cfg.getProp("lobby");

            if (temp == null)
            {

                message = "lobby voice not set, use " + prefix
                        + "setup lobby (voice Channel ID) to fix this";

                if( queueChannel == null )
                {
                    MessageBuild.builder( guild.getDefaultChannel(), message);
                    return;
                }

                MessageBuild.builder( queueChannel, message);

                return;
            }

            lobby = guild.getVoiceChannelById(Long.parseLong(temp));


            //teamOneVoice check
            temp = cfg.getProp("onechannel");

            if (temp == null)
            {

                message = "Team one voice channel not set, use " + prefix
                        + "setup onechannel (voice Channel ID) to fix this";

                if( queueChannel == null )
                {
                    MessageBuild.builder( guild.getDefaultChannel(), message);
                    return;
                }

                MessageBuild.builder( queueChannel, message);

                return;
            }

            teamOneChannel = guild.getVoiceChannelById(Long.parseLong(temp));


            //teamTwoVoice check
            temp = cfg.getProp("twochannel");

            if (temp == null)
            {

                message = "Team two voice channel not set, use " + prefix
                        + "setup twochannel (voice Channel ID) to fix this";

                if( queueChannel == null )
                {
                    MessageBuild.builder( guild.getDefaultChannel(), message);
                    return;
                }

                MessageBuild.builder( queueChannel, message);

                return;
            }

            teamTwoChannel = guild.getVoiceChannelById(Long.parseLong(temp));

        }
        catch ( NumberFormatException e )
        {
            if( queueChannel == null )
            {
                MessageBuild.builder( guild.getDefaultChannel(), "Invalid ID");
                return;
            }
            MessageBuild.builder( queueChannel, "Invalid ID");

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
                MessageBuild.builder( guild.getDefaultChannel(), "Invalid size");
                return;
            }
            MessageBuild.builder( queueChannel, "Invalid size");

            e.printStackTrace();
        }
    }

    public void join( JDA client, Message msg )
    {
        StringBuilder str = new StringBuilder();

        Member member = msg.getMember();

        MessageChannel channel = msg.getChannel();

        if( bannedUser.contains( member ) )
        {
            MessageBuild.reply( channel, member, "You are banned from queueing" );
            return;
        }

        //if no current game
        if( !isRunning )
        {
            if( players.contains( member ) )
            {
                str.append("Already in queue");
            }
            else
            {
                players.add( member );

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
            if( players.contains( member ) )
            {
                str.append( "Already in current game");

                sendMessage( str );

                return;
            }

            if( nextQueue.contains( member ) )
            {
                str.append( "Already in queue for next game");
            }
            else
            {
                nextQueue.add( member );

                str.append("Already a game running, there is ");

                str.append(nextQueue.size());

                str.append(" in queue");
            }

            sendMessage(str);
        }
    }

    public void clear( Message msg )
    {
        players.clear();

        nextQueue.clear();

        MessageBuild.reply( msg, "Queue is cleared" );
    }

    public void leave( Message msg )
    {
        User remove;

        int inQueue;

        if( isRunning )
        {
            remove = msg.getAuthor();

            inQueue = nextQueue.size();

            if(!nextQueue.remove( remove ))
            {
                MessageBuild.reply( msg, "you were not in the next queue, there is " + inQueue + " in queue");

                return;
            }

            inQueue = nextQueue.size();

            MessageBuild.reply( msg, "you have been removed from the next queue, there is now " + inQueue + " in queue");

        }
        else
        {
            remove = msg.getAuthor();

            inQueue = players.size();

            if(!players.remove( remove ))
            {
                MessageBuild.reply( msg,"you were not in queue, there is " + inQueue + " in queue");

                return;
            }

            inQueue = players.size();

            MessageBuild.reply( msg,"you have been removed from the queue, there is now " + inQueue + " in queue");

        }
    }

    public void finish()
    {
        if( !isRunning )
        {
            if( queueChannel == null )
            {
                MessageBuild.builder( guild.getDefaultChannel(), "no ongoing game");
            }
            else
            {
                MessageBuild.builder( queueChannel, "no ongoing game");
            }
        }
        else
            {
            int increment, nextSize;

            players.clear();

            isRunning = false;

            //clear the teams

            teamOne.clear();

            teamTwo.clear();



                for ( Member player : teamOneChannel.getMembers() )
                {
                    guild.moveVoiceMember( player, teamOneChannel ).queue();
                }

                for ( Member player : teamTwoChannel.getMembers() )
                {
                    guild.moveVoiceMember( player, teamTwoChannel ).queue();
                }

            sendMessage( "Current game finished" );

            nextSize = nextQueue.size();

            for( increment = 0; increment < nextSize; increment++ )
            {
                Member current = nextQueue.removeFirst();

                players.add( current );

                if( increment >= size )
                {
                    setupGame();

                    return;
                }
            }
        }

    }


    public void remove( Message msg, Member remove )
    {
        int inQueue;

        Member current;

        current = remove;

        if( isRunning )
        {
            if(!nextQueue.remove( remove ))
            {
                inQueue = nextQueue.size();

                MessageBuild.reply( msg, current + " was not in queue, there is " + inQueue + " in queue");
            }
            else
            {
                inQueue = nextQueue.size();

                MessageBuild.reply( msg, remove + " has been removed from the queue, there is now " + inQueue + " in queue");
            }
        }
        else
        {
            if( !players.remove( remove ) )
            {
                inQueue = players.size();

                MessageBuild.reply( msg,current + " was not in queue, there is " + inQueue + " in queue");
            }
            else
            {
                inQueue = players.size();

                MessageBuild.reply( msg,remove + " has been removed from the queue, there is now " + inQueue + " in queue");
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

        for( Member user : players )
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

            try
            {
                str.append(current.removeFirst());
            }
            catch ( NoSuchElementException e )
            {
                sendMessage( "No maps added to maplist" );
            }



            str.append("\n");

            //readds maps if no more are left for next game
            if( current.size() <= 0 )
            {
                resetMaps();
            }
        }

        str.append( "\nPlayers: \n");

        for( Member user : players )
        {
            str.append( user );

            str.append("\n");
        }

        StringBuilder print = new StringBuilder();

        print.append( "Server: ");

        print.append( guild.getName() );

        print.append("\nTeam One: \n");


        for( Member user : teamOne )
        {
            print.append( user.getNickname() );

            print.append("\n");
        }

        print.append("\nTeam Two: \n");

        for( Member user : teamTwo )
        {
            print.append( user.getNickname() );

            print.append("\n");
        }

        System.out.println(print.toString());

        sendMessage( str );
    }

    public void listUsersInQueue( Message msg )
    {
        StringBuilder build = new StringBuilder("Current users in Queue: ");

        if( isRunning )
        {
            build.append( nextQueue.size() );

            build.append("\nUsers currently in queue for next game:\n");

            for( Member user : nextQueue )
            {
                build.append( user.getNickname() );

                build.append("\n");
            }
        }
        else
        {
            build.append( players.size() );

            build.append("\n Users currently in queue:\n");

            for( Member user : players )
            {
                build.append( user.getNickname() );

                build.append("\n");
            }
        }

        sendMessage( build );
    }

    public void addToTeam( boolean team, Member user, Message msg )
    {
        if( team )
        {
            if( !teamOne.contains( user ) || teamTwo.contains( user ))
            {
                teamOne.add(user);

                MessageBuild.reply( msg,user.getUser().getAsMention() + " added to team 1");
            }
            else
            {
                MessageBuild.reply( msg, "user already on team");
            }
        }
        else
        {
            if( !teamTwo.contains( user ) || teamOne.contains( user ))
            {
                teamTwo.add(user);

                MessageBuild.reply( msg,user.getUser().getAsMention() + " added to team 2");
            }
            else
            {
                MessageBuild.reply( msg, "user already on team");
            }
        }
    }

    public void removeFromTeam( boolean team, Member user, Message msg )
    {
        if( team )
        {
            if( teamOne.contains( user ) )
            {
                teamOne.remove(user);

                MessageBuild.reply( msg,user.getUser().getAsMention() + " removed from team 1");
            }
            else
            {
                MessageBuild.reply( msg, "user not on team");
            }
        }
        else
        {
            if( teamTwo.contains( user ) )
            {
                teamTwo.remove(user);

                MessageBuild.reply( msg,user.getUser().getAsMention() + " removed from team 2");
            }
            else
            {
                MessageBuild.reply( msg, "user not on team");
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

    public void listMaps( Message msg, boolean arg )
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

        MessageBuild.reply( msg, str.toString() );
    }

    public void addMap( Message msg, String map )
    {
        if( maps.contains(map) )
        {
            MessageBuild.reply( msg,"already in pool");
        }
        else
        {
            maps.add( map );

            current.add( map );

            cfg.setProp("maps", mapsToString(maps));

            MessageBuild.reply( msg,map + " added to pool");
        }

    }

    public void removeMap( Message msg, String map )
    {
        if( maps.contains(map) )
        {
            maps.remove(map);

            cfg.setProp("maps", mapsToString(maps));

            if( current.contains(map) )
            {
                current.remove(map);
            }
            MessageBuild.reply( msg,map + " removed from pool");
        }
        else
        {
            MessageBuild.reply( msg,map + " not in pool");
        }
    }

    public void resetMaps()
    {
        current = maps;
    }

    public void toggleMode( Message msg )
    {
        selectionMode = !selectionMode;

        if( selectionMode )
        {
            MessageBuild.reply( msg,"mode set to veto pick");

            cfg.setProp("mode", "veto");
        }
        else
        {
            MessageBuild.reply( msg,"mode set to random map");

            cfg.setProp("mode", "random");
        }
    }

    public void viewMode( Message msg )
    {
        if( selectionMode )
        {
            MessageBuild.reply( msg,"Mode: Veto");
        }
        else
        {
            MessageBuild.reply( msg,"Mode: Random Map");
        }
    }

    public boolean modeBool()
    {
        return selectionMode;
    }

    ///////////////////////UTILS///////////////////////
    private void sendMessage( StringBuilder str )
    {
        if( queueChannel == null )
        {
            MessageBuild.builder( guild.getDefaultChannel(), str.toString());
            return;
        }

        MessageBuild.builder( queueChannel, str.toString());
    }

    private void sendMessage( String str )
    {
        if( queueChannel == null )
        {
            MessageBuild.builder( guild.getDefaultChannel(), str );
            return;
        }

        MessageBuild.builder( queueChannel, str );
    }

    public boolean inTeam( int team, Member user )
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

    public boolean banUser( Member user )
    {
        if( bannedUser.contains(user) )
        {
            return false;
        }
        else
        {
            bannedUser.add(user);

            players.remove( user );

            nextQueue.remove( user );

            return true;
        }


    }

    public boolean unbanUser ( Member user )
    {
        if( bannedUser.contains(user) )
        {
            bannedUser.remove(user);

            return true;
        }
        else
        {
            return false;
        }
    }

    public void forceRemove ( Member user )
    {
        players.remove( user );

        nextQueue.remove( user );
    }
}
