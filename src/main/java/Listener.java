import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import util.Config;
import util.MessageBuild;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener class for events
 */
public class Listener extends ListenerAdapter {

    private JDA client;

    private String owner;

    private Map<Guild, CommandHandler> guildMap;

    /**
     * Constructor for the listener
     * @param client IDiscordClient for doing things
     */
    Listener ( JDA client, String owner, Config cfg )
    {
        this.client = client;

        this.owner = owner;

        guildMap = new HashMap<>();
    }

    /**
     * Handles messages
     */
    @SubscribeEvent
    public void onMessageReceived( MessageReceivedEvent event )
    {
        if( event.getAuthor().isBot() ) return; //replying to other bots = bad

        String prefix, messageStr;

        Guild guild = event.getGuild();

        Message msg = event.getMessage();

        messageStr = msg.getContentDisplay();

        CommandHandler cmd = guildMap.get( guild );

        if( messageStr.startsWith( cmd.prefix ) )
        {
            cmd.run( messageStr.substring( cmd.prefix.length() ), msg );
        }
    }


    /**
     * handles startup
     */
    @SubscribeEvent
    public void onReady( ReadyEvent event ) // This method is called when the ReadyEvent is dispatched
    {
        System.out.println("Guilds: ");

        for( Guild guild : guildMap.keySet() )
        {
            System.out.println( guild.getName() );
        }

        System.out.println( "started successfully" );
    }

    /**
     * also handles some startup
     */
    @SubscribeEvent
    public void onGuildReady(GuildReadyEvent event )
    {
        String prefix;

        CommandHandler cmd;

        Guild guild = event.getGuild();

        Config cfg = new Config( "GuildConfigs/", guild.getId() + ".properties");

        prefix = cfg.getProp( "prefix" );

        //if the guild just joined is new
        //if the guild just joined is new
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
    @SubscribeEvent
    public void onGuildVoiceJoin( GuildVoiceJoinEvent event )
    {
        CommandHandler cmd = guildMap.get(event.getGuild());

        cmd.channelJoin( event );
    }

    /**
     * Removes users from queue if banned/kicked/leaved from server
     * @param event ban event
     */
    @SubscribeEvent
    public void onGuildVoiceLeave( GuildVoiceLeaveEvent event )
    {
        CommandHandler cmd = guildMap.get( event.getGuild() );

        cmd.queue.forceRemove( event.getEntity() );
    }


    /**
     * Figures out what to do on join of a new server
     * @param guild new guild to be setup
     */
    private void newGuild( Guild guild )
    {
        TextChannel defaultChannel = guild.getDefaultChannel();

        if( defaultChannel == null )
        {
            System.out.println("Default Channel not assigned");
        }

        String newMessage = "Hiya! By default the command prefix is ?, you can set that with ?setup prefix (prefix), or find out what i can do with ?help";

        MessageBuild.builder( defaultChannel, newMessage );
    }

}
