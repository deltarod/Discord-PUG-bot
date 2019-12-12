package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.MessageBuild;
import util.QueueHandler;

import java.util.Map;

public class View implements ICommand
{
    @Override
    public int getRank()
    {
        return owner;
    }

    @Override
    public String getName()
    {
        return "view";
    }

    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        //ugly stuff
        StringBuilder str = new StringBuilder("```\n");

        str.append("prefix: ");

        str.append(cfg.getProp("prefix"));

        str.append("\n");

        str.append("modID: ");

        str.append(cfg.getProp("mod"));

        str.append("\n");

        str.append("adminID: ");

        str.append(cfg.getProp("admin"));

        str.append("\n");

        str.append("queueID: ");

        str.append(cfg.getProp("queuetext"));

        str.append("\n");

        str.append("sortID: ");

        str.append(cfg.getProp("sort"));

        str.append("\n");

        str.append("lobbyID: ");

        str.append(cfg.getProp("lobby"));

        str.append("\n");

        str.append("onechannelID: ");

        str.append(cfg.getProp("onechannel"));

        str.append("\n");

        str.append("twochannelID: ");

        str.append(cfg.getProp("twochannel"));

        str.append("\n");

        str.append("team size: ");

        str.append(cfg.getProp("teamsize"));

        str.append("\n");

        str.append("maps: ");

        str.append(cfg.getProp("maps"));

        str.append("\n");

        str.append("map selection: ");

        str.append(cfg.getProp("mode"));

        str.append("\n");

        str.append("map selection bool: ");

        str.append( queue.modeBool());

        str.append("\n");

        str.append("```");



        MessageBuild.reply( msg, str.toString() );
    }

    @Override
    public String help( int permLevel )
    {
        return  "view: \n" +
            "       used to view all the config options\n";
    }
}
