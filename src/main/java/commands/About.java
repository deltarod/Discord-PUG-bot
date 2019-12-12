package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import util.Config;
import util.QueueHandler;

import java.awt.*;
import java.util.Map;

public class About implements ICommand
{
    @Override
    public void run( JDA client, String args, Message msg, Config cfg, Map<String, ICommand> cmdMap, QueueHandler queue, int permLevel )
    {
        String time = getUptimeString( queue.startTime );

        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(new Color(255,0,0));

        builder.setAuthor("Delta's Queue bot");
        builder.setImage("https://i.imgur.com/IkKQcS9.png");

        builder.addField("Bot Dev", "deltarod#9654", true);
        builder.addField("Uptime", time, true);
        builder.addField("Github", "[Queue Bot](https://github.com/deltarod/Discord-PUG-bot)", true);
        builder.addField("Like the bot? ", "[Buy me a beer!](https://www.paypal.me/deltarod)", true);
        builder.addField("Version", "1.1.1", true);

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
