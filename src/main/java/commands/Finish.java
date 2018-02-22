package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.TenManQueue;

import java.util.Map;

public class Finish implements ICommand
{
    @Override
    public String getName()
    {
        return "finish";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten)
    {
        ten.finish(client, msg);
    }

    @Override
    public String toString()
    {
        return "finish: \n" +
                "       Finishes current 10 man";
    }
}
