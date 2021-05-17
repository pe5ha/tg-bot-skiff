import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SkiffLadderWebhook extends TelegramWebhookBot {
    SkiffLadderWebhook(){
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId("235733832");
        message.setText("initial");
        System.out.println("initial");
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "@SkiffLadderGameBot";
    }

    @Override
    public String getBotToken() {
        return "1749369333:AAEe_JrNELDkAwYmIWZinBTKHmJOa7LAddM";
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId("235733832");
        message.setText("send");
        System.out.println("trysend");
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return "https://e9dc08051862.ngrok.io";
    }
}
