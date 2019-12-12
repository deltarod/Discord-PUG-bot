import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import util.Config;

import javax.security.auth.login.LoginException;

/**
 * Runner function for the bot
 */

public class Runner
{

    private static String botConfig = "botCFG.properties";


    /**
     * Main function, starts the bot
     * @param args  should be null, no args gonna be required
     */
    public static void main( String[] args )
    {
        String token, owner;

        JDA client;

        Config cfg = new Config( botConfig );

        token = cfg.getToken();

        owner = cfg.getOwner();

        try
        {
            client = new JDABuilder(token).build();

            client.addEventListener( new Listener( client, owner, cfg ) );

            client.setAutoReconnect( true );
        }
        catch (LoginException e )
        {
            e.printStackTrace();
        }
    }
}
