package util;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;

public class Message
{
    /**
     * sends messages
     * @param client IDiscordClient to send with
     * @param message message for channel to reply at
     * @param contents what to send
     */
    public static IMessage builder(IDiscordClient client, IMessage message, String contents)
    {
        try
        {
            return new MessageBuilder(client)
                    .withChannel(message.getChannel())
                    .withContent(contents).build();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * sends messages
     * @param client IDiscordClient to send with
     * @param channelID channel to send to
     * @param contents  what to send
     */
    public static void builder(IDiscordClient client, IChannel channelID, String contents) {
        try
        {
            new MessageBuilder( client )
                    .withChannel( channelID )
                    .withContent( contents ).build();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
