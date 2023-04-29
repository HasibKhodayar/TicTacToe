import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 * 
 * All images taken from https://www.flaticon.com/ 
 * All sound files taken from https://pixabay.com/sound-effects/search/game/ 
 * All code for sound files were taken from sound.zip in the assigmnet 3 module posted
 * 
 * @author Hasib Khodayar 101225523
 * @version Assignment 3, April 5 2023
 */

public class TicTacToe extends MouseAdapter implements ActionListener
{
   public static final String PLAYER_X = "X"; // player using "X"
   public static final String PLAYER_O = "O"; // player using "O"
   public static final String EMPTY = " ";  // empty cell
   public static final String TIE = "T"; // game ended in a tie
   
   
   public static final ImageIcon X_MARK = new ImageIcon("Xmark.png");
   public static final ImageIcon O_MARK = new ImageIcon("Omark.png");
   public static final ImageIcon BLANK = new ImageIcon("TRANSPARENT.png");
   
   private ImageIcon playerIcon;
 
   private String player;   // current player (PLAYER_X or PLAYER_O)

   private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress

   private int numFreeSquares; // number of squares still free
   
   private JButton board[][]; // 3x3 array representing the board
   
   /* The new menu item */
   private JMenuItem newItem;
   
   /* The quit menu item */
   private JMenuItem quitItem;
   
   /* The reset Stats menu item */
   private JMenuItem resetStatsItem;
   
   /* The reset Stats menu item */
   private JMenuItem changeStartPlayerItem;
   
   //displays
   private JLabel progressDisplay;
   private JPanel boardDisplay;
   private JTextField statisticsDisplay;
   
   //Statictics fields
   private int numWinsX;
   private int numWinsO;
   private int numTies;
   
   AudioClip click;
   
   /** 
    * Constructs a new Tic-Tac-Toe board.
    */
   public TicTacToe()
   {
      
      board = new JButton[3][3];
      JFrame frame = new JFrame("Tic Tac Toe");
      Container contentPane = frame.getContentPane();
      contentPane.setLayout(new BorderLayout());
      frame.setPreferredSize(new Dimension (500,500));
      
      boardDisplay = new JPanel();
      boardDisplay.setLayout(new GridLayout(3,3));
        
      contentPane.add(boardDisplay);
      JMenuBar menubar = new JMenuBar();
      frame.setJMenuBar(menubar); // add menu bar to our frame

      JMenu fileMenu = new JMenu("Game"); // create a menu
      menubar.add(fileMenu); // and add to our menu bar
        
      fileMenu.addMouseListener(this); //mouse listener for menubar
      
      newItem = new JMenuItem("New"); // create a menu item called "New"
      fileMenu.add(newItem); // and add to our menu
      
      changeStartPlayerItem = new JMenuItem("Change Starting Player"); // create a menu item called "New"
      fileMenu.add(changeStartPlayerItem); // and add to our menu
      
      resetStatsItem = new JMenuItem("Reset Statistics"); // create a menu item called "Reset Statistics"
      fileMenu.add(resetStatsItem); // and add to our menu

      quitItem = new JMenuItem("Quit"); // create a menu item called "Quit"
      fileMenu.add(quitItem); // and add to our menu
      
      fileMenu.setFont(new Font("sans-serif", Font.BOLD, 14));
      
      
      // this allows us to use shortcuts (e.g. Ctrl-R and Ctrl-Q)
      final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); // to save typing
      newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
      resetStatsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
      quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
      changeStartPlayerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_MASK));
      
      // listen for menu selections
      newItem.addActionListener(this);
      quitItem.addActionListener(this);
      resetStatsItem.addActionListener(this);
      changeStartPlayerItem.addActionListener(this);
      
      
      progressDisplay = new JLabel();
      progressDisplay.setPreferredSize(new Dimension(200, 40));
      progressDisplay.setFont(new Font("Papyrus",Font.BOLD,18));
      contentPane.add(progressDisplay, BorderLayout.PAGE_END);
      
      statisticsDisplay = new JTextField(20);
      statisticsDisplay.setEditable(false);
      contentPane.add(statisticsDisplay, BorderLayout.PAGE_START);
      
      //statisticsDisplay.setFont(new Font(Font.SERIF,Font.BOLD,12));
      statisticsDisplay.setText(this.stats());
      statisticsDisplay.setFont(new Font("Papyrus",Font.BOLD,18));
      
      playGame();
      
      // finish setting up the frame
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit when we hit the "X"
      frame.pack(); // pack everthing into our frame
      frame.setResizable(false); // we can resize it
      frame.setVisible(true); // it's visible
   }

   /**
    * Method clearBoard Sets everything up for a new game.  Marks all squares in the Tic Tac Toe board as empty,
    * and indicates no winner yet, 9 free squares and the current player is player X.
    */
   private void clearBoard()
   {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            board[i][j].setText(EMPTY);
            board[i][j].setIcon(BLANK);
            board[i][j].setDisabledIcon(BLANK);
            board[i][j].setEnabled(true);
         }
      }
      winner = EMPTY;
      numFreeSquares = 9;
      player = PLAYER_X;     // Player X always has the first turn.
      playerIcon = X_MARK;
      progressDisplay.setText(this.toString());
   }


   /**
    * Plays a game of Tic Tac Toe, initilizes new buttons with action listeners for the board 
    * and adds them to the board display
    */

   public void playGame()
   {
      for(int i =0;i<3;i++){
           for(int j=0; j<3;j++){
               //initilizes board with new buttons
               JButton button = new JButton(EMPTY);
               board[i][j] = button;
               button.addActionListener(this);
               button.addMouseListener(this);
               boardDisplay.add(button);
           }
       }
       //clear board to make sure 
      clearBoard();
      //initilize statistics
      numWinsX = 0;
      numWinsO = 0;
   } 
   

   /**
    * Returns true if filling the given square gives us a winner, and false
    * otherwise.
    *
    * @param int row of square just set
    * @param int col of square just set
    * 
    * @return true if we have a winner, false otherwise
    */
   private boolean haveWinner(int row, int col) 
   {

       if (numFreeSquares>4) {return false;}
        
        // check row "row"
       if ( board[row][0].getIcon().equals(board[row][1].getIcon()) &&
                    board[row][1].getIcon().equals(board[row][2].getIcon()) ) {return true;}
                    
               // check column "col"   
       if ( board[0][col].getIcon().equals(board[1][col].getIcon()) &&
            board[0][col].getIcon().equals(board[2][col].getIcon()) ) {return true;}   
       
       //check one diagonal
       if ( board[0][0].getIcon().equals(board[1][1].getIcon()) &&
                    board[0][0].getIcon().equals(board[2][2].getIcon()) ) {return true;}
                    
       //check other diagonal
       if ( board[0][2].getIcon().equals(board[1][1].getIcon()) &&
                    board[0][2].getIcon().equals(board[2][0].getIcon()) ) {return true;}
        
       // no winner yet
       return false;
   }
   
   /**
    * Method to switch the player in a tictactoe game, if the player is X switch to O otherwise switch to player X
    */
   private void switchPlayer(){
       if(player.equals(PLAYER_X)){
           player = PLAYER_O;
           playerIcon = O_MARK;
       }
       else{
           player = PLAYER_X;
           playerIcon = X_MARK;
       }
   }

   /**
    * Method endGame ends the game by calling haveWinner method, disables the remaining unused buttons, incraments 
    * the score board and plays sound
    * this method does nothing if there is no winner or no tie
    * 
    * @param row, the row of the button that was just clicked
    * @param col, the column of the button that was just clicked
    */
   private void endGame(int row, int col){
       if(haveWinner(row,col)){
           winner = player;
           for(int i =0;i<3;i++){
                   for(int j=0; j<3;j++){
                      board[i][j].setEnabled(false); 
                   }
           }
           if(winner.equals(PLAYER_X)){
               numWinsX += 1;
           }
           else{
               numWinsO += 1;
           }
           
           //play winner sound
           URL urlclick = TicTacToe.class.getResource("winSound.wav");
           click = Applet.newAudioClip(urlclick);
           click.play();
       }
       else if(numFreeSquares==0){
            winner = TIE;
            numTies += 1;
            
            //play Tie sound
            URL urlclick = TicTacToe.class.getResource("tieSound.wav");
            click = Applet.newAudioClip(urlclick);
            click.play();
       }
       
   }
   
   /**
    * Returns a string representing the current stats of the game.  
    *
    * @return String representing the statstics of the tic tac toe game state
    */
   private String stats(){
       return "Statistics:             " + "X Wins: " + numWinsX + "             O Wins: " + numWinsO + 
       "             Ties: " + numTies ;
   }
    
   /**
    * Returns a string representing the current state of the game.  This should look like
    * a regular tic tac toe board, and be followed by a message if the game is over that says
    * who won (or indicates a tie).
    *
    * @return String representing the tic tac toe game state
    */
    public String toString() 
    {
        String retString = " ";
        if( ! winner.equals(EMPTY)){
            if(winner.equals(TIE)){
                retString += " \n Its a Tie!";}
            else{
            retString += "\n" + winner + " wins!";}
        }
        else{
            retString += "\n Game in progress and " + player + "s turn";
        }
        return retString; // this needs to be updated
    }
    
    /** This action listener is called when the user clicks on 
    * any of the GUI's buttons. 
    */
    public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource(); // get the action 
        // see if it's a JButton
        if (o instanceof JButton) {
            JButton button = (JButton)o;
            if(button.getIcon().equals(BLANK) && winner.equals(EMPTY)){
                for(int i =0;i<3;i++){
                   for(int j=0; j<3;j++){
                       if( button == board[i][j]){
                           button.setIcon(playerIcon);
                           button.setDisabledIcon(playerIcon);
                           button.setEnabled(false);
                           
                           //play sound for clicking
                           URL urlclick = TicTacToe.class.getResource("clickSound.wav");
                           click = Applet.newAudioClip(urlclick);
                           click.play();
                                         
                           numFreeSquares--;     // decrement number of free squares
                           endGame(i,j);  // see if the game is over
                           switchPlayer();// switch player if game not over
                       }
                    }}
                
            }
        }
        
         else { // it's a JMenuItem
            
            JMenuItem item = (JMenuItem)o;
            
            if (item == newItem) { // reset
                clearBoard();
            } else if(item == resetStatsItem){
                numWinsX = 0;
                numWinsO = 0;
                numTies = 0;
                statisticsDisplay.setText(this.stats());
            } else if (item == quitItem) { // quit
                System.exit(0);
            } else if(item == changeStartPlayerItem){
                clearBoard();
                switchPlayer();
            }
        }
        
        progressDisplay.setText(this.toString());
        statisticsDisplay.setText(this.stats());

    } 
    
    /**
    * Detects when the mouse enters the component.  We are only "listening" to the
    * JMenu.  We highlight the menu name or buttons when the mouse goes into that component.
    * 
    * Code taken from countermenuhighlight.zip that was posted in Assigment 3 files
    * @param e The mouse event triggered when the mouse was moved into the component
    */
   public void mouseEntered(MouseEvent e) {
        Object o = e.getSource();
        if(o instanceof JButton){
            JButton button = (JButton)o;
            button.setSelected(true);
        }
        else{
        JMenu item = (JMenu) e.getSource();
        item.setSelected(true); // highlight the menu name
        }
   }

   /**
    * Detects when the mouse exits the component.  We are only "listening" to the
    * JMenu.  We stop highlighting the menu name or buttons when the mouse exits  that component.
    * 
    * Code taken from countermenuhighlight.zip that was posted in Assigment 3 files
    * @param e The mouse event triggered when the mouse was moved out of the component
    */
   public void mouseExited(MouseEvent e) {
        Object o = e.getSource();
        if(o instanceof JButton){
            JButton button = (JButton)o;
            button.setSelected(false);
        }
        else{
        JMenu item = (JMenu) e.getSource();
        item.setSelected(false); // highlight the menu name
        }
    }
}