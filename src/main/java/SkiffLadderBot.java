import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SkiffLadderBot extends TelegramLongPollingBot {
    String name="@SkiffLadderGameBot";
    String token="1749369333:AAEQ2ikfg0NWghbHUqvrOXfa4Z0I2J3TZwo";
    DatabaseController database = new DatabaseController();
    GameController gameController = new GameController();
    KeyboardAndViewController keyboardAndViewController = new KeyboardAndViewController();

    long adminUserId=235733832;


    long streamChatId = -542398399;
    boolean stream = true;
    int streamMessageId;

    String statusChannelChatId = "-1001457091664";
    int statusChannelMessageId;

    String  logChatId = "-415920993";
    long statusChatId = -542398399;
    int statusMessageId;



    SkiffLadderBot(){
        sendMyMessage(statusChannelChatId,false,"Бот запущен!");
        statusChannelMessageId=sendMyMessage(statusChannelChatId,false,":)");
        ClockThread clockThread = new ClockThread("clock",this);
        clockThread.start();
    }




    @Override
    public void onUpdateReceived(Update update) {

        long userid;
        String username;
        String firstname;
        String text="";
        long chatId;
        int messageId;


//        update.has

//        System.out.println(update);// test log

        // Если получено сообщение с текстом
        if (update.hasMessage() && update.getMessage().hasText()) {
            // получение данных
            userid = update.getMessage().getFrom().getId();
            username = "@"+update.getMessage().getFrom().getUserName();
            firstname = update.getMessage().getFrom().getFirstName();
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();

            // Добавление нового пользователя
            database.addUser(userid, username, firstname);
            System.out.println(update.getMessage().getChatId() + ": "+update.getMessage().getFrom().getId()+": " + username + ": " + text); // test log
//            System.out.println(update.getMessage().getAuthorSignature());
//            System.out.println(update.getMessage().getSenderChat());
//            System.out.println(update.getMessage().getForwardFrom().getId());
//            System.out.println(sendMyMessage());

            sendMyMessage(logChatId,false,userid+":"+update.getMessage().getFrom().getUserName()+": "+text);

            if (text.equals("/start")){
                SendMessage sendHello = new SendMessage();
                sendHello.setChatId(""+chatId);
                sendHello.setText("Привет\\! Это игра *Лестница Скифов*\\. Она только\\-только доделана до MVP, так что в ней пока очень мало функционала\\. " +
                        "\n\nНа данный момент возможно играть только с другом, вручную отправив сюда его никнейм в формате _@никнейм\\_друга_\\. " +
                        "При этом друг должен предварительно запустить этого бота или написать ему /start\\. \n\n/help – правила игры, прочие команды\\.\n\n" +
                        "░░░░░░░░░░░░░░░░░░░░░\n" +
                        "███░░░░░░░░░░░░░░░███\n" +
                        "██████░░░░░░░░░██████\n" +
                        "█████████░∆░█████████\n" +
                        "█████████████████████\n\n" +
                        "t\\.me/SkiffLadder \\- канал с сообщениями о запуске и статусе бота\\!");
                sendHello.enableMarkdownV2(true);
//                sendHello.setParseMode("MarkdownV2");
                try {
                    execute(sendHello);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            if (text.equals("/rules")){
                SendMessage sendHelp = new SendMessage();
                sendHelp.setChatId(""+chatId);
                sendHelp.setText("Лестница Скифов - легкая интуитивная игра, в которую играют один на один.\n" +
                        "\n\n*–>* В распоряжении игроков по 50 очков." +
                        "\n\n*–>* Игроки делают ставку, выбирая какое количество очков потратить." +
                        "\n\n*–>* Треугольник перемещается на одну ступеньку в сторону того игрока, чья ставка выше. Ваша сторона слева." +
                        "\n\n*–>* Для победы в игре необходимо чтобы треугольник оказался на верхней ступени вашей стороны." +
                        "\n\n_Сложное правило, пока можно пропустить_\n*[–>* Если у одного игрока заканчиваются все его очки, другой игрок считается победившим, если у него достаточно очков, чтобы переместить треугольник на верхнюю ступень на своей стороне, тратя по 1 очку. Если у игрока не хватает для этого очков, определяется ничья.*]*" +
                        "\n\n*–>* Если у обоих игроков закончились очки, а треугольник не переместился на верхнюю ступеньку ни к одному из них — засчитывается ничья." +
                        "\n\n*–>* Хорошим ходом считается тот, который позволяет переместить треугольник в вашу сторону с минимальным перевесом в ставке, либо грамотно уступить с максимальной потерей очков у соперника.");
                sendHelp.enableMarkdown(true);
                sendHelp.setParseMode("Markdown");
                try {
                    execute(sendHelp);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            if(text.equals("/leave")&&database.getUserState(userid).equals("in game")){
                Match match = gameController.cancelGame(userid);
                endGame(match);
            }
            if(text.equals("/help")){
                sendMyMessage(""+chatId,false,"Доступны следующие команды:" +
                        "\n\n/start – главная" +
                        "\n/rules – правила игры" +
                        "\n/leave – покинуть игру");
            }
            if (text.equals("/matcheslist")&&userid==adminUserId) sendMyMessage(""+chatId,false,""+gameController.matches.size());
            if(text.equals("/newgame"))sendMyMessage(""+chatId,true,"Пришлите никнейм пользователя с которым хотите сыграть в формате _@никнейм\\_друга_");
//            if(text.equals("/status")&&userid==adminUserId) ;

            // Создание игры с другом
            // по id друга
//            if(update.getMessage().getForwardFrom() != null)
//                System.out.println(update.getMessage().getForwardFrom().getId());
            // по никнейму друга
            if(text.contains("@")) {
                if (text.equals(username)) ; // чтобы не создавать с собой
                else if (database.getUserState(userid).equals("in game")) // если я ещё в игре. чтобы не создавать одновр неск партий
                    sendMyMessage("" + userid, false, "Вы ещё в игре, покинуть текущую игру можно командой /leave");
                else if (database.existUser(text)) { // создание игры
                    if (database.getUserState(database.getUseridByUsername(text)).equals("inactivity")) {

                        Player player1 = new Player(userid, username, firstname);
                        Player player2 = new Player(database.getUseridByUsername(text), text, database.getFirstnameByUsername(text));
                        Match match = new Match(player1, player2);
                        gameController.newMatch(match);

                        // оправка игры первому игроку
                        SendMessage gameUIp1Message = keyboardAndViewController.sendGameKeyboard(chatId, 50);
                        // оправка игры второму игроку
                        sendMyMessage("" + player2.userid, false, "Игрок " + player1.nickname + " пригласил вас в игру! Ваш ход!");
                        SendMessage gameUIp2Message = keyboardAndViewController.sendGameKeyboard(player2.userid, 50); // вместо chatId - userid

                        try {
                            player1.gameMessageId = execute(gameUIp1Message).getMessageId();
                            player2.gameMessageId = execute(gameUIp2Message).getMessageId();
                            database.setUserState(player1.userid, "in game");
                            database.setUserState(player2.userid, "in game");
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
//                    editBotStatus(statusChatId,statusMessageId);
                        updateBotPublicStatus(); //status
                        sendMyMessage(logChatId, false, player1.nickname.replace("@", "") + " играет с " + player2.nickname.replace("@", "")); // logChat
                        // TODO настройка стрима

                    } else {
                        sendMyMessage("" + chatId, false, "Этот игрок уже с кем-то играет в данный момент.");
                    }
                } else {
                    sendMyMessage("" + chatId, false, "Пользователь " + text + " ещё не запускал бота. Отправьте ему ссылку t.me/SkiffLadderGameBot или перешлите это сообщение.");
                }
            }
//            if(update.getMessage().getForwardFrom() != null)
//                System.out.println(update.getMessage().getForwardFrom().getId());
            // "либо пользователь запретил ссылаться на его профиль
        }

        // info Нажатие инлайн КНОПКИ
        if(update.hasCallbackQuery()){


            userid = update.getCallbackQuery().getFrom().getId();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();

            // logChat
            sendMyMessage(logChatId,false,userid+":"+update.getCallbackQuery().getFrom().getUserName()+": "+update.getCallbackQuery().getData());

            switch (database.getUserState(userid)){
                case "inactivity":
                    // TODO кнопки для старта игры
                    break;
                case "in game":
                    // игрок походил
                    if(Integer.parseInt(update.getCallbackQuery().getData())>=0) { // fixme не актуальное условие, теперь проверка внутри движка

//                        System.out.println(update.getCallbackQuery().getFrom().getUserName()+" play "+update.getCallbackQuery().getData()); // log


                        Match match;
                        match = gameController.someoneMakeMove(userid, Integer.parseInt(update.getCallbackQuery().getData())); // тут обсчитывается ход
                        if(match.currPlayer.moveStatus.equals("abuse")) { // чтобы не ходил дважды
                            sendMyMessage(""+chatId,false,"Дождитесь хода соперника.");
                            sendMyMessage(logChatId,false,match.currPlayer.nickname+" abuse");
                            break;}
                        sendMyMessage(logChatId,false,update.getCallbackQuery().getFrom().getUserName()+" play "+update.getCallbackQuery().getData());


                        EditMessageText editGameMessageP1; // current player
                        EditMessageText editGameMessageP2; // another player
                        String info;

                        // если конец игры
                        if (match.isFinished) {
                            endGame(match);
                            database.addGameResult(match); // почему не в внутри функции?
                        }
                        // если ещё игра
                        else {
                            // обновление для игрока только что нажавшего
//                            String reminder;
                            // TODO "ВАШ ХОД" сверху игры чтобы видно из списка диалогов
                            // TODO таймер на 15 сек
                            if (match.isSomeonePlayerWait) info = "\n*Вы сходили* | Соперник ходит"; else info = "\nВы ходите | Соперник ходит";
                            info+=getMovesHistory(match.turn,match.currPlayer,match.anotherPlayer);
                            editGameMessageP1 = buildGameMessageUpdate(chatId,messageId,info,match.currPlayer.position,match.currPlayer.points);
                            // обновление для другого игрока
                            if (match.isSomeonePlayerWait) info = "\nВы ходите | *Соперник сходил*"; else info = "\nВы ходите | Соперник ходит";
                            info+=getMovesHistory(match.turn,match.anotherPlayer,match.currPlayer);
                            editGameMessageP2 = buildGameMessageUpdate(match.anotherPlayer.userid,match.anotherPlayer.gameMessageId,info,match.anotherPlayer.position,match.anotherPlayer.points);// вместо chatId - userid

                            try {
                                execute(editGameMessageP1);
                                execute(editGameMessageP2);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            /*
                            // стрим
                            info="\nИгрок 1 | Игрок 2";
                            EditMessageText editStreamMessage = new EditMessageText();
                            editStreamMessage.setText(keyboardAndViewController.gameStateImg(match.player1.position) + info);
                            editStreamMessage.setChatId(""+streamChatId);
                            editStreamMessage.setMessageId(streamMessageId);
                            try {
                                execute(editStreamMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            */
                        }

                    }
                    break;
            }
        }





    }


    public void endGame(Match match){
        String info;
        EditMessageText editGameMessageP1; // current player
        EditMessageText editGameMessageP2; // another player
        String result = match.isDraw?"ничья":match.winner.userid==match.player1.userid?"игрок 1":"игрок 2";
        if (!match.isDraw) {


            info="\n*Вы победили!* \uD83D\uDC51";
            info+=getMovesHistory(match.turn,match.winner,match.looser);
            editGameMessageP1 = buildGameMessageUpdate(match.winner.userid,match.winner.gameMessageId,info,match.winner.position,0);
            info="\n*Вы проиграли!*";
            info+=getMovesHistory(match.turn,match.looser,match.winner);
            editGameMessageP2 = buildGameMessageUpdate(match.looser.userid,match.looser.gameMessageId,info,match.looser.position,0);

            database.setUserState(match.player1.userid,"inactivity");
            database.setUserState(match.player2.userid,"inactivity");
            System.out.println("Winner: "+match.winner.nickname+" Looser: "+match.looser.nickname);
        } else {
            info="\n*Ничья!*";
            info+=getMovesHistory(match.turn,match.player1,match.player2);
            editGameMessageP1 = buildGameMessageUpdate(match.player1.userid,match.player1.gameMessageId,info,match.player1.position,0);
            info="\n*Ничья!*";
            info+=getMovesHistory(match.turn,match.player2,match.player1);
            editGameMessageP2 = buildGameMessageUpdate(match.player2.userid,match.player2.gameMessageId,info,match.player2.position,0);

            database.setUserState(match.player1.userid,"inactivity");
            database.setUserState(match.player2.userid,"inactivity");
            System.out.println("Ничья: "+match.player1.nickname+" - "+match.player2.nickname);
        }
        try {
            execute(editGameMessageP1);
            execute(editGameMessageP2);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        // TODO надо ли возможно подчищать список матчей ?
//        editBotStatus(statusChatId,statusMessageId); //log chat
        updateBotPublicStatus(); // STATUS
        sendMyMessage(logChatId,false,result+": "+match.player1.nickname.replace("@","")+" против "+match.player2.nickname.replace("@","")); // logChat
    }



    public int sendMyMessage(String chatId,boolean enableMarkdownV2, String s){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
//        sendMessage.setParseMode("Markdown");
        if(enableMarkdownV2) sendMessage.enableMarkdownV2(true);
        try {
            return execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public EditMessageText buildGameMessageUpdate(long chatId,int messageId,String info,int playerPosition,int playerPoints){
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId("" + chatId);
        editMessage.setMessageId(messageId);
//        if (match.someoneMakeMove) info = "\n*Вы сходили* | Соперник ходит";
        editMessage.setText(keyboardAndViewController.gameStateImg(playerPosition) + info);
        editMessage.setReplyMarkup(keyboardAndViewController.setGameKeyboard(playerPoints));
        editMessage.enableMarkdown(true);
        editMessage.setParseMode("Markdown");
        return editMessage;
    }

    public String getMovesHistory(int turn, Player currPlayer, Player anotherPlayer){
        String movesHistory="\n";
        movesHistory+="*["+currPlayer.points+"]*–*["+(anotherPlayer.points+anotherPlayer.bet)+"]*\n";

        for (int i = 0; i < turn; i++) {
            movesHistory += ""+currPlayer.moves.get(i)+"–"+anotherPlayer.moves.get(i);
            if(currPlayer.moves.get(i)-anotherPlayer.moves.get(i)==1) movesHistory += " \uD83C\uDFAF";
            movesHistory+="\n";
        }
        if(currPlayer.moves.size()>turn) movesHistory +=currPlayer.moves.get(currPlayer.moves.size()-1)+"–";
        if(anotherPlayer.moves.size()>turn) movesHistory +=" –?";

        return movesHistory;
    }


    public void updateBotPublicStatus(){
        String status=buildBotStatus();
        editMyMessageText(statusChannelChatId,statusChannelMessageId,false,status);
    }



//    public int sendBotStatus(long logChatId){
//        String status = buildBotStatus();
//        return sendMyMessage(""+logChatId,false,status);
//    }
//    public void editBotStatus(long logChatId,int statusMessageId){
//        String status = buildBotStatus();
//        editMyMessageText(""+logChatId,statusMessageId,false,status);
//    }
    public String buildBotStatus(){
        String status = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
        status+="Бот работает, если здесь правильное время.\n";
        status+="Время: "+sdf.format(new Date())+"\n\n";
        status+="Число сыгранных игр: "+gameController.gamesCounter+"\n";
        status+="Игроков в игре: "+ database.getActiveUserCount()+"\n\n";
        status+="Го играй и приглашай знакомых :>\n";
        status+="t.me/SkiffLadderGameBot\n";
        return status;
    }

    public void editMyMessageText(String chatId,int messageId,boolean enableMarkdownV2,String newText){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(""+chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
//        editMessageText.disableWebPagePreview();
        if(enableMarkdownV2)editMessageText.setParseMode("MarkdownV2");
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    // TODO трансляция хода игры (без кнопок) в канал трансляций


}