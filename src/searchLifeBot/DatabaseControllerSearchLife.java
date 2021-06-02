import java.sql.*;

public class DatabaseControllerSearchLife {
    private final String IP="localhost:3306";
    private final String DB="skiff-db";
    private final String url="jdbc:mysql://"+IP+"/"+DB+"?serverTimezone=Europe/Moscow";
    private final String login="root";
    private final String password="1234";


    DatabaseControllerSearchLife(){

        try{
            Connection conn = DriverManager.getConnection(url, login, password);
            System.out.println("Connection to database SUCCESFULL.");
        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
    }


    public void update(String s){
        try{
            Connection conn = DriverManager.getConnection(url, login, password);
            System.out.println("Connection to database SUCCESFULL.");

            Statement statement = conn.createStatement();
            statement.executeUpdate(s);

            conn.close();
            statement.close();

        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
    }

    public void addUser(int id){

        try{
            Connection conn = DriverManager.getConnection(url, login, password);
            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
            ResultSet res=statement.executeQuery("SELECT EXISTS(SELECT user_id FROM users WHERE user_id = "+id+");");
            res.next();
            int exist = res.getInt(1);

            if(exist==0){
                update("insert users(user_id) values (" + id + ");"); //user_id, coins, last_search_time
            }

            conn.close();
            statement.close();

        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
    }
    public int queryForInt(String s){
        try{
            Connection conn = DriverManager.getConnection(url, login, password);
            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
            ResultSet res=statement.executeQuery(s);
            res.next();
            int n = res.getInt(1);

            conn.close();
            statement.close();
            return n;
        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
        return -1;
    }


    public User getUser(int id){
        try{
            Connection conn = DriverManager.getConnection(url, login, password);
            System.out.println("Connection to database SUCCESFULL.");


            Statement statement = conn.createStatement();
            ResultSet res=statement.executeQuery("SELECT * FROM users WHERE user_id = "+id+";");
            res.next();
            User user = new User(res.getInt(1),res.getInt(2),res.getString(3)); //user_id, coins, last_search_time

            conn.close();
            statement.close();
            return user;
        } catch (SQLException exc) {
            System.out.println("Connection to database failed...");
            exc.printStackTrace();
        }
        return null;
    }
    public void setUser(User user) {
        update("update users set coins=" + user.coins + ", last_search_time=" + user.timestamp + " where user_id=" + user.id + ";");
    }

    public int timeLastSearchDif(int id){
        return queryForInt("SELECT TIMESTAMPDIFF(SECOND,(select last_search_time from users where user_id="+id+"), '"+new Timestamp(System.currentTimeMillis())+"');");

    }


}
