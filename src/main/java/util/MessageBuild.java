package util;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class MessageBuild
{
    public static void builder( MessageChannel channel, String contents )
    {
        channel.sendMessage( contents ).queue();
    }

    public static void reply( MessageChannel channel, Member author, String contents )
    {
        StringBuilder build = new StringBuilder();

        build.append( author.getUser().getAsTag() );

        build.append(", ");

        build.append(contents);

        channel.sendMessage( build ).queue();
    }

    public static void reply( Message msg, String contents )
    {
        User author = msg.getAuthor();

        MessageChannel channel = msg.getChannel();

        StringBuilder build = new StringBuilder();

        build.append( author.getAsTag() );

        build.append(", ");

        build.append( contents );

        channel.sendMessage( build ).queue();
    }
}
