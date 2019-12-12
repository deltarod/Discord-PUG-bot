package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.MessageBuild;
import util.QueueHandler;

import java.util.Map;

public class Unban implements ICommand
{
    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {

        if( msg.getMentions().size() != 0 )
        {
            for( Member user : msg.getMentionedMembers() )
            {
                if(queue.unbanUser( user ))
                {
                    MessageBuild.reply( msg, user.getNickname() + " is now unbanned from queue");
                }
                else
                {
                    MessageBuild.reply( msg, user.getNickname() + " is not banned from queue");
                }
            }
        }
        else
        {
            MessageBuild.reply( msg,"No users included in message");
        }
    }

    @Override
    public String getName()
    {
        return "unban";
    }

    @Override
    public int getRank()
    {
        return admin;
    }

    @Override
    public String help(int permLevel)
    {
        return "unban:\n" +
                "       removes queue ban";
    }
}
