import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class SearchLifeBot extends TelegramLongPollingBot {
    String name="@SkiffLadderGameBot";
    String token="1749369333:AAEQ2ikfg0NWghbHUqvrOXfa4Z0I2J3TZwo";
    DatabaseController database = new DatabaseController();
//    String url="https://36501943c5a0.ngrok.io";

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
//            database.addUser((int) (long) update.getMessage().getFrom().getId());
            User user = database.getUser((int) (long) update.getMessage().getFrom().getId());
            System.out.println(user.id+" "+user.coins+" "+user.timestamp);


            System.out.println(update.getMessage().getChatId()+": "+update.getMessage().getFrom().getId()+": "+update.getMessage().getText());
//            System.out.println(update.getMessage().getText());
            if(update.getMessage().getText().equals("/search")) {


                int timeDif = database.timeLastSearchDif((int) (long)update.getMessage().getFrom().getId());
                System.out.println(timeDif);
                System.out.println(new Timestamp(System.currentTimeMillis()));

                if(timeDif>30||user.id==235733832) {
                    SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
                    message.setChatId(update.getMessage().getChatId().toString());

                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(update.getMessage().getChatId().toString());




                    File dir = new File("src/main/resources/tags");
                    File[] files = dir.listFiles();
                    Random rand = new Random();
                    File file = files[rand.nextInt(files.length)];
                    System.out.println(file);

                    sendPhoto.setPhoto(new InputFile(file));



                    if(file.getPath().contains("microbe")){
                        int p = 15;
                        user.coins+=p;
                        String mes = "Congratulation!\n"+update.getMessage().getFrom().getFirstName()+" found life!\n@"+update.getMessage().getFrom().getUserName()+" gained "+p+" MC.\nThey now have "+user.coins+" MC.";
                        message.setText(mes);
                        System.out.println(mes);
                    }
                    else {
                        int p = 1;
                        user.coins+=p;
                        String mes ="No sign of life.\n"+update.getMessage().getFrom().getUserName()+" gained "+p+" MC.\nThey now have "+user.coins+" MC.";
                        message.setText(mes);
                        System.out.println(mes);
                    }

                    user.timestamp="'"+new Timestamp(System.currentTimeMillis())+"'";
                    database.setUser(user);

                    try {
                        execute(sendPhoto);
                        execute(message); // Call method to send the messageuser.timestamp="'"+new Timestamp(System.currentTimeMillis()) +"'";
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
                    message.setChatId(update.getMessage().getChatId().toString());
                    System.out.println("mes to "+update.getMessage().getChatId().toString());
                    String mes ="Try later. [Once per 30 second]";
                    message.setText(mes);
                    System.out.println(mes);
                    try {
                        execute(message); // Call method to send the message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                }



            }
//            else{
//                SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
//                message.setChatId(update.getMessage().getChatId().toString());
//                System.out.println("mes to "+update.getMessage().getChatId().toString());
//                message.setText("Unknown command");
//                try {
//                    execute(message); // Call method to send the message
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }



        }
    }



}
