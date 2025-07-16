package model;

import controller.Constants.*;
import controller.Position;
import controller.Move;

/**
 * Interface for the Model component in the MVC architecture.
 * Declares the essential methods for managing the game logic, board state,
 * and data manipulation in the Game of the Amazons.
 * @author OFIR AVIANI | 23.02.2025
 */

public interface IModel
{
    public void resetBoard();

    public CellContent[][] getBoardCopy();
 
    public PlayerColor getCurrentPlayerColor();

    public boolean isInBoard(Position pos);
 
    public Position[] amazonsForPlayerSign(PlayerColor playerSign);
    
    public Position[] freeAmazonsForPlayerSign(PlayerColor playerSign);

    public Position[] getReachableSquares(Position pos);

    public Position[] shootablesAfterJump(Position jumpsFrom, Position jumpsTo);

    public void playMove(Move moveToPlay);

    public boolean isGameOver();

    public void createBoard(String boardType);

    public PlayerColor getWinner();

    public Position[] combineArrays(Position[] arr1, Position[] arr2);

    public Move[] allPossibleMoves();

    public Move getAiMove(OpponentType opponent, PlayerColor playerSign);

    public void randomBoard();

}
