package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import util.Config;
import util.MessageBuild;
import util.QueueHandler;

import java.util.Map;

public class Team implements ICommand
{
    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        String[] commandVar;

        Member user;

        boolean team = false;

        try
        {
            commandVar = args.split("\\s+", 2);
        }
        catch ( NullPointerException e )
        {
            MessageBuild.reply( msg, "no arguments");
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
                MessageBuild.reply( msg,"invalid team");
            }
        }
        catch (NullPointerException e )
        {
            MessageBuild.reply( msg,"Please specify a team(1/2)");

            return;
        }

        try
        {
            user = msg.getMentionedMembers().remove(0);
        }
        catch ( IndexOutOfBoundsException e )
        {
            MessageBuild.reply( msg,"no user to add/remove");
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
                MessageBuild.reply( msg,"Invalid subcommand");
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
