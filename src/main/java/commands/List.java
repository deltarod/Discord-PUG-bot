package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class List implements ICommand
{
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        queue.listUsersInQueue( msg );
    }


    @Override
    public String help(int permLevel)
    {
        return "list:\n" +
                "       lists current users in queue(could change to number of people in queue as well)";
    }

    @Override
    public int getRank()
    {
        return any;
    }

}
