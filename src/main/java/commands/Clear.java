package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;
import java.util.Map;

public class Clear implements ICommand
{
    @Override
    public String getName()
    {
        return "clear";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel)
    {
        queue.clear( msg );
    }

    @Override
    public int getRank()
    {
        return mod;
    }

    @Override
    public String help( int permLevel ) {
        return "clear \n" +
                "       clears current queue";
    }
}
