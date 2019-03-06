package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class Ban implements ICommand
{
    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel)
    {
        if( msg.getMentions().size() != 0 )
        {
            for(IUser user : msg.getMentions())
            {
                if(queue.banUser( user ))
                {
                    msg.reply( user.getDisplayName(msg.getGuild()) + "  is now banned from queue");
                }
                else
                {
                    msg.reply(user.getDisplayName(msg.getGuild()) + " is already banned from queue");
                }
            }
        }
        else
        {
            msg.reply("No users included in message");
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
