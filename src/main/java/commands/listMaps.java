package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class listMaps implements ICommand
{
    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel)
    {
        try
        {
            switch ( args )
            {
                case "current":
                    queue.listMaps( msg, true );
                    break;
                case "pool":
                    queue.listMaps( msg, false );
                    break;
                default:
                    msg.reply("invalid sub command");
                    break;
            }
        }
        catch ( NullPointerException e )
        {
            msg.reply("Sub command required");
        }



    }

    @Override
    public int getRank()
    {
        return any;
    }

    @Override
    public String getName()
    {
        return "maplist";
    }

    @Override
    public String help(int permLevel)
    {
        return "maplist\n" +
                "       lists current maps in maplist\n" +
                "   Sub Commands:\n" +
                "       current\n" +
                "           maps currently in the list for current games\n" +
                "       pool" +
                "           all the maps in map pool";
    }
}
