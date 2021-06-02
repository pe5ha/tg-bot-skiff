import java.util.ArrayList;

public class Player {
    public long userid;
    public String nickname;
    public String name;

    public int gameMessageId;

    public int points;
    public int bet;
    public int position;
    public int seconds;
    public String moveStatus;

    ArrayList<Integer> moves = new ArrayList<>();


    public Player(long userid, String nickname, String name) {
        this.userid = userid;
        this.nickname = nickname;
        this.name = name;
        points = 50;
        bet=0;
        seconds = 15;
        position = 0;
        moveStatus="thinks";
    }

    public void setSeconds(int seconds){
        seconds = seconds;
    }

    public void addMove(int betPoints){
        moves.add(betPoints);
    }



}
