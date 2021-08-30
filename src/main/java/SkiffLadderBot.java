import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.*;


public class SkiffLadderBot extends TelegramLongPollingBot {
    String name="@SkiffLadderGameBot";
//    String token="1749369333:AAEQ2ikfg0NWghbHUqvrOXfa4Z0I2J3TZwo";
    String token="1796068046:AAGqlRdhy-XDaV73PtHyzR6DL9LjIR7M1iA";
    private final String BOT_TOKEN = "" + (System.getenv("BOT_TOKEN") == null ? token : System.getenv("BOT_TOKEN"));
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
//        clockThread.start();
    }




    @Override
    public void onUpdateReceived(Update update) {

        long userid;
        String username;
        String firstname;
        String text="";
        long chatId;
        int messageId;

        // TODO таймер на ход, очки друг против друга, играть со случайным, рейтинг


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

            System.out.println(chatId + ":" + userid + ":" + username + ":" + firstname + ": " + text);
            sendMyMessage(logChatId, false, chatId + ":" + userid + ":" + username.replace("@Pavel_Naumenko","Pavel_Naumenko") + ":" + firstname + ": " + text);


            if (text.equals("/start")){
                SendMessage sendHello = new SendMessage();
                sendHello.setChatId(""+chatId);
                sendHello.setText("Привет\\! Это игра *Лестница Скифов*\\." +
                        "\n\nНа данный момент возможно играть только с другом, вручную отправив сюда его никнейм в формате _@никнейм\\_друга_\\. " +
                        "При этом друг должен предварительно запустить этого бота\\." +
                        "\n\nОписание и правила: /about" +
                        "\nСписок команд: /help" +
                        "\n\nЧисло сыгранных игр: *"+database.getGamesCountAll()+"*\n" +
                        "За сегодня: *"+database.getGamesCountToday()+"*\n"+
                        "Играют сейчас: *"+ database.getActiveUserCount()+"*\n\n" +
                        "░░░░░░░░░░░░░░░░░░░░░\n" +
                        "███░░░░░░░░░░░░░░░███\n" +
                        "██████░░░░░░░░░██████\n" +
                        "█████████░∆░█████████\n" +
                        "█████████████████████\n\n");
                sendHello.enableMarkdownV2(true);
                sendHello.disableWebPagePreview();
//                sendHello.setParseMode("MarkdownV2");
                try {
                    execute(sendHello);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (text.equals("/rules")){
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
            else if(text.equals("/leave")&&database.getUserState(userid).equals("in game")){
                Match match = gameController.cancelGame(userid);
                endGame(match);
            }
            else if(text.equals("/help")){
                sendMyMessage(""+chatId,false,"Доступны следующие команды:" +
                        "\n\n/start – главная менюшка" +
                        "\n/about – статья про бота" +
                        "\n/rules – правила игры" +
                        "\n/leave – отменить текущую игру" +
                        "\n/status – работает ли бот?");
            }
            else if(text.equals("/status")){
                sendMyMessage(""+chatId,false,"@SkiffLadder - канал со статусом бота и полезной инфой");
            }
            else if(text.equals("/about")){
                sendMyMessage(""+chatId,false,"https://telegra.ph/Lestnica-Skifov--telegram-bot-07-30");
            }
            else if (text.equals("/newgame")) {
                sendMyMessage("" + chatId, true, "Пришлите никнейм пользователя с которым хотите сыграть в формате _@никнейм\\_друга_");
            }
//            if(text.equals("/status")&&userid==adminUserId) ;
            if (userid==adminUserId) {
                if(text.contains("/selectOneString")){
                    sendMyMessage(""+chatId,false,database.selectOneString(text.split(" ",2)[1]));
                }
                else if(text.contains("/selectOneInt")){
                    sendMyMessage(""+chatId,false,""+database.selectOneInt(text.split(" ",2)[1]));
                }
                else if(text.contains("/sendTo")){
                    sendMyMessage(text.split(" ",3)[1],false,text.split(" ",3)[2]);
                }
                else if(text.contains("/sendAll")){

                }
            }

            // Создание игры с другом
            if(text.startsWith("@")) {
                long friendId = database.getUseridByUsername(text);
                if(friendId<0) sendMyMessage("" + chatId, false, "Пользователь " + text + " ещё не запускал бота. Отправьте ему ссылку t.me/SkiffLadderGameBot или перешлите это сообщение.");
                else if (friendId==userid) sendMyMessage(""+chatId,false,"Сыграй с кем-нибудь телеграм знакомым"); // чтобы не создавать с собой
                else {
                    Player player1 = new Player(userid, username, database.getFirstnameByUserid(userid));
                    Player player2 = new Player(friendId, text, database.getFirstnameByUserid(friendId));
                    newGame(player1, player2);
                }

            }

        }

        // info Нажатие инлайн КНОПКИ
        else if(update.hasCallbackQuery()){

            userid = update.getCallbackQuery().getFrom().getId();
            username = "@"+update.getCallbackQuery().getFrom().getUserName();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            String button = update.getCallbackQuery().getData();
            int betPoints = -1;
            try {
                betPoints=Integer.parseInt(button);
            }
            catch(NumberFormatException ignored) {
            }

            if (gameController.isCurrentGame(userid,messageId)&&betPoints>=0){ // если это кнопка идущей игры

                Match match = gameController.someoneMakeMove(userid, betPoints); // тут обсчитывается ход

                if(match.currPlayer.moveStatus.equals("abuse"))
                    sendMyMessage(""+chatId,false,"Дождитесь хода соперника."); //Если игрок спамит
                else if(match.isFinished) {// если конец игры
                    endGame(match);
                    database.addGameResult(match);
                }
                else {  // если ещё игра
                    EditMessageText editGameMessageP1; // current player
                    EditMessageText editGameMessageP2; // another player
                    String info;

                    // обновление для игрока только что нажавшего
                    if (match.isSomeonePlayerWait) info = "\n*Вы сходили* | Соперник ходит";
                    else info = "\nВы ходите | Соперник ходит";
                    info += getMovesHistory(match.turn, match.currPlayer, match.anotherPlayer);
                    editGameMessageP1 = buildGameMessageUpdate(chatId, messageId, info, match.currPlayer.position, match.currPlayer.points);
                    // обновление для другого игрока
                    if (match.isSomeonePlayerWait) info = "\nВы ходите | *Соперник сходил*";
                    else info = "\nВы ходите | Соперник ходит";
                    info += getMovesHistory(match.turn, match.anotherPlayer, match.currPlayer);
                    editGameMessageP2 = buildGameMessageUpdate(match.anotherPlayer.userid, match.anotherPlayer.gameMessageId, info, match.anotherPlayer.position, match.anotherPlayer.points);// вместо chatId - userid

                    try {
                        execute(editGameMessageP1);
                        execute(editGameMessageP2);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }




            }
            else if(button.equals("rematch")){
                long friendId = database.getFriendIdByPreviousGame(userid,messageId);
                if(friendId>0) {

                    Player player1 = new Player(userid, username, database.getFirstnameByUserid(userid));
                    Player player2 = new Player(friendId, database.getUsernameByUserid(friendId), database.getFirstnameByUserid(friendId));

                    newGame(player1, player2);
                }
                else {
                    sendMyMessage(""+chatId,false,"Что-то пошло не так..");
                }
            }


        }










    }

    public void newGame(Player player1,Player player2){

        if (gameController.isPlayingNowPlayer(player1.userid)) sendMyMessage("" + player1.userid, false, "Вы ещё в игре, покинуть текущую игру можно командой /leave");
        else if (gameController.isPlayingNowPlayer(player2.userid)) sendMyMessage("" + player2.userid, false, "Этот игрок уже с кем-то играет в данный момент");
        else if(sendMyMessage("" + player2.userid, false, "Игрок " + player1.getAvailableName() + " пригласил вас в игру! Ваш ход!")!=0){
            // всё ок - начало игры
            sendMyMessage(""+player1.userid, false, "Вы отправили вызов игроку "+player2.getAvailableName());

            Match match = new Match(player1, player2);
            gameController.newMatch(match);

            // оправка игры первому игроку
            SendMessage gameUIp1Message = keyboardAndViewController.sendGameKeyboard(player1.userid, 50);
            // оправка игры второму игроку
            SendMessage gameUIp2Message = keyboardAndViewController.sendGameKeyboard(player2.userid, 50);

            try {
                player1.gameMessageId = execute(gameUIp1Message).getMessageId();
                player2.gameMessageId = execute(gameUIp2Message).getMessageId();
                database.setUserState(player1.userid, "in game");
                database.setUserState(player2.userid, "in game");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
//                    editBotStatus(statusChatId,statusMessageId);
//            updateBotPublicStatus(); //status
            System.out.println("Началась игра: "+player1.getAvailableName() + " vs " + player2.getAvailableName());
            sendMyMessage(logChatId, false, "Началась игра: "+player1.getAvailableName().replace("@Pavel_Naumenko","Pavel_Naumenko") + " vs " + player2.getAvailableName().replace("@Pavel_Naumenko","Pavel_Naumenko")); // logChat
            // TODO настройка стрима

        } else {
            sendMyMessage("" + player1.userid, false, player2.getAvailableName() +" заблокировал бота :( попроси его снова запустить меня...");
        }
    }

    public void endGame(Match match){
        String info;
        EditMessageText editGameMessageP1; // current player
        EditMessageText editGameMessageP2; // another player

        if (!match.isDraw) {
            info="\n*Вы победили!* \uD83D\uDC51";
            info+=getMovesHistory(match.turn,match.winner,match.looser);
            editGameMessageP1 = buildGameMessageUpdate(match.winner.userid,match.winner.gameMessageId,info,match.winner.position,0);
            info="\n*Вы проиграли!*";
            info+=getMovesHistory(match.turn,match.looser,match.winner);
            editGameMessageP2 = buildGameMessageUpdate(match.looser.userid,match.looser.gameMessageId,info,match.looser.position,0);

            database.setUserState(match.player1.userid,"inactivity");
            database.setUserState(match.player2.userid,"inactivity");
            System.out.println("Winner: "+match.winner.username +" Looser: "+match.looser.username);
        } else {
            info="\n*Ничья!*";
            info+=getMovesHistory(match.turn,match.player1,match.player2);
            editGameMessageP1 = buildGameMessageUpdate(match.player1.userid,match.player1.gameMessageId,info,match.player1.position,0);
            info="\n*Ничья!*";
            info+=getMovesHistory(match.turn,match.player2,match.player1);
            editGameMessageP2 = buildGameMessageUpdate(match.player2.userid,match.player2.gameMessageId,info,match.player2.position,0);

            database.setUserState(match.player1.userid,"inactivity");
            database.setUserState(match.player2.userid,"inactivity");
            System.out.println("Ничья: "+match.player1.username +" - "+match.player2.username);
        }

        // Реванш кнопка
        if(!match.isCanceled) {
            List<List<AbstractMap.SimpleEntry<String, String>>> arrayButtonNames = new ArrayList<>();
            List<AbstractMap.SimpleEntry<String, String>> buttonsRow = new ArrayList<>();
            buttonsRow.add(new AbstractMap.SimpleEntry<>("rematch", "Реванш!"));
            arrayButtonNames.add(buttonsRow);
            editGameMessageP1.setReplyMarkup(keyboardAndViewController.buildMessageKeyboard(arrayButtonNames));
            editGameMessageP2.setReplyMarkup(keyboardAndViewController.buildMessageKeyboard(arrayButtonNames));
        }
        try {
            execute(editGameMessageP1);
            execute(editGameMessageP2);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


//        updateBotPublicStatus(); // STATUS
        System.out.println("Закончилась игра: "+match.winner.getAvailableName()+(match.isDraw?" ничья ":" победил ")+ match.looser.getAvailableName());
        String result = "Закончилась игра: "+match.winner.getAvailableName().replace("@Pavel_Naumenko","Pavel_Naumenko")+(match.isDraw?" ничья ":" победил ")+ match.looser.getAvailableName().replace("@Pavel_Naumenko","Pavel_Naumenko");
        sendMyMessage(logChatId,false,result); // logChat

        gameController.deleteMatch(match);// подчищать список матчей ?
    }


    public void streamGame(){
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
        editMyMessageText(statusChannelChatId,statusChannelMessageId,true,status);
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm",new Locale("GMT+03:00"));
        status+="*Время: "+sdf.format(new Date())+"*\n";
        status+="Бот работает, если здесь правильное время.\n\n";
        status+="Число сыгранных игр: *"+gameController.gamesCounter+"*\n";
        status+="Игроков в игре: *"+ database.getActiveUserCount()+"*\n\n";
        status+="Го играй и приглашай знакомых :>\n";
        status+="t.me/SkiffLadderGameBot\n";
        return escapeMarcdownV2(status);
    }
    public String escapeMarcdownV2(String st){
        return st.replace("_","\\_")
//                .replace("*","\\*")
        .replace("[","\\[")
        .replace("]","\\[")
        .replace("(","\\(")
        .replace("~","\\~")
        .replace("`","\\`")
        .replace(">","\\>")
        .replace("#","\\#")
        .replace("+","\\+")
        .replace("-","\\-")
        .replace("=","\\=")
        .replace("|","\\|")
        .replace("{","\\{")
        .replace("}","\\}")
        .replace(".","\\.")
        .replace("!","\\!");
    }

    public void editMyMessageText(String chatId,int messageId,boolean enableMarkdownV2,String newText){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(""+chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
//        editMessageText.disableWebPagePreview();
        if(enableMarkdownV2)editMessageText.setParseMode("MarkdownV2");
//        try {
//            execute(editMessageText);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    // TODO трансляция хода игры (без кнопок) в канал трансляций


}