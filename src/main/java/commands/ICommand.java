package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;


/**
 * Used for different commands
 */
public interface ICommand
{
    int any = 0, mod = 1, owner = 2;

    /**
     * gets name of command, used for running the commands
     * @return name of command
     */
    String getName();

    /**
     * runs command
     * @param client    current client
     * @param args      args for command
     * @param msg       msg containing command
     * @param cfg       cfg of guild
     * @param cmdMap    map of all commands
     * @param queue       tenmanqueue
     * @param permLevel permission level of user executing command
     */
    void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel );

    /**
     * gets rank requirement of command
     * @return any, mod, owner
     */
    int getRank();

    /**
     * gets the string to print out help commands
     * @param permLevel permissions of current user
     */
    String help( int permLevel );

}
