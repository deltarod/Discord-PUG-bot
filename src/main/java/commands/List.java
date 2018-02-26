package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import util.Config;
import util.TenManQueue;

import java.util.Map;

public class List implements ICommand
{
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten, int permLevel )
    {
        ten.listUsersInQueue( msg );
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
