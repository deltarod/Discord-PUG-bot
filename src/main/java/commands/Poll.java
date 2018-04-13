package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import util.Config;
import util.QueueHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Poll implements ICommand
{
    @Override
    public String getName()
    {
        return "poll";
    }

    @Override
    public void run(IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {

        try
        {
            //Y emoji
            msg.addReaction(ReactionEmoji.of("\uD83C\uDDFE"));

            TimeUnit.MILLISECONDS.sleep(200);

            //N emoji
            msg.addReaction(ReactionEmoji.of("\uD83C\uDDF3"));

            TimeUnit.MILLISECONDS.sleep(200);

            //shrug emote
            msg.addReaction(ReactionEmoji.of("\uD83E\uDD37"));
        }
        catch (Exception e )
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getRank() {
        return any;
    }

    @Override
    public String help(int permLevel)
    {
        return "poll: \n" +
                "       adds reactions for a poll";
    }
}
