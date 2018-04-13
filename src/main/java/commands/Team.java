package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Team implements ICommand
{
    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel)
    {
        String[] commandVar;

        boolean team = false;

        IUser user = msg.getMentions().remove(0);

        try
        {
            commandVar = args.split("\\s+", 2);
        }
        catch ( NullPointerException e )
        {
            msg.reply("no arguments");
            return;
        }

        try
        {
            if (commandVar[1].equals("1"))
            {
                team = true;
            }
            else if (commandVar[1].equals("2"))
            {
                team = false;
            }
            else
            {
                msg.reply("invalid team");
            }
        }
        catch (NullPointerException e )
        {
            msg.reply("Please specify a team(1/2)");

            return;
        }


        switch ( commandVar[0] )
        {
            case "add":
                queue.addToTeam(team, user, msg );
                break;
            case "remove":
                queue.removeFromTeam( team, user, msg );
                break;
            default:
                msg.reply("Invalid subcommand");
        }
    }

    @Override
    public int getRank()
    {
        return admin;
    }

    @Override
    public String help(int permLevel)
    {
        return "team SubCommand:(add/remove) Team:(1/2) user\n";
    }

    @Override
    public String getName()
    {
        return "team";
    }
}
