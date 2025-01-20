package facebooknotifier;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordListeners extends ListenerAdapter {
    public void onReady(@Nonnull ReadyEvent event) {
        JDA jda = event.getJDA();
        for (Guild guild : jda.getGuilds()) {
            for (TextChannel channel : guild.getTextChannels()) {
                channel.sendMessage("HELLO WORLDS :D").queue();
            }
        }
    }
}
