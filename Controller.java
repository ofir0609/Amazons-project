package controller;

import model.IModel;
import view.IView;
import controller.Constants.CellContent;
import controller.Constants.OpponentType;
import controller.Constants.PlayerColor;

/**
 * Acts as the central controller for the Game of the Amazons application.
 * Mediates between the model and view, and initializes the game..
 * @author OFIR AVIANI | 23.02.2025
 */

public class Controller
{
    private IModel model;
    private IView view;

    /**
     * Constructs a Controller object containing a model and a view.
     * @param model the game logic and state handler
     * @param view the user interface display and interaction handler
     */
    public Controller(IModel model, IView view)
    {
        this.model = model;
        this.view = view;
    }

    /**
    * Starts the game by updating the view and making it visible.
    */
    public void runGame()
    {
        view.updateScreen();;
        view.setVisible(true);
    }

    /**
    * Resets the game board to its initial state - clear from arrows, and with the amazons in their places
    */
    public void resetGameBoard()
    {
        model.resetBoard();
    }

    /**
    * Returns a copy of the current game board.
    * @return a matrix representing the contents of the board
    */
    public CellContent[][] getGameBoardCopy()
    {
        return model.getBoardCopy();
    }

    /**
    * Gets and returns the color of the player whose turn it is.
    * @return the current player's color
    */
    public PlayerColor getCurrentPlayerSign()
    {
        return model.getCurrentPlayerColor();
    }

    /**
     * Creates a new board layout based on the given type.
     * @param boardType the type of board to create (random, good for white, etc.)
     */
    public void createABoard(String boardType)
    {
        model.createBoard(boardType);
    }

    /**
     * Checks if a position is inside the board boundaries.
     * @param pos the position to check
     * @return true if the position is inside the board, false otherwise
     */
    public boolean isInsideBoard(Position pos)
    {
        return model.isInBoard(pos);
    }

    /**
     * Returns all current Amazon positions for the given player.
     * @param playerSign the color of the player
     * @return array of positions where the player's Amazons are located
     */
    public Position[] getAmazonPositions(PlayerColor playerSign)
    {
        return model.amazonsForPlayerSign(playerSign);
    }

    /**
     * Returns the position of only the free Amazons for the given player (the ones that can move)
     * @param playerSign the color of the player
     * @return array of free Amazon positions
     */
    public Position[] getFreeAmazonPositions(PlayerColor playerSign)
    {
        return model.freeAmazonsForPlayerSign(playerSign);
    }

    /**
     * Returns the positions that are reachable in *one* jump from the given position.
     * @param pos the starting position
     * @return array of positions that can be reached from the given position in a single jump
     */
    public Position[] getReachables(Position pos)
    {
        return model.getReachableSquares(pos);
    }

    /**
     * Finds the shootable squares after jumping from one square to another.
     * @param jumpsFrom the initial Amazon position
     * @param jumpsTo the new Amazon position after jump
     * @return array of positions where the Amazon can shoot an arrow
     */
    public Position[] getShootablesAfterJump(Position jumpsFrom, Position jumpsTo)
    {
        return model.shootablesAfterJump(jumpsFrom, jumpsTo);
    }

    /**
     * Plays the given move in the game.
     * @param moveToPlay the move to execute
     */
    public void playAMove(Move moveToPlay)
    {
        model.playMove(moveToPlay);
    }

    /**
     * Checks whether the game has ended.
     * @return true if the game is over, false otherwise
     */
    public boolean isTheGameOver()
    {
        return model.isGameOver();
    }

    /**
     * Returns the winner of the game, if the game is over.
     * @return the color of the winning player, or null if there's no winner yet
     */
    public PlayerColor getWinner()
    {
        return model.getWinner();
    }

    /**
     * Combines two arrays of positions into one.
     * @param arr1 the first array
     * @param arr2 the second array
     * @return a new array containing all elements of arr1 and arr2
     */
    public Position[] combineArrs(Position[] arr1, Position[] arr2)
    {
        return model.combineArrays(arr1, arr2);
    }

    /**
     * Retrieves all possible moves for the current player.
     * @return array of all possible moves
     */
    public Move[] getPossibleMoves()
    {
        return model.allPossibleMoves();
    }

    /**
     * Requests an AI-generated move based on the opponent type and current player.
     * @param opponent the type of AI opponent
     * @param playerSign the current player's color
     * @return the selected move from the AI
     */
    public Move getAIMove(OpponentType opponent, PlayerColor playerSign)
    {
        return model.getAiMove(opponent, playerSign);
    }

    /**
     * Creates a randomly generated board layout.
     */
    public void makeRandomBoard()
    {
        model.randomBoard();
    }
}
