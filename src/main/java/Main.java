import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {


        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(new SkiffLadderBot());
            SetWebhook setWebhook = new SetWebhook();
            setWebhook.setUrl("https://e9dc08051862.ngrok.io");

            botsApi.registerBot(new SkiffLadderWebhook(),setWebhook);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
