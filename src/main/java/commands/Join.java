package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Join implements ICommand
{
    @Override
    public String getName()
    {
        return "join";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        System.out.println("command: "+ msg.getGuild().getName());

        queue.join( client, msg );
    }

    @Override
    public int getRank() {
        return any;
    }

    @Override
    public String help(int permLevel)
    {
        return "join: \n" +
                "       Joins current queue";
    }
}
