package facebooknotifier;

import java.io.IOException;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String chatId = "6841309828";

    public TelegramBot(String botToken) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    /*private void startScheduledMessages() {
        scheduler.scheduleAtFixedRate(this::sendAutomaticMessage, 0, 30, TimeUnit.SECONDS);
    }

    private void sendAutomaticMessage() {
        SendMessage sendMessage = new SendMessage(chatId, "🔔 Automaattinen ilmoitus!");
        sendMessage.setDisableNotification(false);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), ProcessMessages.getImgtxt() + "\nhttps://facebook.com/groups/csgofinland/permalink/" + ProcessMessages.getPostId() + "\n");
            sendMessage.setDisableNotification(false);
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void triggeredPost(String chatId, String imgtxt, String postId) {
        SendMessage sendMessage = new SendMessage(chatId, imgtxt + "\nhttps://facebook.com/groups/csgofinland/permalink/" + postId);
        sendMessage.setDisableNotification(false);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String botToken = GetCredentials.readTelegramApi();
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        try {
            botsApplication.registerBot(botToken, new TelegramBot(botToken));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}