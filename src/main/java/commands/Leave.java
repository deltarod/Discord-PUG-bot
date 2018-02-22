package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.TenManQueue;

import java.util.Map;

public class Leave implements ICommand
{
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten) {
        ten.leave( client, msg );
    }

    @Override
    public String toString() {
        return "leave:\n" +
                "       Leaves the queue, or the in line queue";
    }
}
