package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Queue implements ICommand
{
    @Override
    public String getName()
    {
        return "queue";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
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
        return "queue: \n" +
                "       Joins current queue";
    }
}

