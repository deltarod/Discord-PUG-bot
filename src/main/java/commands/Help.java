package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
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
    public void run( IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten, int permLevel  )
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

        msg.getAuthor().getOrCreatePMChannel().sendMessage( stringBuilder.toString() );
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
