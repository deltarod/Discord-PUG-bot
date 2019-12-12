package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
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
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        client.shutdown();
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
