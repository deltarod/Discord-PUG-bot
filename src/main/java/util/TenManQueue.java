package util;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

import java.util.LinkedList;
import java.util.List;

public class TenManQueue
{
    private IGuild guild;

    private Config cfg;

    private IDiscordClient client;

    private IUser owner;

    private List<IUser> nextQueue, players, teamOne, teamTwo;

    public static IRole teamOneRole, teamTwoRole, modRole;

    private static IChannel queueChannel;

    public static IVoiceChannel teamOneChannel, teamTwoChannel, sortChannel, lobby;

    private boolean isRunning = false;

    public TenManQueue( Config cfg, IGuild guild, IDiscordClient client, IUser owner )
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

    }


    public void check()
    {
        try {

            String temp, message, prefix = cfg.getProp("prefix");


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
            temp = cfg.getProp("10mantext");

            if (temp == null) {

                message = "10 man text channel not set, use " + prefix
                        + "setup 10mantext (text Channel ID) to fix this";

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


            //teamOneRole check
            temp = cfg.getProp("onerole");

            if ( temp == null )
            {
                message = "Team one role not set, use " + prefix
                        + "setup onerole (role ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            teamOneRole = guild.getRoleByID(Long.parseLong(temp));


            //teamTwoRole check
            temp = cfg.getProp("tworole");

            if (temp == null) {

                message = "Team two role not set, use " + prefix
                        + "setup tworole (role ID) to fix this";

                if( queueChannel == null )
                {
                    Message.builder(client, guild.getDefaultChannel(), message);
                    return;
                }

                Message.builder(client, queueChannel, message);

                return;
            }

            teamTwoRole = guild.getRoleByID(Long.parseLong(temp));

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
    }

    public static IRole getModRole()
    {
        return modRole;
    }

    public void join(IDiscordClient client, IMessage msg )
    {
        if( isRunning )
        {
            if( nextQueue.contains( msg.getAuthor() ))
            {
                msg.reply("already in queue");
                return;
            }

            msg.reply( "There is already a game running, you are in queue for the next game. Your current position is " + (nextQueue.size() + 1) );

            nextQueue.add( msg.getAuthor() );

            return;
        }

        if( players.contains( msg.getAuthor() ))
        {
            msg.reply("already in queue");
            return;
        }

        players.add( msg.getAuthor() );

        if( players.size() < 10 )
        {
            msg.reply("You have been added to queue, there is currently " + players.size() + " in queue");
        }

        if( players.size() > 9 )
        {
            if( queueChannel == null )
            {

                Message.builder(client, msg, "game starting @everyone");
            }

            else
            {
                    Message.builder(client, queueChannel, "game starting @everyone");
            }

            setupGame();

            isRunning = true;
        }
    }

    public void clear( IMessage msg )
    {
        players.clear();

        msg.reply("Queue is cleared");
    }

    public void leave( IDiscordClient client, IMessage msg )
    {
        IUser remove;

        int inQueue = 0;

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

        for( IUser player : teamOne )
        {
            player.removeRole( teamOneRole );

            player.moveToVoiceChannel( lobby );
        }

        for ( IUser player : teamTwo )
        {
            player.removeRole( teamTwoRole );

            player.moveToVoiceChannel( lobby );
        }

        teamOne = new LinkedList<>();

        teamTwo = new LinkedList<>();

        if( queueChannel == null )
        {
            Message.builder(client, guild.getDefaultChannel(), "Current 10 man finished @everyone");
            return;
        }
        else
        {
            Message.builder(client, queueChannel, "Current 10 man finished @everyone");
        }



        for( IUser user : nextQueue )
        {
            increment++;

            players.add( user );

            nextQueue.remove( user );


            if( increment >= 10 )
            {
                break;
            }
        }

    }


    public void remove( IMessage msg, IUser remove )
    {
        int inQueue = 0;

        IUser current;

        current = remove;

        if( isRunning )
        {
            inQueue = nextQueue.size();

            if(!players.remove( remove ))
            {
                msg.reply( current + " was not in queue, there is " + inQueue + " in queue");

                inQueue = nextQueue.size();

                return;
            }

        }
        else
        {
            inQueue = players.size();

            if(!players.remove( remove ))
            {
                msg.reply(current + " was not in queue, there is " + inQueue + " in queue");

                inQueue = players.size();

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

        IUser current;

        while( players.size() > 0 )
        {
            size = players.size();

            index = (int)(Math.random()*(size));

            current = players.remove( index );

            if( team )
            {
                current.addRole( teamOneRole );

                teamOne.add(current);


            }
            else
            {
                current.addRole( teamTwoRole );

                teamTwo.add(current);
            }

            team = !team;

        }
    }

    public void listUsersInQueue( IMessage msg )
    {
        StringBuilder build = new StringBuilder("Current users in Queue: ");

        if( isRunning )
        {
            build.append( nextQueue.size() );

            build.append("\nUsers currently in queue:\n");

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

        return;

    }
}
