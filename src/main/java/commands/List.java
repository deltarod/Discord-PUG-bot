package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class List implements ICommand
{
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        queue.listUsersInQueue( msg );
    }


    @Override
    public String help(int permLevel)
    {
        return "list:\n" +
                "       lists current users in queue";
    }

    @Override
    public int getRank()
    {
        return any;
    }

}
