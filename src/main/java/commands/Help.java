package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Help implements ICommand
{

    @Override
    public String getName()
    {
        return "help";
    }

    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel  )
    {


        String prefix = cfg.getProp( "prefix" );

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("```\n");

        stringBuilder.append("How to use subcommands: \n   ");

        stringBuilder.append(prefix);

        stringBuilder.append("(command) (subcommand) (arguments)\n\n");

        stringBuilder.append("Commands:\n");



        for( ICommand cmd : cmdMap.values() )
        {
            if( permLevel >= cmd.getRank() )
            {
                stringBuilder.append(prefix);

                stringBuilder.append(cmd.help( permLevel ));

                stringBuilder.append("\n\n");
            }
        }

        stringBuilder.append("```");

        msg.getAuthor().openPrivateChannel().complete().sendMessage( stringBuilder ).queue();

    }

    @Override
    public int getRank() {
        return any;
    }

    @Override
    public String help( int permLevel )
    {
        return "help:\n" +
                "       Lists all commands";
    }
}
