package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import util.Config;
import util.TenManQueue;

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
    public String getName();

    /**
     * runs command
     * @param client    current client
     * @param args      args for command
     * @param msg       msg containing command
     * @param cfg       cfg of guild
     * @param cmdMap    map of all commands
     * @param ten       tenmanqueue
     * @param permLevel permission level of user executing command
     */
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten, int permLevel );

    /**
     * gets rank requirement of command
     * @return any, mod, owner
     */
    public int getRank();

    /**
     * gets the string to print out help commands
     * @param permLevel
     */
    public String help( int permLevel );

}
