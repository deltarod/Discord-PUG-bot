import commands.ICommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.handle.GuildSetupController;
import util.Config;
import org.reflections.Reflections;
import util.MessageBuild;
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

    private Guild guild;

    //owner of bot
    private Member owner;

    //current bot client
    private JDA client;

    //map of commands
    private Map<String, ICommand> cmdMap;

    //queue man queue for server
    public QueueHandler queue;


    CommandHandler( String commandPrefix, Guild guild, JDA client, Config cfg, String owner )
    {
        prefix = commandPrefix;

        this.guild = guild;

        this.client = client;

        this.cfg = cfg;

        this.owner = guild.getMemberById( Long.parseLong( owner ) );

        queue = new QueueHandler( cfg, guild, client, this.owner );

        cmdMap = new HashMap<>();

        Reflections ref = new Reflections("commands");

        //loads commands from the util package
        //Lambda statement!
        ref.getSubTypesOf(ICommand.class).forEach(subclass -> {

            ICommand cmd = null;
            try {
                cmd = subclass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            cmdMap.put( cmd.getName(), cmd );
        });
    }


    /**
     * Checks for correct permissions, then executes command
     * @param command   command to execute
     * @param msg       message of origin
     */
    public void run( String command, Message msg )
    {
        Member author = msg.getMember();

        MessageChannel channel = msg.getChannel();

        String[] commandVar = command.split("\\s+", 2);

        ICommand cmd = cmdMap.get( commandVar[ 0 ] );

        if( cmd == null )
        {
            MessageBuild.reply( channel, author, commandVar[ 0 ] + " is an invalid command" );

            return;
        }

        int permLevel = checkPerms( cmd.getRank(), author );

        if( permLevel >= cmd.getRank() )
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
            MessageBuild.reply( channel, author, "Your power is weak");
        }

        //if prefix gets changed
        prefix = cfg.getProp("prefix");
    }

    private int checkPerms( int requiredPerm, Member author )
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

        return perm;
    }


    private boolean isAboveRole( Role role, Member user )
    {
        List<Role> userRoles = user.getRoles();

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


        for( Role roleCheck : userRoles )
        {
            if( roleCheck.getPosition() >= roleIndex )
            {
                return true;
            }
        }

        return false;
    }

    void channelJoin( GuildVoiceJoinEvent event )
    {
        Member member = event.getMember();

        VoiceChannel channel = event.getChannelJoined();

        if( channel == queue.sortChannel )
        {
            if( queue.inTeam( 1 , member ) )
            {
                guild.moveVoiceMember( member, queue.teamOneChannel ).queue();
            }
            else if( queue.inTeam( 2, member ) )
            {
                guild.moveVoiceMember( member, queue.teamTwoChannel ).queue();
            }

        }
    }

}
