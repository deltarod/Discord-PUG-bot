import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.*;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import util.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener class for events
 */
public class Listener
{

    IDiscordClient client;

    private static String guildCfg = "guildCfg.properties";

    private Map<IGuild, String> guildMap;

    Config cfg;

    /**
     * Constructor for the listener
     * @param client IDiscordClient for doing things
     */
    public Listener ( IDiscordClient client )
    {
        this.client = client;

        guildMap = new HashMap<IGuild, String>();

        cfg = new Config( guildCfg );
    }

    /**
     * Handles messages
     */
    @EventSubscriber
    public void onMessageReceivedEvent( MessageReceivedEvent event )
    {
        // TODO: 2/22/2018 command handling
    }

    /**
     * handles startup
     */
    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) // This method is called when the ReadyEvent is dispatched
    {

        // TODO: 2/22/2018 test .online to see if it is server based or not
        //client.online("Type +join to queue in proper channel");
        //foo(); // Will be called!

        System.out.println( "started successfully" );
    }

    /**
     * also handles some startup
     */
    @EventSubscriber
    public void onGuildJoin( GuildCreateEvent event )
    {
        String prefix;

        IGuild guild = event.getGuild();

        prefix = cfg.getPrefix( guild.getStringID() );

        if( prefix == "NEWSERVER" )
        {
            guildMap.put( guild, "?" );

            newGuild( guild );
        }
        else
        {
            guildMap.put( guild, prefix );
        }

        // TODO: 2/22/2018 GuildBasedCommands
    }

    /**
     * Figures out what to do on join of a new server
     * @param guild
     */
    private void newGuild( IGuild guild )
    {
        IChannel defaultChannel = guild.getDefaultChannel();

        String newMessage = "Hiya! By default the command prefix is ?, you can set that with (insert command here), or find out what i can do with ?help";

        Message.builder( client, defaultChannel, newMessage );
    }

}
