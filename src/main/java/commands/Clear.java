package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.TenManQueue;

import java.util.Map;

public class Clear implements ICommand
{
    @Override
    public String getName()
    {
        return "clear";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten, int permLevel)
    {

    }

    @Override
    public int getRank()
    {
        return mod;
    }

    @Override
    public String help( int permLevel ) {
        return "Clear \n" +
                "       clears current queue";
    }
}
