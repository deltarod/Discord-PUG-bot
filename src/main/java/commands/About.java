package commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import util.Config;
import util.QueueHandler;

import java.util.Map;

public class About implements ICommand
{
    @Override
    public void run( IDiscordClient client, String args, IMessage msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        String time = getUptimeString( queue.startTime );

        EmbedBuilder builder = new EmbedBuilder();

        builder.withColor(255,0,0);

        builder.withAuthorName("Delta's Queue bot");
        builder.withAuthorIcon("https://i.imgur.com/IkKQcS9.png");

        builder.appendField("Bot Dev", "deltarod#9654", true);
        builder.appendField("Uptime", time, true);
        builder.appendField("Github", "[Queue Bot](https://github.com/deltarod/Discord-PUG-bot)", true);
        builder.appendField("Like the bot? ", "[Buy me a beer!](https://www.paypal.me/deltarod)", true);
        builder.appendField("Version", "1.0", true);

        msg.getChannel().sendMessage(builder.build());
    }

    @Override
    public String getName()
    {
        return "about";
    }

    @Override
    public int getRank()
    {
        return any;
    }

    @Override
    public String help( int permLevel )
    {
        return "about:\n" +
                "       Gives information about the bot";
    }

    private String getUptimeString( long startTime )
    {
        long seconds, minutes, hours;

        long currentTime = System.currentTimeMillis();

        long uptime = currentTime - startTime;

        String time = "";

        seconds = uptime/1000;

        minutes = seconds/60;

        hours = minutes/60;

        //hours
        if( hours < 10 )
        {

            time += "0";

            time += hours;

        }
        else
        {
            time += hours;
        }

        time += ":";

        //minutes
        if( minutes < 10 )
        {

            time += "0";

            time += minutes%60;

        }
        else
        {
            time += minutes%60;
        }

        time += ":";

        //seconds
        if( seconds < 10 )
        {

            time += "0";

            time += seconds%60;

        }
        else
        {
            time += seconds%60;
        }

        return time;
    }


}
