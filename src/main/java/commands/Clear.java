package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
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
    public void run(JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        queue.clear( msg );
    }

    @Override
    public int getRank()
    {
        return admin;
    }

    @Override
    public String help( int permLevel ) {
        return "clear \n" +
                "       clears current queue";
    }
}
