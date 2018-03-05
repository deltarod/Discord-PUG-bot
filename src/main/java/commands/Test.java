package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Test implements ICommand
{
    @Override
    public String getName()
    {
        return "test";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {

    }

    @Override
    public int getRank()
    {
        return owner;
    }

    @Override
    public String help( int permLevel )
    {
        return "test: \n" +
                "       pls ignore commanderino plsorino";
    }

}
