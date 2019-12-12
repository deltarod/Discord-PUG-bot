package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.MessageBuild;
import util.QueueHandler;

import java.util.Map;

public class MapList implements ICommand
{
    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
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
                case "mode":
                    queue.viewMode( msg );
                    break;
                default:
                    MessageBuild.reply( msg, "invalid sub command");
                    break;
            }
        }
        catch ( NullPointerException e )
        {
            MessageBuild.reply( msg, "Sub command required");
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
                "       pool\n" +
                "           all the maps in map pool\n" +
                "       mode\n" +
                "           displays current mode";
    }
}
