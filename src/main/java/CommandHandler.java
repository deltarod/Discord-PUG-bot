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
    private QueueHandler queue;



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

        //if user is mod of guild
        else if( isMod( queue.modRole, guild, author ) )
        {
            permLevel = ICommand.mod;
        }

        //if user is owner of bot or guild
        if( author.equals( owner ) || msg.getGuild().getOwner().equals( author ) )
        {
            permLevel = ICommand.owner;
        }

        if( permLevel < cmd.getRank() )
        {
            msg.reply("Your power is weak");

            return;
        }

        try
        {
            cmd.run( client, commandVar[1], msg, cfg, cmdMap, queue, permLevel );
        }
        //if no args
        catch ( ArrayIndexOutOfBoundsException e )
        {
            cmd.run( client, null, msg, cfg, cmdMap, queue, permLevel );
        }

        //if prefix gets changed
        prefix = cfg.getProp("prefix");
    }


    private boolean isMod( IRole modRole, IGuild guild, IUser user )
    {
        List<IRole> userRoles = user.getRolesForGuild( guild );

        int modIndex;

        try
        {
            modIndex = modRole.getPosition();
        }
        catch ( NullPointerException e )
        {
            //for first time setup so new user can set mod role

            return true;
        }


        for( IRole role : userRoles )
        {
            if( modIndex >= role.getPosition() && role.getPosition() != 0 )
            {
                return true;
            }
        }

        return false;
    }

    void channelJoin( UserVoiceChannelEvent event )
    {
        IUser user = event.getUser();

        if( event.getVoiceChannel() == QueueHandler.sortChannel )
        {
            if( queue.inTeam( 1 , user ) )
            {
                user.moveToVoiceChannel( QueueHandler.teamOneChannel );
            }
            else if( queue.inTeam( 2, user ) )
            {
                user.moveToVoiceChannel( QueueHandler.teamTwoChannel );
            }

        }
    }
}
