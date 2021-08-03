import java.sql.*;

public class DatabaseController {
//    private final String IP="localhost:3306";
//    private final String DB="skiff-db";
//    private final String url="jdbc:mysql://"+IP+"/"+DB+"?serverTimezone=Europe/Moscow";
//    private final String login="root";
//    private final String password="1234";

    private final String DATABASE_URL = "jdbc:" + (System.getenv("JAWSDB_URL") != null ?
            System.getenv("JAWSDB_URL") :
            "mysql://root:1234@localhost:3306/skiff-db?serverTimezone=Europe/Moscow");
//            "mysql://hxajv37etqujodak:chg4fl154941i8fe@pei17y9c5bpuh987.chr7pe7iynqr.eu-west-1.rds.amazonaws.com:3306/wnsvihyqnfnr9wqv?serverTimezone=Europe/Moscow");

    DatabaseController(){
        System.out.println("Current database URL: " + DATABASE_URL);
        try {
//            Connection conn = DriverManager.getConnection(url, login, password);
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connection to database SUCCESFULL.");
//            Statement statement = conn.createStatement();
//            statement.executeUpdate("SET time_zone='Europe/Moscow';");


        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }

        resetAllUsersStatus();


    }


    public void update(String s){
        try{
            Connection conn = DriverManager.getConnection(DATABASE_URL);
//            System.out.println("Connection to database SUCCESFULL.");

            Statement statement = conn.createStatement();
//            statement.executeUpdate("SET time_zone='+3:00';");
            statement.executeUpdate(s);

            conn.close();
            statement.close();

        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
    }

    public void addUser(long id, String nickname, String name){

        try{
            Connection conn = DriverManager.getConnection(DATABASE_URL);
//            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
//            statement.executeUpdate("SET time_zone='Europe/Moscow';");

            ResultSet res=statement.executeQuery("SELECT EXISTS(SELECT userid FROM users WHERE userid = "+id+");");
            res.next();
            int exist = res.getInt(1);

            if(exist==0){
                update("insert into users (userid,nickname,name) values (" + id + ",'"+nickname+"','"+name+"');");
            }
            update("update users set nickname = '"+nickname+"' where userid = "+id+";");

            conn.close();
            statement.close();

        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
    }
    public int selectOneInt(String s){
        try{
            Connection conn = DriverManager.getConnection(DATABASE_URL);
//            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
//            statement.executeUpdate("SET time_zone='Europe/Moscow';");
            ResultSet res=statement.executeQuery(s);

            int n = -1;
            if(res.next()) n = res.getInt(1);

            conn.close();
            statement.close();
            return n;
        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
        return -1;
    }
    public String selectOneString(String s){
        try{
            Connection conn = DriverManager.getConnection(DATABASE_URL);
//            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
//            statement.executeUpdate("SET time_zone='Europe/Moscow';");
            ResultSet res=statement.executeQuery(s);
            String str = "";
            if(res.next()) str = res.getString(1);

            conn.close();
            statement.close();
            return str;
        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
        return "";
    }


    public String getUserState(long id){
        return selectOneString("select userstate from users where userid = "+id+";");
    }
    public void setUserState(long userid, String userstate){
        update("update users set userstate = '"+userstate+"' where userid = "+userid+";");
    }
    public void addGameResult(Match match){
        String res,win;
        if(match.isDraw){
            win="NULL";
            res = "draw";
        }
        else {
            win=match.winner.userid+"";
            res = match.winner.username;
        }
        String game_log="";
        for (int i = 0; i < match.turn; i++) {
            game_log+=match.player1.moves.get(i)+"-"+match.player2.moves.get(i)+";\n";
        }
        update("insert into games (player1,player2,p1_message_id,p2_message_id,nickname1,nickname2,result,winner,game_log) values ("+match.player1.userid+","+match.player2.userid+","+match.player1.gameMessageId+","+match.player2.gameMessageId+",'"+ match.player1.username +"','"+ match.player2.username +"','"+res+"',"+win+",'"+game_log+"');");
    }

    public int getActiveUserCount(){
        return selectOneInt("select count(*) from users where userstate = 'in game';");
    }



    public int timeLastSearchDif(int id){
        return selectOneInt("SELECT TIMESTAMPDIFF(SECOND,(select last_search_time from users where user_id="+id+"), '"+new Timestamp(System.currentTimeMillis())+"');");
    }
    public int getUseridByUsername(String username){
        if(username.equals("@null")) return -1;
        return selectOneInt("select userid from users where nickname = '"+username+"';");
    }
    public String getUsernameByUserid(long userid){
        return selectOneString("select nickname from users where userid = '"+userid+"';");
    }

    public String getFirstnameByUserid(long userid){
        return selectOneString("select name from users where userid = '"+userid+"';");
    }
    public long getFriendIdByPreviousGame(long userid,int messageId){
        int id = selectOneInt("select player2 from games where player1 = "+userid+" and p1_message_id = "+messageId+";");
        if(id>0) return id;
        id = selectOneInt("select player1 from games where player2 = "+userid+" and p2_message_id = "+messageId+";");
        if(id>0) return id;
        return -1;
    }
    public int getGamesCountAll(){
        return selectOneInt("SELECT count(*) FROM games;");
    }
    public int getGamesCountToday(){
        return selectOneInt("SELECT count(*) FROM games where date(game_date) = cast(now() as date);");
    }

    public boolean existUser(String nickname){
        try{
            Connection conn = DriverManager.getConnection(DATABASE_URL);
//            System.out.println("Connection to database SUCCESFULL.");
            Statement statement = conn.createStatement();
//            statement.executeUpdate("SET time_zone='Europe/Moscow';");
            if (nickname.equals("@null")) nickname = "";
            ResultSet res=statement.executeQuery("SELECT EXISTS(SELECT * FROM users WHERE nickname = '"+nickname+"');");
            res.next();
            boolean exist = res.getInt(1) != 0;
            conn.close();
            statement.close();
            return exist;
        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
        return false;
    }


    public void resetAllUsersStatus(){
        update("update users set userstate = 'inactivity';");
    }

}
