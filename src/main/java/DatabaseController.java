import java.sql.*;

public class DatabaseController {
    private final String IP="localhost:3306";
    private final String DB="skiff-db";
    private final String url="jdbc:mysql://"+IP+"/"+DB+"?serverTimezone=Europe/Moscow";
    private final String login="root";
    private final String password="1234";


    DatabaseController(){

        try{
            Connection conn = DriverManager.getConnection(url, login, password);
            System.out.println("Connection to database SUCCESFULL.");
        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }

        resetAllUsersStatus();


    }


    public void update(String s){
        try{
            Connection conn = DriverManager.getConnection(url, login, password);
//            System.out.println("Connection to database SUCCESFULL.");

            Statement statement = conn.createStatement();
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
            Connection conn = DriverManager.getConnection(url, login, password);
//            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
            ResultSet res=statement.executeQuery("SELECT EXISTS(SELECT userid FROM users WHERE userid = "+id+");");
            res.next();
            int exist = res.getInt(1);

            if(exist==0){
                update("insert into users (userid,nickname,name) values (" + id + ",'"+nickname+"','"+name+"');");
            }

            conn.close();
            statement.close();

        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
    }
    public int selectOneInt(String s){
        try{
            Connection conn = DriverManager.getConnection(url, login, password);
//            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
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
            Connection conn = DriverManager.getConnection(url, login, password);
//            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
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
        String  res;
        if(match.isDraw) res = "draw";
        else res = match.winner.nickname;
        update("insert into games (player1,player2,result) values ("+match.player1.userid+","+match.player2.userid+",'"+res+"');");
    }

    public int getActiveGamesCount(){
        return selectOneInt("select count(*) from users where userstate = 'in game';")/2;
    }



    public int timeLastSearchDif(int id){
        return selectOneInt("SELECT TIMESTAMPDIFF(SECOND,(select last_search_time from users where user_id="+id+"), '"+new Timestamp(System.currentTimeMillis())+"');");
    }
    public int getUseridByUsername(String username){
        return selectOneInt("select userid from users where nickname = '"+username+"';");
    }
    public String getFirstnameByUsername(String username){
        return selectOneString("select name from users where nickname = '"+username+"';");
    }

    public boolean existUser(String nickname){
        try{
            Connection conn = DriverManager.getConnection(url, login, password);
//            System.out.println("Connection to database SUCCESFULL.");
            Statement statement = conn.createStatement();
            ResultSet res=statement.executeQuery("SELECT EXISTS(SELECT nickname FROM users WHERE nickname = '"+nickname+"');");
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
