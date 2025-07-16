package view;

/**
 * Interface for the View component in the MVC architecture of the Game of the Amazons.
 * Defines methods that all views must implement in order to interact with the controller
 * and display the game state to the user.
 * @author OFIR AVIANI | 23.02.2025
 */

public interface IView 
{
    public void clearBoard();

    public void updateScreen();
   
    public void setVisible(boolean status);  // since View1 extends JFrame, and JFrame also has a 'setVisible' method, we don't need to implemenent it in 'View1'
}
