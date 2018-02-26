package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
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
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten, int permLevel )
    {
        ten.finish(client, msg);
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
