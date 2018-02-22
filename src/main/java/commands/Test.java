package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.Message;
import util.TenManQueue;

import java.util.Map;

public class Test implements ICommand
{
    @Override
    public String getName()
    {
        return "test";
    }

    @Override
    public void run( IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, TenManQueue ten )
    {
        Message.builder(client, msg, "hi");
    }

    @Override
    public String toString() {
        return "test: \n" +
                "       pls ignore commanderino plsorino";
    }
}
