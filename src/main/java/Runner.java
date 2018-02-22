import sx.blah.discord.api.*;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.*;
import util.Config;

/**
 * Runner function for the bot
 */

public class Runner
{

    private static String botConfig = "botCFG.properties";

    /**
     * Creates new discord client
     *
     * @param token     secret token for login
     * @param login     bool to login or not
     * @return          new discord client
     */
    public static IDiscordClient createClient(String token, boolean login) // Returns a new instance of the Discord client
    {

        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance

        clientBuilder.withToken( token ); // Adds the login info to the builder

        try
        {
            if ( login )
            {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            }
            else
            {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        }
        catch (DiscordException e) // This is thrown if there was a problem building the client
        {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Main function, starts the bot
     * @param args  should be null, no args gonna be required
     */
    public static void main( String[] args )
    {
        String token;

        Config cfg = new Config( botConfig );

        token = cfg.getToken();

        IDiscordClient client = createClient( token, true );

        EventDispatcher dispatcher = client.getDispatcher();

        dispatcher.registerListener( new Listener( client ) );
    }
}
