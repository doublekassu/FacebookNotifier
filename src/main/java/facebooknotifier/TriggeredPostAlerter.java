package facebooknotifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class TriggeredPostAlerter {
    private static final String TOKEN_FILE_PATH = "discordbot/bottoken.txt";

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(TOKEN_FILE_PATH));
        String token = scan.nextLine();
        JDA jda = JDABuilder.createDefault(token).build();

        jda.addEventListener(new DiscordListeners());
    }
}
