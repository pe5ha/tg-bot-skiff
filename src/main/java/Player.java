import java.util.ArrayList;

public class Player {
    public long userid;
    public String username;
    public String firstname;

    public int gameMessageId;

    public int points;
    public int bet;
    public int position;
    public int seconds;
    public String moveStatus;

    ArrayList<Integer> moves = new ArrayList<>();


    public Player(long userid, String username, String firstname) {
        this.userid = userid;
        this.username = username;
        this.firstname = firstname;
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

    public String getAvailableName(){
        return (username.equals("@null")?firstname:username);
    }


}
