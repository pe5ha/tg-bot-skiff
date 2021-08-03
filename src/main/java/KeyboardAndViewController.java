import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class KeyboardAndViewController {



    public void setKeyboard(){



    }


    public SendMessage sendGameKeyboard(long chatId, int points) {
        InlineKeyboardMarkup inlineKeyboardMarkup = setGameKeyboard(points);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(""+chatId);
        sendMessage.setText("░░░░░░░░░░░░░░░░░░░░░\n" +
                "███░░░░░░░░░░░░░░░███\n" +
                "██████░░░░░░░░░██████\n" +
                "█████████░∆░█████████\n" +
                "█████████████████████\n" +
                "Вы ходите | Соперник ходит\n" +
                "*[50]*–*[50]*");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.enableMarkdown(true);

        return sendMessage;
    }



    public EditMessageReplyMarkup editGameKeyboard(long chatId,int messageId,int points){

        EditMessageReplyMarkup replyMarkup = new EditMessageReplyMarkup();
        replyMarkup.setChatId(""+chatId);
        replyMarkup.setMessageId(messageId);
        replyMarkup.setReplyMarkup(setGameKeyboard(points));


        return replyMarkup;
    }







    public InlineKeyboardMarkup setGameKeyboard(int points){
       if(points==0) return null;
       int rows = points/8+1;

       List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
       for (int i = 0; i < rows; i++) {
           List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
           for (int j = 0; j < 8; j++) {
               InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
               if(i*8+j>points) {
                   inlineKeyboardButton.setText(" ");
                   inlineKeyboardButton.setCallbackData(""+(-(i*8+j)));
               }
               else {
                   inlineKeyboardButton.setText(""+(i*8+j));
                   inlineKeyboardButton.setCallbackData(""+(i*8+j));
               }
               keyboardButtonsRow.add(inlineKeyboardButton);
           }
           rowList.add(keyboardButtonsRow);
       }

       InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
       inlineKeyboardMarkup.setKeyboard(rowList);

       return inlineKeyboardMarkup;
   }

   public InlineKeyboardMarkup buildMessageKeyboard (List<List<AbstractMap.SimpleEntry<String,String>>> arrayButtonNames){
       List<List<InlineKeyboardButton>> rowsList = new ArrayList<>();
       for (int i = 0; i < arrayButtonNames.size(); i++) {
           List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
           for (int j = 0; j < arrayButtonNames.get(i).size(); j++) {
               InlineKeyboardButton button = new InlineKeyboardButton();
               button.setText(arrayButtonNames.get(i).get(j).getValue());
               button.setCallbackData(arrayButtonNames.get(i).get(j).getKey());
               buttonsRow.add(button);
           }
           rowsList.add(buttonsRow);
       }
       InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
       inlineKeyboardMarkup.setKeyboard(rowsList);
       return inlineKeyboardMarkup;
   }





    public String gameStateImg(int state){

        return switch (state) {
            case -3 -> "░░░░░░░░░░░░░░░░░░░∆░\n" +
                    "███░░░░░░░░░░░░░░░███\n" +
                    "██████░░░░░░░░░██████\n" +
                    "█████████░░░█████████\n" +
                    "█████████████████████";
            case -2 -> "░░░░░░░░░░░░░░░░░░░░░\n" +
                    "███░░░░░░░░░░░░░∆░███\n" +
                    "██████░░░░░░░░░██████\n" +
                    "█████████░░░█████████\n" +
                    "█████████████████████";
            case -1 -> "░░░░░░░░░░░░░░░░░░░░░\n" +
                    "███░░░░░░░░░░░░░░░███\n" +
                    "██████░░░░░░░∆░██████\n" +
                    "█████████░░░█████████\n" +
                    "█████████████████████";
            case 1 -> "░░░░░░░░░░░░░░░░░░░░░\n" +
                    "███░░░░░░░░░░░░░░░███\n" +
                    "██████░∆░░░░░░░██████\n" +
                    "█████████░░░█████████\n" +
                    "█████████████████████";
            case 2 -> "░░░░░░░░░░░░░░░░░░░░░\n" +
                    "███░∆░░░░░░░░░░░░░███\n" +
                    "██████░░░░░░░░░██████\n" +
                    "█████████░░░█████████\n" +
                    "█████████████████████";
            case 3 -> "░∆░░░░░░░░░░░░░░░░░░░\n" +
                    "███░░░░░░░░░░░░░░░███\n" +
                    "██████░░░░░░░░░██████\n" +
                    "█████████░░░█████████\n" +
                    "█████████████████████";
            default -> "░░░░░░░░░░░░░░░░░░░░░\n" +
                    "███░░░░░░░░░░░░░░░███\n" +
                    "██████░░░░░░░░░██████\n" +
                    "█████████░∆░█████████\n" +
                    "█████████████████████";
        };
    }



}
