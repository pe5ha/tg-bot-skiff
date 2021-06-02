public class Match {
    public Player player1;
    public Player player2;
    public Player currPlayer;
    public Player anotherPlayer;
    public Player winner;
    public Player looser;
    public int secondsToMove;
    public int turn;
    public boolean isSomeonePlayerWait;
    public boolean isGameEnd;
    public boolean isDraw;



    public Match(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;

        secondsToMove = 15;
        isSomeonePlayerWait=false;
        isGameEnd=false;
        isDraw=false;
        currPlayer=player1;
        anotherPlayer=player2;
        turn=0;

    }

    public void update(){

    }



    public Player getPlayer1(){
        return player1;
    }

    public Player getPlayer2(){
        return player2;
    }




}
