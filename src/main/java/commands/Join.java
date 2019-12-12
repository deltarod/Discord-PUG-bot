package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Join implements ICommand
{
    @Override
    public String getName()
    {
        return "join";
    }

    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        queue.join( client, msg );
    }

    @Override
    public int getRank() {
        return any;
    }

    @Override
    public String help(int permLevel)
    {
        return "join: \n" +
                "       Joins current queue";
    }
}
