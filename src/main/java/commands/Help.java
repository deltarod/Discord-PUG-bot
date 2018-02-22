package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.Message;
import util.TenManQueue;

import java.util.Map;

public class Help implements ICommand
{

    @Override
    public String getName()
    {
        return "help";
    }

    @Override
    public void run( IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten )
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


            stringBuilder.append(prefix);

            stringBuilder.append( cmd );

            stringBuilder.append( "\n\n" );
        }

        stringBuilder.append("```");

        Message.builder( client, msg, stringBuilder.toString() );
    }

    @Override
    public String toString()
    {
        return "help:\n" +
                "       Lists all commands";
    }
}
