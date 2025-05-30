package facebooknotifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TriggeredPostAlerter {
    private static final String TOKEN_FILE_PATH = "discordbot/bottoken.txt";
    private Scanner scan;
    private String token;
    private JDA jda;

    public TriggeredPostAlerter() throws FileNotFoundException {
        this.scan = new Scanner(new File(TOKEN_FILE_PATH));
        this.token = scan.nextLine();
        this.jda = JDABuilder.createDefault(token).build();
        this.jda.addEventListener(new DiscordListeners());
    }

    public void newPostAlertDiscord(String channelId, String imgtxt, String postId, String postLink, String keyWordCategory) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage("CATEGORY: " + keyWordCategory + "\n" + imgtxt + "\n" + postLink + "\n").queue();
        } else {
            System.out.println("Channel with ID " + channelId + " not found.");
        }
    }
}
