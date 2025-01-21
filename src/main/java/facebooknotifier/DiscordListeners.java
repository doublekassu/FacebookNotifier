package facebooknotifier;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordListeners extends ListenerAdapter {
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        System.out.println("Bot is ready and connected to Discord!");
    }
}
