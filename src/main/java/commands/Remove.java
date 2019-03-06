package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Remove implements ICommand
{
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        try
        {
            queue.remove( msg, msg.getMentions().remove(0));
        }
        catch ( IndexOutOfBoundsException e )
        {
            msg.reply("invalid remove statement");
        }
    }

    @Override
    public String help(int permLevel)
    {
        return "remove:\n" +
                "       removes user from queue\n" +
                "       remove(@user)";
    }

    @Override
    public int getRank()
    {
        return mod;
    }


}
