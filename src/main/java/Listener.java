import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.*;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.obj.*;
import util.Config;
import util.Message;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener class for events
 */
public class Listener
{

    private IDiscordClient client;

    private String owner;

    private Map<IGuild, CommandHandler> guildMap;

    /**
     * Constructor for the listener
     * @param client IDiscordClient for doing things
     */
    Listener ( IDiscordClient client, String owner, Config cfg )
    {
        this.client = client;

        this.owner = owner;

        guildMap = new HashMap<>();
    }

    /**
     * Handles messages
     */
    @EventSubscriber
    public void onMessageReceivedEvent( MessageReceivedEvent event )
    {
        String prefix, messageStr;

        IGuild guild = event.getGuild();

        IMessage msg = event.getMessage();

        messageStr = msg.getContent();

        CommandHandler cmd = guildMap.get( guild );

        if( messageStr.startsWith( cmd.prefix ) )
        {
            cmd.run( messageStr.substring( cmd.prefix.length() ), msg );
        }
    }

    /**
     * handles startup
     */
    @EventSubscriber
    public void onReadyEvent( ReadyEvent event ) // This method is called when the ReadyEvent is dispatched
    {
        client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, " 10 mans, now with random maps!");

        System.out.println( "started successfully" );
    }

    /**
     * also handles some startup
     */
    @EventSubscriber
    public void onGuildJoin( GuildCreateEvent event )
    {
        String prefix;

        CommandHandler cmd;

        IGuild guild = event.getGuild();

        Config cfg = new Config( "GuildConfigs/", guild.getStringID() + ".properties");

        prefix = cfg.getProp( "prefix" );

        if( prefix == null )
        {
            prefix = "?";

            cfg.setProp( "prefix", prefix );

            cmd = new CommandHandler( prefix, guild, client, cfg, owner );

            guildMap.put( guild, cmd );

            newGuild( guild );
        }
        else
        {
            cmd = new CommandHandler( prefix, guild, client, cfg, owner );

            guildMap.put( guild, cmd );
        }

    }

    /**
     * Handles users joining a channel, mostly for sorting teams
     * @param event user join event
     */
    @EventSubscriber
    public void userJoinChannel( UserVoiceChannelEvent event )
    {
        CommandHandler cmd = guildMap.get(event.getGuild());

        cmd.channelJoin( event );
    }

    /**
     * Figures out what to do on join of a new server
     * @param guild new guild to be setup
     */
    private void newGuild( IGuild guild )
    {
        IChannel defaultChannel = guild.getDefaultChannel();

        String newMessage = "Hiya! By default the command prefix is ?, you can set that with ?setup prefix (prefix), or find out what i can do with ?help";

        Message.builder( client, defaultChannel, newMessage );
    }

}
