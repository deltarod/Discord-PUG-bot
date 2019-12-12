package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.QueueHandler;

import java.util.Map;


/**
 * Used for different commands
 */
public interface ICommand
{
    int any = 0, mod = 1, admin = 2, owner = 3;

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
    void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel );

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
