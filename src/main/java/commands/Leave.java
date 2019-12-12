package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Leave implements ICommand
{
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public void run(JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel ) {
        queue.leave( msg );
    }

    @Override
    public String help(int permLevel)
    {
        return "leave:\n" +
                "       Leaves the queue, or the in line queue";
    }

    @Override
    public int getRank()
    {
        return any;
    }
}
