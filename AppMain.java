import controller.Controller;
import model.Model;
import view.View;

/**
 * Entry point of the Game of the Amazons application.
 * Initializes the main controller and view, and starts the game UI.
 * @author OFIR AVIANI | 23.02.2025
 */

public class AppMain
{
    public static void main(String[] args) throws Exception
    {
        Model m = new Model();
        View v = new View();
        Controller controller = new Controller(m, v);
        v.setController(controller);
        controller.runGame();
    }
}