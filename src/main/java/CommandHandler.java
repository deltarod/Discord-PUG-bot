import commands.ICommand;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import util.Config;
import org.reflections.Reflections;
import util.QueueHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandHandler
{
    //prefix of commands for a given guild
    String prefix;

    //config for guild
    private Config cfg;

    private IGuild guild;

    //owner of bot
    private IUser owner;

    //current bot client
    private IDiscordClient client;

    //map of commands
    private Map<String, ICommand> cmdMap;

    //queue man queue for server
    public QueueHandler queue;


    CommandHandler( String commandPrefix, IGuild guild, IDiscordClient client, Config cfg, String owner )
    {
        prefix = commandPrefix;

        this.guild = guild;

        this.client = client;

        this.cfg = cfg;

        this.owner = guild.getUserByID( Long.parseLong( owner ) );

        queue = new QueueHandler( cfg, guild, client, this.owner );

        cmdMap = new HashMap<>();

        Reflections ref = new Reflections("commands");

        //loads commands from the util package
        //Lambda statement!
        ref.getSubTypesOf(ICommand.class).forEach(subclass -> {
            try
            {
                ICommand cmd = subclass.newInstance();

                cmdMap.put( cmd.getName(), cmd );
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        });
    }


    /**
     * Checks for correct permissions, then executes command
     * @param command   command to execute
     * @param msg       message of origin
     */
    public void run( String command, IMessage msg )
    {
        IUser author = msg.getAuthor();

        String[] commandVar = command.split("\\s+", 2);

        ICommand cmd = cmdMap.get( commandVar[ 0 ] );

        int permLevel = ICommand.any;

        if( cmd == null )
        {
            msg.reply( commandVar[ 0 ] + " is an invalid command");

            return;
        }


        if( checkPerms( cmd.getRank(), author ) )
        {
            try
            {
                cmd.run( client, commandVar[1], msg, cfg, cmdMap, queue, permLevel );
            }
            //if no args
            catch ( ArrayIndexOutOfBoundsException e )
            {
                cmd.run( client, null, msg, cfg, cmdMap, queue, permLevel );
            }
        }
        else
        {
            msg.reply("Your power is weak");
        }

        //if prefix gets changed
        prefix = cfg.getProp("prefix");
    }

    private boolean checkPerms( int requiredPerm, IUser author )
    {
        int perm = ICommand.any;

        if( isAboveRole( queue.modRole, author ) )
        {
            perm = ICommand.mod;
        }

        if( isAboveRole( queue.adminRole, author ) || guild.getOwner().equals( author ) )
        {
            perm = ICommand.admin;
        }

        if( author.equals(owner) )
        {
            perm = ICommand.owner;
        }

        return requiredPerm <= perm;
    }


    private boolean isAboveRole( IRole role, IUser user )
    {
        List<IRole> userRoles = user.getRolesForGuild( guild );

        int roleIndex;

        try
        {
            roleIndex = role.getPosition();
        }
        catch ( NullPointerException e )
        {
            //for first time setup so new user can set mod role

            return true;
        }


        for( IRole roleCheck : userRoles )
        {
            if( roleCheck.getPosition() >= roleIndex )
            {
                return true;
            }
        }

        return false;
    }

    void channelJoin( UserVoiceChannelEvent event )
    {
        IUser user = event.getUser();

        if( event.getVoiceChannel() == queue.sortChannel )
        {
            if( queue.inTeam( 1 , user ) )
            {
                user.moveToVoiceChannel( queue.teamOneChannel );
            }
            else if( queue.inTeam( 2, user ) )
            {
                user.moveToVoiceChannel( queue.teamTwoChannel );
            }

        }
    }

}
