import java.util.ArrayList;

public class GameController {
    ArrayList<Match> matches;

    GameController(){
        matches = new ArrayList<>();

    }


    public void newMatch(Match match){
        matches.add(match);

    }

    public Match someoneMakeMove(long userid, int betPoints){
        Match match = findMatchByPlayerId(userid);
        match.currPlayer=findPlayerById(match, userid);


        if(match.currPlayer.moveStatus.equals("thinks")){ // чтобы не ходить дважды, статус "походил"
            match.currPlayer.addMove(betPoints);
            match.currPlayer.points-=betPoints;
            match.currPlayer.bet=betPoints;
            match.currPlayer.moveStatus="done";
            match.isSomeonePlayerWait= !match.isSomeonePlayerWait; // один игрок ждет другого или оба сейчас ходят?
        }
        else{
            match.currPlayer.moveStatus="abuse";
        }



        // завершение хода - оба игрока сделали ставку, возвращение статуса "ходит"
        if((!match.player1.moveStatus.equals("thinks"))&&(!match.player2.moveStatus.equals("thinks"))){  // если оба игрока НЕ думают, то есть если оба походили
            if(match.player1.moves.get(match.player1.moves.size()-1)>match.player2.moves.get(match.player2.moves.size()-1)){
                match.player1.position++;
                match.player2.position--;
            }
            else if(match.player1.moves.get(match.player1.moves.size()-1)<match.player2.moves.get(match.player2.moves.size()-1)){
                match.player1.position--;
                match.player2.position++;
            }
            match.player1.bet=0;
            match.player2.bet=0;
            match.turn++;

            // проверка конца игры
            if(match.player1.position==3||(match.player2.points==0&&match.player1.points>=getNeededStepToWin(match.player1.position))){
                match.isGameEnd=true;
                match.winner=match.player1;
                match.looser=match.player2;
            }
            else if(match.player2.position==3||(match.player1.points==0&&match.player2.points>=getNeededStepToWin(match.player2.position))){
                match.isGameEnd=true;
                match.winner=match.player2;
                match.looser=match.player1;
            }
            // ничья
            else if((match.player1.points==0&&match.player2.points<getNeededStepToWin(match.player2.position))||
                    (match.player2.points==0&&match.player1.points<getNeededStepToWin(match.player1.position))){
                match.isGameEnd=true;
                match.isDraw=true;
            }
            // следующий ход
            else {
                match.player1.seconds=match.secondsToMove;
                match.player2.seconds=match.secondsToMove;
                match.player1.moveStatus="thinks";
                match.player2.moveStatus="thinks";
            }



        }






        return match;
    }


    public Match stopGame(long userid){
        Match match = findMatchByPlayerId(userid);
        match.looser=findPlayerById(match,userid);
        match.winner=match.anotherPlayer;
        match.isGameEnd=true;
        return match;
    }


    public int getNeededStepToWin(int position){
        return switch (position){
            case 2 -> 1;
            case 1 -> 2;
            case 0 -> 3;
            case -1 -> 4;
            case -2 -> 5;
            case -3 -> 1000; // костыль!!!!
            default -> -1;
        };
    }


    public Player findPlayerById(Match match, long userid){

        if (match.player1.userid == userid) {
            match.anotherPlayer=match.getPlayer2();
            return match.getPlayer1();
        }
        if (match.player2.userid == userid) {
            match.anotherPlayer=match.getPlayer1();
            return match.getPlayer2();
        }

        return null;
    }

    public Match findMatchByPlayerId(long userid){
        for (Match match : matches) {
            if (match.player1.userid == userid && !match.isGameEnd) return match;
            if (match.player2.userid == userid && !match.isGameEnd) return match;
        }
        return null;
    }





}
