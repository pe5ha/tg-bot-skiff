import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockThread extends Thread{

    SkiffLadderBot Bot;

    ClockThread(String name, SkiffLadderBot Bot){
        super(name);
        this.Bot=Bot;
    }


    public void run(){
        System.out.printf("%s started... \n", Thread.currentThread().getName());
        try{
//            int i=0;
//            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
            while (true) {
                Thread.sleep(60000);
//                Bot.editMyMessageText(""+Bot.statusChatId, Bot.statusMessageId, false, sdf.format(new Date()));
//                i++;
//                Bot.updateBotPublicStatus();
            }
        }
        catch(InterruptedException e){
            System.out.println("Thread has been interrupted");
        }
        System.out.printf("%s fiished... \n", Thread.currentThread().getName());
    }



}
