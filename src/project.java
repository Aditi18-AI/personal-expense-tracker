import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;


 public class project{

 
    //Variables for the main frame and UI components//
    private JFrame frame;
    private JPanel titleBar;
    private JLabel titleLabel;
    private JLabel closeLabel;
    private JLabel minimizeLabel;
    private JPanel dashboardPanel;
    private JPanel  buttonsPanel;
    private JButton addTransactionButton;
    private JButton removeTransactionButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    
    //Variable to store total amount//
    private double totalamount = 0.0;
    
    //ArrayList to store data Panel Values//
    private ArrayList<String>dataPanelValues = new ArrayList<>(); 
    
    //Variable for form dragging//
    private boolean isDragging = false;
    private Point mouseOffset;

    //Constructor//
   public project(){
      frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800,600);
      frame.setLocationRelativeTo(null);

      //Remove form border and default close and minimize buttons//
      frame.setUndecorated(true); 
      frame.setVisible(true);
      // Set custom border to the frame//
      frame.getRootPane().setBorder( BorderFactory.createMatteBorder(5,5,5,5,new Color(30,30,30)));

      //Create and setup the title bar//
      titleBar = new JPanel();
      titleBar.setLayout(null);
      titleBar.setBackground(new Color(52,73,94));
      titleBar.setPreferredSize(new Dimension(frame.getWidth(),30));
       frame.add(titleBar,BorderLayout.NORTH);

       //Create and setup the title label//
       titleLabel = new JLabel( "Expense and Income Tracker");
       titleLabel.setForeground(Color.WHITE);
       titleLabel.setFont(new Font("Arial",Font.BOLD,17));
       titleLabel.setBounds(10,0,250,30);
       titleBar.add(titleLabel);

       //Create and setup the close label//
       closeLabel = new JLabel("X");
       closeLabel.setForeground(Color.WHITE);
       closeLabel.setFont(new Font("Arial",Font.BOLD,17));
       closeLabel.setHorizontalAlignment(SwingConstants.CENTER);
       closeLabel.setBounds(frame.getWidth() - 40,0,30,30);
       closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

       //Create and setup the minimize label//

       minimizeLabel = new JLabel("-");
       minimizeLabel.setForeground(Color.WHITE);
       minimizeLabel.setFont(new Font("Arial",Font.BOLD,17));
       minimizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
       minimizeLabel.setBounds(frame.getWidth() - 80,0,50,30);
       minimizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
      //Add Mouse Listener for close Label interactions//
       closeLabel.addMouseListener(new MouseAdapter(){
         public void mouseClicked(MouseEvent e){
            System.exit(0);
         }
         public void mouseEntered(MouseEvent e){
            closeLabel.setForeground(Color.RED);
         }
         public void mouseExited(MouseEvent e){
            closeLabel.setForeground(Color.WHITE);
         }

       });

         titleBar.add(closeLabel);

       //Add Mouse Listener for minimize Label interactions//
       minimizeLabel.addMouseListener(new MouseAdapter(){
         public void mouseClicked(MouseEvent e){
            frame.setState(JFrame.ICONIFIED);
         }
         public void mouseEntered(MouseEvent e){
            minimizeLabel.setForeground(Color.RED);
         }
         public void mouseExited(MouseEvent e){
            minimizeLabel.setForeground(Color.WHITE);
         }

       });

       titleBar.add(minimizeLabel);

       //Set up form dragging functionally//
       //MouseListener for Window dragging//
       titleBar.addMouseListener(new MouseAdapter(){
         public void mousePressed(MouseEvent e){
            isDragging = true;
            mouseOffset = e.getPoint();
         }
         public void mouseReleased (MouseEvent e){
            isDragging = false;
         }

       });
       //Mouse motion Listener for window dragging//
       titleBar.addMouseMotionListener(new MouseMotionAdapter(){
          public void mouseDragged(MouseEvent e){
            if(isDragging)
            {
               Point newLocation = e.getLocationOnScreen();
               //Calculate the new window location by adjusting for the initial mouse offset//
               newLocation.translate(-mouseOffset.x,-mouseOffset.y);
               //Set th3e new location of the main window to achieve draging effect//
               frame.setLocation(newLocation);
            }
          }
       });
       //Create and setup the dashboard Panel//
       dashboardPanel = new JPanel();
       dashboardPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
       dashboardPanel.setBackground(new Color(236,240,241));
       frame.add(dashboardPanel,BorderLayout.CENTER);

        //Calculate total amount and populate data panel values//
        totalamount = TransactionValuesCalculation.getTotalValue(TransactionDAO.getAllTransaction());
        dataPanelValues.add(String.format("-Rs%,.2f", TransactionValuesCalculation.getTotalExpenses(TransactionDAO.getAllTransaction())));
        dataPanelValues.add(String.format("Rs%,.2f", TransactionValuesCalculation.getTotalIncome(TransactionDAO.getAllTransaction())));
        dataPanelValues.add(String.format("Rs%,.2f", totalamount));
        



       
       

       //Add data panels for Expense,Income, and Total//
       adddataPanel("Expense",0);
       adddataPanel("Income",1);
       adddataPanel("Total",2);
       
       //Create and set up Buttons Panel//
       addTransactionButton = new JButton("Add Transaction");
       addTransactionButton.setBackground(new Color(41,128,185));
       addTransactionButton.setForeground(Color.WHITE);
       addTransactionButton.setFocusPainted(false);
       addTransactionButton.setBorderPainted(false);
       addTransactionButton.setFont(new Font("Arial",Font.BOLD,14));
       addTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
       addTransactionButton.addActionListener((e) -> {showAddTransactionDialog(); });
       
       removeTransactionButton = new JButton("Remove Transaction");
       removeTransactionButton.setBackground(new Color(231,76,60));
       removeTransactionButton.setForeground(Color.WHITE);
       removeTransactionButton.setFocusPainted(false);
       removeTransactionButton.setBorderPainted(false);
       removeTransactionButton.setFont(new Font("Arial",Font.BOLD,14));
       removeTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
       removeTransactionButton.addActionListener((e) -> {
          
              removeSelectedTransaction();
          
      });
       
       
       buttonsPanel = new JPanel();
       buttonsPanel.setLayout(new BorderLayout(10,5));
       buttonsPanel.add(addTransactionButton,BorderLayout.NORTH);
       buttonsPanel.add(removeTransactionButton,BorderLayout.SOUTH);
       dashboardPanel.add(buttonsPanel);
       
       
       //Setup the transaction table//
        String[] columnNames = {"ID","Type" ,"Description" ,"Amount"};
        tableModel = new DefaultTableModel(columnNames,0){

         public boolean isCellEditable(int row , int column){
            //Make all cells non editable//
            return false;
          }
        };

        //transactionTable.setShowGrid(true);
        //transactionTable.setGridColor(Color.GRAY);
        transactionTable = new JTable(tableModel);

        configureTransactionTable();
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(900,800));
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        configurescrollPane(scrollPane);
        dashboardPanel.add(scrollPane);

       
        frame.setVisible(true);

   }
       //fix the negative values//
       private String  fixNegativeValueDisplay(double values){

         //Check if the input starts with "Rs-" (indicating negay=tive)//
         String newval = String.format("Rs%.2f",values);
         if(newval.startsWith("Rs")){
            //Extract the numeric part after "Rs-"//
            String numericPart = newval.substring(2);
            //Format the result as "Rsxxxx"//
            newval = "Rs"+numericPart;

         }


         return newval;

       } 

      //Remove the selected transaction from the table and datbase//
      private void removeSelectedTransaction(){
         int selectedRow = transactionTable.getSelectedRow();

         if(selectedRow != -1){
            //Obtain the transaction details from the selected row//
            int transactionId = (int) transactionTable.getValueAt(selectedRow,0);
            String type = transactionTable.getValueAt(selectedRow,1).toString();
            String amountStr = transactionTable.getValueAt(selectedRow,3).toString();
            double amount = Double.parseDouble(amountStr.replace("Rs","").replace(" ","").replace(",",""));
            
            //Upadate totalAmount based on the type of the transaction//
            totalamount -= amount;
            JPanel totalPanel = (JPanel) dashboardPanel.getComponent(2);
            totalPanel.repaint();

            int indexToUpdate = type.equals("Income") ?  1 : 0;
            String currentValue = dataPanelValues.get(indexToUpdate);
            double currentAmount = Double.parseDouble(currentValue.replace("Rs","").replace(" ","").replace(",","").replace("--","-"));
            double updatedAmount = currentAmount + (type.equals("Income")  ? -amount : amount);
            //dataPanelValues.set(indexToUpdate , String.format("Rs%,.2f",updatedAmount));
            if(indexToUpdate == 1){//Income
            dataPanelValues.set(indexToUpdate,String.format("Rs%,.2f",updatedAmount));

         }//Expense//
         else{dataPanelValues.set(indexToUpdate,fixNegativeValueDisplay(updatedAmount)); 

}


            //Repaint the corresponding data panel//
            JPanel  dataPanel =  (JPanel) dashboardPanel.getComponent(indexToUpdate); 
            dataPanel.repaint();

            tableModel.removeRow(selectedRow);
            removeTransactionFromDatabase(transactionId);



            /*
             Calculate total amount and populate data panel values//
             totalamount = TransactionValuesCalculation.getTotalValue(TransactionDAO.getAllTransaction());
             dataPanelValues.add(String.format("-Rs%,.2f", TransactionValuesCalculation.getTotalExpenses(TransactionDAO.getAllTransaction())));
             dataPanelValues.add(String.format("Rs%,.2f", TransactionValuesCalculation.getTotalIncome(TransactionDAO.getAllTransaction())));
             dataPanelValues.add(String.format("Rs%,.2f", totalamount));
             */


         }
      }



      //Remove a transaction from database//
      private void removeTransactionFromDatabase(int transactionId){
        try {
            Connection connection = Database.getconnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM transaction_table WHERE id = ?");

            ps.setInt(1,transactionId);
            ps.executeLargeUpdate();
            System.out.println("Transaction removed");


        } 
        
        
        catch (SQLException ex) {
          ex.printStackTrace(); 
        }

      }

       //Displays the dialog for adding a new transaction//
       private void showAddTransactionDialog(){
         //Create a new dialog for adding transaction//
         JDialog dialog = new JDialog(frame ,"Add Transcation",true);
         dialog.setSize(400,250);
         dialog.setLocationRelativeTo(frame);
         //Create a panel to hold the components in a grid layout//
         JPanel dialogPanel = new JPanel(new GridLayout(4,2,10,10));
         //set an empty border with padding for the dialog panel//
         dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // add padding
         dialogPanel.setBackground(Color.LIGHT_GRAY);
         

         //Create and configure components for transaction input// 
         JLabel typeLabel = new JLabel("Type");
         JComboBox<String>  typeComboBox = new JComboBox<>(new String [] {"Expense", "Income"}); 
         typeComboBox.setBackground(Color.WHITE);
         typeComboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
         JLabel descriptionLabel = new JLabel("Description");
        
         JTextField descriptionField = new  JTextField();
         descriptionField.setBorder(BorderFactory.createLineBorder(Color.BLACK));


         JLabel amountLabel = new JLabel("Amount");
         JTextField amountField = new  JTextField();
         amountField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
         //Create and configure "Add" Button//
         JButton addButton = new JButton("Add");
         addButton.setBackground(new Color(41,125,185));
         addButton.setForeground(Color.WHITE);
         addButton.setFocusPainted(false);
         addButton.setBorderPainted(false);
         addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

         addButton.addActionListener((e) -> {
            addTransaction(typeComboBox,descriptionField,amountField);
         });
         
         
         
         //Add components to dialog panel//
         dialogPanel.add(typeLabel);
         dialogPanel.add( typeComboBox );
         dialogPanel.add(descriptionLabel);
         dialogPanel.add(descriptionField);
         dialogPanel.add(amountLabel);
         dialogPanel.add(amountField);
         dialogPanel.add(new JLabel()); // empty space cell
         dialogPanel.add(addButton);

         Database.getconnection();
         
         dialog.add(dialogPanel);
         dialog.setVisible(true);
      }

      //Add new Transaction to the database//

      private void addTransaction(JComboBox<String> typeComboBox, JTextField descriptionField , JTextField amountFiled) {
         String type = (String) typeComboBox.getSelectedItem();
         String description = descriptionField.getText();
         String amount = amountFiled.getText();
         
         //Parse the amount string to a double value//
         double newAmount = Double.parseDouble(amount.replace("Rs", "").replace(",", "").trim());

         //Update the total amount based on the transaction type (Income or Expense)//
         //Income//
         if(type.equals("Income")){totalamount += newAmount;}
         //Expense//
         else{totalamount -= newAmount ;}

         //Update the displayed total amount on the dashboard//

         JPanel totalPanel = (JPanel) dashboardPanel.getComponent(2);
         totalPanel.repaint();

         //Determine the index of the data panel to panel to update based on the transaction type//
         int indexToUpdate = type.equals("Income") ? 1 : 0;

         //Retrieve the current value of data panel//

         String currentValue = dataPanelValues.get(indexToUpdate);

         //Parse the current amount string to a double value//
         double currentAmount = Double.parseDouble(currentValue.replace("Rs", "").replace("",""));

         //Calculate the updated amount based on the transaction type//
         double updatedAmount = currentAmount + (type.equals("Income") ? newAmount : -newAmount);

         //Update the data panel with a new amount//
         //dataPanelValues.set(indexToUpdate,String.format("Rs%,.2f",updatedAmount));
         if(indexToUpdate == 1){//Income
            dataPanelValues.set(indexToUpdate,String.format("Rs%,.2f",updatedAmount));

         }//Expense//
         else{dataPanelValues.set(indexToUpdate,fixNegativeValueDisplay(updatedAmount)); 

}

         //Update the displayed data panel on the dashboard//
         JPanel dataPanel =(JPanel) dashboardPanel.getComponent(indexToUpdate);
         dataPanel.repaint();



        try {

         
         Connection connection = Database.getconnection();
         String insertQuery = "INSERT INTO transaction_table (transaction_type, description, amount) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(insertQuery);

         ps.setString(1, type);              // e.g., "Expense"
         ps.setString(2, description);       // e.g., "Test"
         ps.setDouble(3, Double.parseDouble(amount)); // e.g., 100
         ps.executeUpdate();


         tableModel.setRowCount(0);
         populateTableTransactions();


         System.out.println("Type: " + type);
         System.out.println("Description: " + description);
         System.out.println("Amount: " + amount);
         System.out.println("Data inserted successfully");
      } 
      
     catch (SQLException e) {
      System.out.println("Data not inserted successfully: " + e.getMessage());
}
        

      
      }
      //Populate Table transaction//
      private void populateTableTransactions(){
         for(Transaction transaction : TransactionDAO.getAllTransaction()){
            Object [] rowData = {transaction.getId(), transaction.getType(),
               transaction.getDescription(),transaction.getAmount()};
             
               tableModel.addRow(rowData);

         }
      }
      
      


         //Configure the appearance and behavior of the transaction table//
         private void configureTransactionTable()
        {
         transactionTable.setRowHeight(30);
         transactionTable.setBackground(new Color(236,240,241));
         transactionTable.setFont(new Font("Arial",Font.PLAIN,18));
         transactionTable.setShowGrid(true);
         transactionTable.setBorder(null);
         transactionTable.setDefaultRenderer(Object.class, new TransactionTableCellRenderer());
         transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

         populateTableTransactions();

        JTableHeader tableHeader = transactionTable.getTableHeader();
        tableHeader.setForeground(Color.RED);
        tableHeader.setFont(new Font("Arial",Font.BOLD,10));
        tableHeader.setDefaultRenderer(new GradientHeaderRenderer());

        }
        //Configures the appearance of the scroll pane//
        private void configurescrollPane(JScrollPane scrollPane){
         scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
         scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
         scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
         scrollPane.setPreferredSize(new Dimension(750,300));


        }


        //Add data panel to the dashboard panel//
        private void adddataPanel(String title,int index)
        {
           JPanel dataPanel = new JPanel(){
            //Override the paintComponent method to customize the appearance//
            protected void paintComponent(Graphics g){
               //Call the paintComponent method of the super class//
               super.paintComponent(g);
               Graphics2D g2d = (Graphics2D) g;
               //make the drawing smooth//
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               //Check if the title is "Total",to determine the content to display//
               if(title.equals("Total")){
               //Check if the title is "Total", draw the data panel with the total amount//

                  //drawDataPanel(g2d,title,String.format("Rs%.2f",totalamount),getWidth(),getHeight());
                  drawDataPanel(g2d,title,fixNegativeValueDisplay(totalamount) ,getWidth(),getHeight());

               }
               
               else{
                  //IF the title is not "Total", draw the data panel with the corresponding value from the list//
                  drawDataPanel(g2d,title,dataPanelValues.get(index),getWidth(),getHeight());

               }

            }

           };
           //Set the layout ,size,background colour, and border for the data panel//
           dataPanel.setLayout(new GridLayout(2,1));
           dataPanel.setPreferredSize(new Dimension(170,100));
           dataPanel.setBackground(new Color(255,255,255));
           dataPanel.setBorder(new LineBorder (new Color(149,165,166), 2));
           dashboardPanel.add(dataPanel);
         }
        //Draws a data panel with specified title and value//
         private void drawDataPanel(Graphics g,String title,String value,int width,int height){

            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(new Color(255,255,255));
            g2d.fillRoundRect(0,0,width,height,20,20);
            g2d.setColor(new Color(236,240,241));
            g2d.fillRect(0,0,width,40);
            
            
            //draw title//
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial",Font.BOLD,20));
            g2d.drawString(title,20,30);
            
            //draw value//
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial",Font.BOLD,16));
            g2d.drawString(value,20,75);
         } 
      
         //main method//
      public static void main(String [] args){
      new project();
    }
}

//Custom table header renderer with gradient background//
class GradientHeaderRenderer extends JLabel implements TableCellRenderer{
   
   private final Color startColor = new Color(192,192,192);
   private final  Color endColor = new Color(50,50,50);
   

   public GradientHeaderRenderer(){
      setOpaque(false);
      setHorizontalAlignment(SwingConstants.CENTER);
      setForeground(Color.WHITE);
      setFont(new Font("Arial",Font.BOLD,22));
      setBorder(
    BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 1, Color.YELLOW),
        BorderFactory.createEmptyBorder(2, 5, 2, 5)
    )
);


   }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
       setText(value.toString());
       return this;
    }

    protected void paintComponent(Graphics g){
      Graphics2D g2d = (Graphics2D) g;
      int width = getWidth();
      int height = getHeight();
      GradientPaint gradientPaint = new GradientPaint(0,0,startColor, width , 0 , endColor);
      g2d.setPaint(gradientPaint);
      g2d.fillRect(0,0,width,height);
      super.paintComponent(g);
    }
}

  //Create a custom scroll bar UI class for the scrollpane//
    class CustomScrollBarUI extends BasicScrollBarUI{
      private Color thumbColor = new Color(189,195,199);
      private Color trackColor = new Color(236,240,241);
      protected void configureScrollBarColors(){
         //Call the superclass method to ensure default configuration//
         super.configureScrollBarColors();

      }
      //Override a method to create the decrease button of the scroll bar// 
      protected JButton createDecreaseButton(int orientation){
         //Create an empty button for the decrease button//
         return createEmptyButton();
      }
       //Override a method to create the increase button of the scroll bar// 
      protected JButton createIncreaseButton(int orientation){
         //Create an empty button for the increase button//
         return createEmptyButton();
      }
      //Override a method to paint the thumb of the scroll bar//
      protected void paintThumb (Graphics g,JComponent c, Rectangle thumbBounds){
         //Set the color and fill the thumb area with the specified color//
         g.setColor(thumbColor);
         g.fillRect(thumbBounds.x,thumbBounds.y,thumbBounds.width,thumbBounds.height);

      }
      //Override a method to paint the track of the scroll bar//
      protected void paintTrack(Graphics g,JComponent c, Rectangle trackBounds){
         //Set the color and fill the track area with the specified color//
         g.setColor(trackColor);
         g.fillRect(trackBounds.x,trackBounds.y,trackBounds.width,trackBounds.height);
      }
      //Private method to create an empty button with zero dimensions//
      private JButton createEmptyButton(){
         JButton button = new JButton();
         button.setPreferredSize(new Dimension(0,0));
         button.setMaximumSize(new Dimension(0,0));
         button.setMinimumSize(new Dimension(0,0));
         return button;
      }
      
    }

    //Custom cell renderer for the transaction table/
    class TransactionTableCellRenderer extends DefaultTableCellRenderer{

      //Override method to customize the rendering of table cells//
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus,int row,int column){
         //Call the superclass method to get the default rendering component//
         Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

         //Get the transaction type from the second column of the table//

         String type = (String) table.getValueAt(row, 1);
         //Customize the appearance based on the selection and transaction type//
         if(isSelected){
            c.setForeground(Color.BLACK);
            c.setBackground(Color.GRAY);
         }
         else{
            if("Income".equals(type)){
               c.setBackground(new Color(144,238,144));
              
            }
            else{
               c.setBackground(new Color(255,99,71));
              
            }
         }
         return c;

      }
    }
   


 
