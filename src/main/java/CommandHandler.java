import commands.ICommand;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import util.Config;
import org.reflections.Reflections;
import util.TenManQueue;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler
{
    public String prefix;

    private Config cfg;

    private IGuild guild;

    private IDiscordClient client;

    private Reflections ref;

    private Map<String, ICommand> cmdMap;

    private TenManQueue ten;



    public CommandHandler( String commandPrefix, IGuild guild, IDiscordClient client, Config cfg )
    {
        prefix = commandPrefix;

        this.guild = guild;

        this.client = client;

        this.cfg = cfg;

        ten = new TenManQueue( cfg, guild, client );

        cmdMap = new HashMap<>();

        ref = new Reflections("commands");

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

    public void run(String command, IMessage msg )
    {
        String[] commandVar = command.split("\\s+", 2);

        ICommand cmd = cmdMap.get( commandVar[ 0 ] );

        if( cmd == null )
        {
            msg.reply( commandVar[ 0 ] + " is an invalid command");

            return;
        }

        try
        {
            cmd.run( client, commandVar[1], msg, cfg, cmdMap, ten );
        }
        catch ( ArrayIndexOutOfBoundsException e )
        {
            cmd.run( client, null, msg, cfg, cmdMap, ten );
        }

        //if prefix gets changed
        prefix = cfg.getProp("prefix");
    }

    public void channelJoin( UserVoiceChannelEvent event )
    {
        IUser user = event.getUser();

        if( event.getVoiceChannel() == TenManQueue.sortChannel )
        {
            if(user.hasRole( TenManQueue.teamOneRole ))
            {
                user.moveToVoiceChannel( TenManQueue.teamOneChannel );
            }
            if(user.hasRole( TenManQueue.teamTwoRole ))
            {
                user.moveToVoiceChannel( TenManQueue.teamTwoChannel );
            }
        }
    }
}
