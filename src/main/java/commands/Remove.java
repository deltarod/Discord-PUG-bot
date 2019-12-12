package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.MessageBuild;
import util.QueueHandler;

import java.util.Map;

public class Remove implements ICommand
{
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        try
        {
            queue.remove( msg, msg.getMentionedMembers().remove(0));
        }
        catch ( IndexOutOfBoundsException e )
        {
            MessageBuild.reply( msg, "invalid remove statement");
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
