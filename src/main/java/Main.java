import com.sun.net.httpserver.HttpServer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.TimeZone;

public class Main {
    public static void main(String[] args) throws IOException {


//        TimeZone.setDefault(TimeZone.getTimeZone("Russia/Moscow"));

        SkiffLadderBot Bot = new SkiffLadderBot();

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(Bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }




    }
}
