package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Shutdown implements ICommand
{
    @Override
    public String getName()
    {
        return "shutdown";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel)
    {
        client.logout();
    }

    @Override
    public int getRank()
    {
        return owner;
    }

    @Override
    public String help(int permLevel)
    {
        return "shutdown \n" +
                "       shuts down bot";
    }
}
