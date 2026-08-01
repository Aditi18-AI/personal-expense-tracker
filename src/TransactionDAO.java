import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


//DAO(Data Access Object)class for handling trandsctions in the database//
public class TransactionDAO{
    //Method to retrieve all the transaction from the database//
    public static List<Transaction> getAllTransaction(){
        List<Transaction> transactions = new ArrayList<>();

        Connection connection = Database.getconnection();
        PreparedStatement ps;
         ResultSet rs ;


         try{
         ps = connection.prepareStatement("SELECT * FROM `transaction_table`");
         rs = ps.executeQuery();

         //Iterate through the result set obtained from the sql query//
         while(rs.next()){
            //Extract transaction details from the result set//
            int id = rs.getInt("id");
            String type = rs.getString("transaction_type");
            String description = rs.getString("description");
            double amount = rs.getDouble("amount");
            //Add the transaction to the list//
            Transaction transaction = new Transaction (id , type , description , amount);
            transactions.add(transaction);
            
         }

         }
         catch(SQLException ex){
            ex.printStackTrace();
            
         }
         //Return the list of transactions 
        return transactions;
        
    }

}