package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.MessageBuild;
import util.QueueHandler;

import java.util.Map;

public class Ban implements ICommand
{
    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        if( msg.getMentions().size() != 0 )
        {
            for( Member user : msg.getMentionedMembers() )
            {
                if(queue.banUser( user ))
                {
                    MessageBuild.reply( msg,  user.getNickname() + "  is now banned from queue");
                }
                else
                {
                    MessageBuild.reply( msg, user.getNickname() + " is already banned from queue");
                }
            }
        }
        else
        {
            MessageBuild.reply( msg, "No users included in message");
        }

    }

    @Override
    public String getName()
    {
        return "ban";
    }

    @Override
    public int getRank()
    {
        return admin;
    }

    @Override
    public String help(int permLevel)
    {
        return "ban:\n" +
                "       bans user from queueing";
    }
}
