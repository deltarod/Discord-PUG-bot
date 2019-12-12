package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Finish implements ICommand
{
    @Override
    public String getName()
    {
        return "finish";
    }

    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        queue.finish();
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
        return mod;
    }
}
