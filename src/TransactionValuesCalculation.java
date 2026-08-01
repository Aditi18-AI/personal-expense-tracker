
import java.util.List;

//Class to create various transaction values//
public class TransactionValuesCalculation{
   //Method to calculate the total incomes from a list of transactions//
   public static Double getTotalIncome(List<Transaction> transactions){
    //Initialize the TotalIncome variable//
   double totalIncome = 0.0;
   //Loop thorough each transaction in the list//
   for(Transaction transaction  : transactions){
    //Check if the transaction type is "Income"//
    if("Income".equals(transaction.getType())){
        //Add the transaction amount to the total income//
        totalIncome += transaction.getAmount();

    }
   }
   //Return the total calculated income//
    return totalIncome;

   }
   //Method to calculate the total expenses from a list of transactions//
   public static Double getTotalExpenses(List<Transaction> transactions){
    //Initialize the TotalExpense variable//
   double totalExpenses = 0.0;
   //Loop thorough each transaction in the list//
   for(Transaction transaction  : transactions){
    //Check if the transaction type is "Expense"//
    if("Expense".equals(transaction.getType())){
        //Add the transaction amount to the total expense//
        totalExpenses+= transaction.getAmount();

    }
   }
   //Return the total calculated expense//
    return totalExpenses;
  }

//Method to claculate the total value (income - expenses) from a list of transactions//
public static Double getTotalValue(List<Transaction> transactions){
    //Calculate the total income using getTotalIncome method//
    Double totalIncome = getTotalIncome(transactions);
    Double totalExpense = getTotalExpenses(transactions);
    


    return totalIncome - totalExpense;
    
} 


}