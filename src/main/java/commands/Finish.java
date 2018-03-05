package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Finish implements ICommand
{
    @Override
    public String getName()
    {
        return "finish";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        queue.finish(client, msg);
    }

    @Override
    public String help(int permLevel)
    {
        return "finish: \n" +
                "       Finishes current 10 man\n" +
                "       PLEASE DONT FINISH EARLY";
    }

    @Override
    public int getRank() {
        return any;
    }
}
