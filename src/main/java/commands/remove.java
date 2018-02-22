package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.TenManQueue;

import java.util.Map;

public class remove implements ICommand
{
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten) {
        ten.remove( msg, msg.getMentions().remove(0));
    }

    @Override
    public String toString() {
        return "remove:\n" +
                "       removes user from queue\n" +
                "       (prefix)(remove)(@user)";
    }
}
