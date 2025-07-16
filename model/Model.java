package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

//import controller.Controller;
import controller.Move;
import controller.Position;
import controller.Constants;
import static controller.Constants.PlayerColor.*;
import static controller.Constants.CellContent.*;
import static controller.Constants.OpponentType.*;


/**
 * The Model class implements the core game logic and board state management for the Game of the Amazons.
 * It acts as the Model in the MVC pattern.
 * The model maintains the game board, validates moves, and more.
 * @author OFIR AVIANI | 23.02.2025
 */

public class Model implements IModel, Constants
{
    //private Controller controller;
    private CellContent[][] boardState; //each square is empty or has white/black amazon/arrow
    private PlayerColor colorToPlayNow; // whose turn is it now

    /**
     * construcs a Model object
     */
    public Model()
    {
        boardState = new CellContent[BOARD_ROWS][BOARD_COLS];
        resetBoard();       
    }


    // wrapper for resetBoard()
    @Override
    public void resetBoard()
    {
        resetBoard(boardState);
    }

    /**
     * Resets the board to the default starting state - 
     * clears out the arrows, and places the amazons in their standard positions.
     */
    public void resetBoard(CellContent[][] board)
    {
        // clear board
        for (int row = 0; row < BOARD_ROWS; row++)
         {
             for (int col = 0; col < BOARD_COLS; col++)
             {
                boardState[row][col] = CellContent.EMPTY;
            }
        }

        // put the white and black amazon on the board
        for (int i = 0; i < BLACK_AMAZONS_START_UP.length; i++)
        {
            int row = BLACK_AMAZONS_START_UP[i][0], col = BLACK_AMAZONS_START_UP[i][1];
            board[row][col] = CellContent.BLACK_AMAZON;
        }
        for (int i = 0; i < WHITE_AMAZONS_START_UP.length; i++)
        {
            int row = WHITE_AMAZONS_START_UP[i][0], col = WHITE_AMAZONS_START_UP[i][1];
            board[row][col] = CellContent.WHITE_AMAZON;
        }
        
        colorToPlayNow = WHITE;
    }

    /**
     * Initializes the board based on a predefined board, or a randomized board.
     * Calls either randomBoard() (not shown) or copyPreparedBoard().
     */
    @Override
    public void createBoard(String boardType)
    {
        if (boardType.equals("random"))
            randomBoard();
        else
            copyPreparedBoard(boardType);
    }

    /**
     * Loads a board layout from defined options like board1 or board2.
     * Warns if the name is unrecognized.
     * @param boardType
     */
    public void copyPreparedBoard(String boardType)
    {
        CellContent[][] boardToCopy;
        if(boardType.equals("white"))
            boardToCopy = goodForWhite;
        else if(boardType.equals("black"))
            boardToCopy = goodForBlack;
        else
        {
            System.out.println("Board not found!");
            return;
        }

        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                boardState[i][j] = boardToCopy[i][j];
            }
        }
    }

    // wrapper for getBoardCopy()
    @Override
    public CellContent[][] getBoardCopy()
    {
        return getBoardCopy(boardState);
    }

    /**
     * returns a copy of the board
     */
    public CellContent[][] getBoardCopy(CellContent[][] board)
    {
        CellContent[][] copyOfBoard = new CellContent[BOARD_ROWS][BOARD_COLS];
        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                copyOfBoard[i][j] = board[i][j];
            }
        }
        return copyOfBoard;
    }

    /**
     * returns the color of the player that should play now
     */
    @Override
    public PlayerColor getCurrentPlayerColor()
    {
        return colorToPlayNow;
    }


    // wrapper of isInBoard()
    @Override
    public boolean isInBoard(Position pos)
    {
        int row = pos.getRow(), col = pos.getCol();
        return isInBoard(row, col);
    }

    /**
     * returns whether a position is inside the board or not
     * @param row row of Position
     * @param col column of Position
     * @return
     */
    public boolean isInBoard(int row, int col)
    {
        if(row < 0 || row >= BOARD_ROWS || col < 0 || col >= BOARD_COLS)
            return false;
        return true;
    }

    // wrapper of getReachableSquares()
    @Override
    public Position[] getReachableSquares(Position pos)
    {
        return getReachableSquares(boardState, pos);
    }

    /**
     * recieves a position, and finds all empty squares reachable from it in a straight line (like a queen in chess).
     */
    public Position[] getReachableSquares(CellContent[][] board, Position pos)
    {
        ArrayList<Position> reachableArrList = new ArrayList<Position>();
        int tempRow, tempCol;
        // idea by my twin brother Guy:
        int rowDiff, colDiff;
        for (int i = 0; i < 8; i++)
        {
            rowDiff = DIRECTIONS[i][0];
            colDiff = DIRECTIONS[i][1];
            tempRow = pos.getRow() + rowDiff;
            tempCol = pos.getCol() + colDiff;
            while(isInBoard(tempRow, tempCol) && board[tempRow][tempCol] == EMPTY)
            {
                reachableArrList.add(new Position(tempRow, tempCol));
                tempRow += rowDiff;
                tempCol += colDiff;
            }
        }
        
        Position[] reachableArr = new Position[reachableArrList.size()];
        for (int i = 0; i < reachableArr.length; i++)
            reachableArr[i] = reachableArrList.get(i);
        return reachableArr;
    }

    // wrapper for canReachFromTo()
    public boolean canReachFromTo(Position pos1, Position pos2)
    {
        return canReachFromTo(boardState, pos1, pos2);
    }


    /**
     * returns whether it is possible to move in one move from ony of the squares to the second one
     * (reachability is symetric, so if pos1 reaches pos2, then pos2 reaches pos1, and vice versa)
     * @param pos1 first position
     * @param pos2 second position
     * @return
     */
    public boolean canReachFromTo(CellContent[][] board, Position pos1, Position pos2)
    {
        Position reachables[] = getReachableSquares(board, pos1);
        return isPosInArr(reachables, pos2); // checking if the second position can be reached by the first one.
    }

    
    // wrapper for shootablesAfterJump
    @Override
    public Position[] shootablesAfterJump(Position jumpsFrom, Position jumpsTo)
    {
        return shootablesAfterJump(boardState, jumpsFrom, jumpsTo);
    }

    /**
     * returns to which squares an amazon can shoot after jumping from one square to another
     * assuming there is an amazon at jumpsFrom, and that it can jump to jumpTo
     * @return array of squares to shoot at
     */
    public Position[] shootablesAfterJump(CellContent[][] board, Position jumpsFrom, Position jumpsTo)
    {
        if(!(isInBoard(jumpsFrom) && isInBoard(jumpsTo)))
        {
            System.out.println("Unvalid values");
            return null;
        }
        CellContent[][] boardCopy = getBoardCopy(board);
        boardCopy[jumpsFrom.getRow()][jumpsFrom.getCol()] = EMPTY;
        return getReachableSquares(boardCopy, jumpsTo);
    }

    // wrapper for canShootAfterJump()
    public boolean canShootAfterJump(Position jumpsFrom, Position jumpsTo, Position shootsAt)
    {
        return canShootAfterJump(boardState, jumpsFrom, jumpsTo, shootsAt);
    }

    /**
     * Returns true if shooting to shootsAt is legal after jumping.
     */
    public boolean canShootAfterJump(CellContent[][] board, Position jumpsFrom, Position jumpsTo, Position shootsAt)
    {
        Position[] shootables = shootablesAfterJump(board, jumpsFrom, jumpsTo);
        if (isPosInArr(shootables, shootsAt))
            return true;
        return false;
    }

    /**
     * Checks if a specific Position exists in an array.
     */
    public boolean isPosInArr(Position arr[], Position pos)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if(arr[i].equals(pos))
                return true;
        }
        return false;
    }

    /**
     * Checks if all parts of a move (source, destination, and targer) are inside the board.
     * @return whether the move is only inside the board
     */
    public boolean isMoveOnlyInsideBoard(Move moveToPlay)
    {
        Position tempPos;
        
        tempPos = new Position(moveToPlay.getJumpsFromRow(), moveToPlay.getJumpsFromCol());
        if(!isInBoard(tempPos))
        {
            //System.out.println("A try to reach amazons outside the board.");
            return false;
        }
        
        tempPos = new Position(moveToPlay.getJumpsToRow(), moveToPlay.getJumpsToCol());
        if(!isInBoard(tempPos))
        {
            //System.out.println("A try to send amazons outside the board.");
            return false;
        }

        tempPos = new Position(moveToPlay.getShootsAtRow(), moveToPlay.getShootsAtCol());
        if(!isInBoard(tempPos))
        {
            //System.out.println("A try to shoot to outside of the board.");
            return false;
        }

        return true;
    }

    // wrapper for isLegalMove()
    public boolean isLegalMove(Move moveToPlay)
    {
        return isLegalMove(boardState, colorToPlayNow, moveToPlay);
    }

    /**
     * Checkes if a move is legal on a given board with the specified player turn.
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegalMove(CellContent[][] board, PlayerColor colorToPlay, Move moveToPlay)
    {
        if(moveToPlay == null)
        {
            System.out.println("Move is null!");
            return false;
        }

        System.out.println("\n" + moveToPlay);

        if(!isMoveOnlyInsideBoard(moveToPlay))
        {
            System.out.println(" One or more of the positions of the move was/were outside the board");
            return false;
        }

        CellContent squareSign = board[moveToPlay.getJumpsFromRow()][moveToPlay.getJumpsFromCol()];
        if(squareSign!= BLACK_AMAZON && squareSign != WHITE_AMAZON) 
        {
            System.out.println(" Illegal move. There is no amazon in this square.");
            return false;
        }

        if(squareSign != playerSignToAmazonSign(colorToPlay))
        {
            // System.out.println(squareSign.name());
            System.out.println(" Illegal move. Other's player turn.");
            return false;
        }

        Position jumpFrom = new Position (moveToPlay.getJumpsFromRow(), moveToPlay.getJumpsFromCol());
        Position jumpsTo = new Position(moveToPlay.getJumpsToRow(), moveToPlay.getJumpsToCol());
        if(!canReachFromTo(jumpFrom, jumpsTo))
        {
            System.out.println(" Illegal move. Amazon cannot jump to that square.");
            return false;
        }

        Position shootsAt = new Position(moveToPlay.getShootsAtRow(), moveToPlay.getShootsAtCol());
        if(!canShootAfterJump(jumpFrom, jumpsTo, shootsAt))
        {
            System.out.println(" Illegal move. Amazon cannot shoot at that square.");
            return false;
        }

        return true;
    }

    /**
     * returns the value of an arrow of the color of the player.
     */
    public CellContent playerSignToArrowSign(PlayerColor colorSign)
    {
        if (colorSign == WHITE)
            return WHITE_ARROW;
        return BLACK_ARROW;
    }

    /**
     * returns the value of an amazon of the color of the player.
     */
    public CellContent playerSignToAmazonSign(PlayerColor colorSign)
    {
        if (colorSign == WHITE)
            return WHITE_AMAZON;
        return BLACK_AMAZON;
    }

    /**
     * returns the player color of an amazon or an arrow.
     */
    public PlayerColor contentSignToColorSign(CellContent cellSign)
    {
        if (cellSign == WHITE_AMAZON || cellSign == WHITE_ARROW)
            return WHITE;
        if(cellSign == BLACK_AMAZON || cellSign == BLACK_ARROW)
            return BLACK;
        System.out.println("Error. asked color of empty square");
        return null;
    }

    // wrapper for amazonsForPlayerSign()
    @Override
    public Position[] amazonsForPlayerSign(PlayerColor playerSign)
    {
        return amazonsForPlayerSign(boardState, playerSign);
    }

    /**
     * Returns positions of all amazons for the current player.
     */
    public Position[] amazonsForPlayerSign(CellContent[][] board, PlayerColor playerSign)
    {
        Position[] amazonsArr;
        if(playerSign == WHITE)
            amazonsArr = new Position[WHITE_AMAZON_NUMBER];
        else
            amazonsArr = new Position[BLACK_AMAZON_NUMBER];
        // There are supposed to be 4 amazons for each color
        // But in the future different versions could be added, with different numbers of amazons
        int counter = 0;
        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                if(board[i][j] == playerSignToAmazonSign(playerSign))
                {
                    amazonsArr[counter] = new Position(i, j);
                    counter+=1;
                }
            }
        }
        return amazonsArr;
    }

    // wrapper for freeAmazonsForPlayerSign()
    @Override
    public Position[] freeAmazonsForPlayerSign(PlayerColor playerSign)
    {
        return freeAmazonsForPlayerSign(boardState, playerSign);
    }

    /**
     * Returns the positions of the free amazons for a given player, on the provided board state.
     */
    public Position[] freeAmazonsForPlayerSign(CellContent[][] board, PlayerColor playerSign)
    {
        ArrayList<Position> freeAmazonsList = new ArrayList<Position>();
        Position[] amazons = amazonsForPlayerSign(board, playerSign);
        for (int i = 0; i < amazons.length; i++)
        {
            if(getReachableSquares(board, amazons[i]).length != 0)
                freeAmazonsList.add(amazons[i]);
        }
        
        Position[] freeAmazonsArr = new Position[freeAmazonsList.size()];
        for (int i = 0; i < freeAmazonsList.size(); i++)
            freeAmazonsArr[i] = freeAmazonsList.get(i);
        return freeAmazonsArr;
    }

    /**
     * returns the other color for a given color. White -> Black, Black -> White
    */
    public PlayerColor otherColor(PlayerColor colorToPlayNow)
    {
        if (colorToPlayNow == WHITE)
            return BLACK;
        return WHITE;
    }

    // wrapper for isGameOver()
    @Override
    public boolean isGameOver()
    {
        return isGameOver(boardState, colorToPlayNow);
    }

    /**
     * returns whether the game is over or not
     */
    public boolean isGameOver(CellContent[][] board, PlayerColor colorToPlay)
    {
        Position[] amazonPositions = amazonsForPlayerSign(board, colorToPlay);
        for (int i = 0; i < amazonPositions.length; i++)
        {
            Position[] reachables = getReachableSquares(board, amazonPositions[i]);
            if(reachables.length!=0)
                return false; // the amazon can move
        }
        return true; // The current player cannot move any amazon, they lost
    }

    /**
     * returns the winning player if the game is over.
     * if the game isn't over, returns null
     */
    @Override
    public PlayerColor getWinner()
    {
        if(!isGameOver())
            return null; // no one won yet
        return otherColor(colorToPlayNow);
    }


    // wrapper for playMove()
    @Override
    public void playMove(Move moveToPlay)
    {
        playMove(boardState, colorToPlayNow, moveToPlay);
        colorToPlayNow = otherColor(colorToPlayNow);

        if(isGameOver())
        {
            System.out.println("Game Over | " + getWinner().name() + " won");
            return;
        }
    }

    /**
     * plays a move on a given board for a given player, and updates
     * @param board the board to nake the move on
     * @param colorToPlay the color of the one playing now
     * @param moveToPlay the move to be played
     */
    public void playMove(CellContent[][] board, PlayerColor colorToPlay, Move moveToPlay)
    {
        //printBoard(board);
        if(!isLegalMove(board, colorToPlay, moveToPlay))
        {
            System.out.println("An attempt to play an unvalid move.\n");
            return;
        }

        CellContent amazonSign = playerSignToAmazonSign(colorToPlay);
        CellContent arrowSign = playerSignToArrowSign(colorToPlay);

        board[moveToPlay.getJumpsFromRow()][moveToPlay.getJumpsFromCol()] = EMPTY;
        board[moveToPlay.getJumpsToRow()][moveToPlay.getJumpsToCol()] = amazonSign;
        board[moveToPlay.getShootsAtRow()][moveToPlay.getShootsAtCol()] = arrowSign;

        System.out.println("Move played successfully!\n");
    }

    // wrapper for printBoard()
    public void printBoard()
    {
        printBoard(boardState);
    }

    /**
     * prints the board - used for debugging the code
     * @param board the board to print
     */
    public void printBoard(CellContent[][] board)
    {
        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                switch (board[i][j])
                {
                    case WHITE_AMAZON:
                        System.out.print("WH_AMAZON  "); break;
                    case BLACK_AMAZON:
                        System.out.print("BL_AMAZON  "); break;
                    case WHITE_ARROW:
                        System.out.print("WH_ARROW   "); break;
                    case BLACK_ARROW:
                        System.out.print("BL_ARROW   "); break;
                    case EMPTY:
                        System.out.print("EMPTY      "); break;
                }
            }    
            System.out.println();
        }
    }

    // wrapper for movesForAmazon()
    public Move[] movesForAmazon(Position pos)
    {
        return movesForAmazon(boardState, pos);
    }

    /**
     * calculates and returns all the possible moves for a given amazon
     * @param board the board to check
     * @param fromPos the position of the amazon
     * @return array of possible moves
     */
    public Move[] movesForAmazon(CellContent[][] board, Position fromPos)
    {
        ArrayList<Move> possibleMovesList = new ArrayList<Move>();
        CellContent cell = board[fromPos.getRow()][fromPos.getCol()];
        if(cell != WHITE_AMAZON && cell != BLACK_AMAZON)
            return null;
        
        Position[] squaresToJumpTo = getReachableSquares(board, fromPos);
        for (int i = 0; i < squaresToJumpTo.length; i++)
        {
            Position[] shootablesForJump = shootablesAfterJump(board, fromPos, squaresToJumpTo[i]);
            for (int j = 0; j < shootablesForJump.length; j++)
            {
                Move tmpMove = new Move(fromPos, squaresToJumpTo[i], shootablesForJump[j]);
                possibleMovesList.add(tmpMove);
            }
        }

        Move[] movesArr = new Move[possibleMovesList.size()];
        for (int i = 0; i < possibleMovesList.size(); i++)
            movesArr[i] = possibleMovesList.get(i);
        return movesArr;
    }

    /**
     * helper function for combining two Position arrays
     */
    @Override
    public Position[] combineArrays(Position[] arr1, Position[] arr2)
    {
        int len1 = arr1.length, len2 = arr2.length;
        Position[] combinedArr = new Position[len1 + len2];
        System.arraycopy(arr1, 0, combinedArr, 0, len1);
        System.arraycopy(arr2, 0, combinedArr, len1, len2);
        return combinedArr;
    }

    // wrapper for allPossibleMoves()
    @Override
    public Move[] allPossibleMoves()
    {
        return allPossibleMoves(boardState, colorToPlayNow);
    }

    /**
     * calculates and returns all the possible moves on a given board for a given player
     * @param board board to check
     * @param playerSign the player that should play now
     * @return array of moves that can be played
     */
    public Move[] allPossibleMoves(CellContent[][] board, PlayerColor playerSign)
    {
        ArrayList<Move> possibleMovesList = new ArrayList<Move>();
        Position[] amazons = amazonsForPlayerSign(playerSign);
        for (int i = 0; i < amazons.length; i++)
        {
            Move[] movesForAmazon = movesForAmazon(amazons[i]);
            for (int j = 0; j < movesForAmazon.length; j++)
            {
                possibleMovesList.add(movesForAmazon[j]);
            }
        }

        Move[] movesArr = new Move[possibleMovesList.size()];
        for (int i = 0; i < possibleMovesList.size(); i++)
            movesArr[i] = possibleMovesList.get(i);
        return movesArr;
    }

    /**
     * Gets the AI move for a given opponent difficulty level.
     */
    @Override
    public Move getAiMove(OpponentType opponentType, PlayerColor playerSign)
    {
        switch (opponentType)
        {
            case RANDOM:
                return randAiMove(boardState, playerSign);
            case EASY:
                return plainAiMove(boardState, EASY, playerSign);
            case MEDIUM:
                return plainAiMove(boardState, MEDIUM, playerSign);
            case HARD:
                return complexAiMove(boardState, playerSign);
        }
        return null;
    }

    /**
     * finds a random legal move for a given board and player
     * @param board the board to check
     * @param playerSign the player that should play
     * @return a random legal move
     */
    public Move randAiMove(CellContent[][] board, PlayerColor playerSign)
    {
        Move[] possibleMoves = allPossibleMoves(board, playerSign);
        if(possibleMoves.length == 0)
            return null;
        int rand = new Random().nextInt(possibleMoves.length);
        Move chosenMove = possibleMoves[rand];
        return chosenMove;
    }

    /**
     * finds the move with the best score in an array
     * @param moveArr array of scored moves
     * @return the best move
     */
    public Move bestMove (Move[] moveArr)
    {
        if(moveArr == null || moveArr.length == 0) // shouldn't happen
            return null;
        Move bestOne = moveArr[0];
        for (int i = 1; i < moveArr.length; i++)
        {
            if(moveArr[i].getScore() > bestOne.getScore())
                bestOne = moveArr[i];
        }
        return bestOne;
    }

    /**
     * Evaluates and chooses the best move using a static evaluation function based on the opponent's difficulty.
     * For either EASY or MEDIUM computer players
     * @param board the board to check
     * @param opponentType the type of the opponent (easy/medium)
     * @param playerToPlay the color of the player that should play now
     * @return the best Move found by the check
     */
    public Move plainAiMove(CellContent[][] board, OpponentType opponentType, PlayerColor playerToPlay)
    {
        Move[] possibleMoves = allPossibleMoves(board, playerToPlay);
        if(possibleMoves.length == 0)
            return null;
        int emptySquares = countEmptySquares(board);
        CellContent[][] boardCopy = getBoardCopy();
        int[][][] pathsMat = pathsForWholeBoard(board);
        Position[] playerAmazons = amazonsForPlayerSign(board, playerToPlay);
        Position[] opponentAmazons = amazonsForPlayerSign(board, otherColor(playerToPlay));


        for (int i = 0; i < possibleMoves.length; i++)
        {
            doMoveOnBoard(boardCopy, playerToPlay, possibleMoves[i]);
            updatePathsMatrix(boardCopy, pathsMat, possibleMoves[i]);
            updateAmazonArr(playerAmazons, possibleMoves[i]);
            
            if(opponentType == EASY)
                possibleMoves[i].setScore(easyEvaluationFunction(boardCopy, pathsMat, playerAmazons, opponentAmazons, playerToPlay));
            else // opponent is MEDIUM
                possibleMoves[i].setScore(complexEvaluationFunction(boardCopy, pathsMat, playerAmazons, opponentAmazons, emptySquares - 1, playerToPlay, false));         
            
            undoMoveOnBoard(boardCopy, playerToPlay, possibleMoves[i]);
            undoUpdateAmazonArr(playerAmazons, possibleMoves[i]);
            undoUpdatePathsMatrix(boardCopy, pathsMat, possibleMoves[i]);
        }

        return bestMove(possibleMoves);
    }


    /**
     * Performs a deeper evaluation of each move, by simulating the opponent’s best possible response.
     * @param board the board to check
     * @param playerToPlay the color of the player that should play now
     * @return the best Move found by the check
     */
    public Move complexAiMove(CellContent[][] board, PlayerColor playerToPlay)
    {
        Move[] possibleMoves = allPossibleMoves(board, playerToPlay);
        PriorityQueue<Move> moveQueue = new PriorityQueue<>(
            (m1, m2) -> Double.compare(m2.getScore(), m1.getScore()) // max-heap
        );

        if(possibleMoves.length == 0)
            return null;
        int emptySquares = countEmptySquares(board);
        CellContent[][] boardCopy = getBoardCopy();
        int[][][] pathsMat = pathsForWholeBoard(board);
        Position[] playerAmazons = amazonsForPlayerSign(board, playerToPlay);
        Position[] opponentAmazons = amazonsForPlayerSign(board, otherColor(playerToPlay));

        for (int i = 0; i < possibleMoves.length; i++)
        {
            doMoveOnBoard(boardCopy, playerToPlay, possibleMoves[i]);
            updatePathsMatrix(boardCopy, pathsMat, possibleMoves[i]);
            updateAmazonArr(playerAmazons, possibleMoves[i]);
            
            possibleMoves[i].setScore(complexEvaluationFunction(boardCopy, pathsMat, playerAmazons, opponentAmazons, emptySquares - 1, playerToPlay, false));         
            
            undoMoveOnBoard(boardCopy, playerToPlay, possibleMoves[i]);
            undoUpdateAmazonArr(playerAmazons, possibleMoves[i]);
            undoUpdatePathsMatrix(boardCopy, pathsMat, possibleMoves[i]);

            moveQueue.add(possibleMoves[i]);
        }

        int bestMovesCount = Math.min((moveQueue.size() + 1) / 2, 100);
        Move[] wellCheckedMoves = new Move[bestMovesCount];
        int counter = 0;
        for (int i = 0; i < bestMovesCount; i++)
        {
            Move checkedMove = moveQueue.poll();
            doMoveOnBoard(boardCopy, playerToPlay, checkedMove);
            if(isGameOver(boardCopy, otherColor(playerToPlay)))
            {
                checkedMove.setScore(2000); // the move will win
                wellCheckedMoves[counter++] = checkedMove;
            }
            else
            {
                // finding the smartest response
                Move bestResponse = bestOpponentResponse(board, playerToPlay, checkedMove, pathsMat, playerAmazons, opponentAmazons);
                double bestResponseScore = bestResponse.getScore();
                checkedMove.setScore(checkedMove.getScore() - bestResponseScore);
                wellCheckedMoves[counter++] = checkedMove;
            }
            undoMoveOnBoard(boardCopy, playerToPlay, checkedMove);
        }

        Move bestMoveFound = bestMove(wellCheckedMoves);
        doMoveOnBoard(boardCopy, playerToPlay, bestMoveFound);
        updatePathsMatrix(boardCopy, pathsMat, bestMoveFound);
        updateAmazonArr(playerAmazons, bestMoveFound);            
        complexEvaluationFunction(boardCopy, pathsMat, playerAmazons, opponentAmazons, emptySquares, playerToPlay, true);
        return bestMoveFound;
    }

    /**
     * Calculates the best possible response move for the opponent, 
     * treating it as the worst case for the current player.
     */
    public Move bestOpponentResponse(CellContent[][] boardCopy, PlayerColor playerToPlay, Move checkedMove, int[][][] pathsMat, Position[] playerAmazons, Position[] opponentAmazons)
    {
        doMoveOnBoard(boardCopy, playerToPlay, checkedMove);
        updatePathsMatrix(boardCopy, pathsMat, checkedMove);
        updateAmazonArr(playerAmazons, checkedMove);
        
        // worst for the player, but best for the opponent playing it
        Move bestResponse = plainAiMove(boardCopy, MEDIUM, otherColor(playerToPlay));

        undoMoveOnBoard(boardCopy, playerToPlay, checkedMove);
        undoUpdateAmazonArr(playerAmazons, checkedMove);
        undoUpdatePathsMatrix(boardCopy, pathsMat, checkedMove);

        return bestResponse;
    }

    /**
     * counts the empty squares in a board
     */
    public int countEmptySquares(CellContent[][] board)
    {
        int count = 0;
        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                if(board[i][j] == EMPTY)
                    count+=1;
            }
        }
        return count;
    }

    /**
     * returns the opposite direction number of a direction
     * @param directionNum the "key" of the direction
     */
    public int oppositeDirectionNum(int directionNum)
    {
        switch (directionNum) 
        {
            case 0: return 1; // down -> up
            case 1: return 0; // up -> down
            case 2: return 3; // right -> left
            case 3: return 2; // left -> right

            case 4: return 5; // down-right -> up-left
            case 5: return 4; // up-left -> down-right
            case 6: return 7; // down-left -> up-right
            case 7: return 6; // up-right -> down-left
            
            default: throw new IllegalArgumentException("Invalid direction number");
        }
    }


    /**
     * performs a move on a board
     * removes amazon, places the amazon in the jumpTo, and places arrow at shootAt
     * @param board the board to change
     * @param color the color of the playing player
     * @param moveToPlay the move to perform
     */
    public void doMoveOnBoard(CellContent[][] board, PlayerColor color, Move moveToPlay)
    {
        board[moveToPlay.getJumpsFromRow()][moveToPlay.getJumpsFromCol()] = EMPTY;
        board[moveToPlay.getJumpsToRow()][moveToPlay.getJumpsToCol()] = playerSignToAmazonSign(color);
        board[moveToPlay.getShootsAtRow()][moveToPlay.getShootsAtCol()] = playerSignToArrowSign(color);
    }

    /**
     * undoes a move on a board
     * removes the arrow that was placed, removes the amazon that was places, and places an amazon in its original square
     * @param board the board that was changed
     * @param color the color of the player who played
     * @param moveToPlay the move to undo
     */
    public void undoMoveOnBoard(CellContent[][] board, PlayerColor color, Move moveToPlay)
    {
        board[moveToPlay.getShootsAtRow()][moveToPlay.getShootsAtCol()] = EMPTY;
        board[moveToPlay.getJumpsToRow()][moveToPlay.getJumpsToCol()] = EMPTY;
        board[moveToPlay.getJumpsFromRow()][moveToPlay.getJumpsFromCol()] = playerSignToAmazonSign(color);
    }

    /**
     * updates the paths matrix after a position was occupied
     * @param board the board of amazons
     * @param lengthsMat the matrix of paths
     * @param newPositionedRow the row of the new occupied square
     * @param newPositionedCol the column of the new occupied square
     */
    public void updatePathsPiecePositioned(CellContent[][] board, int[][][] lengthsMat, int newPositionedRow, int newPositionedCol)
    {
        for (int directionNum = 0; directionNum < 8; directionNum++)
        {    
            int[] direction = DIRECTIONS[directionNum];
            int oppositeDirNum = oppositeDirectionNum(directionNum);
            int tempRow = newPositionedRow + direction[0], tempCol = newPositionedCol + direction[1];

            // updating each square that could be reached by the new piece (the empty ones, and the occupied one)
            int freeSquares = 0;
            while (isInBoard(tempRow, tempCol) && board[tempRow][tempCol] == EMPTY)
            {
                lengthsMat[tempRow][tempCol][oppositeDirNum] = freeSquares;
                tempRow += direction[0];
                tempCol += direction[1];
                freeSquares++;
            }
            if(isInBoard(tempRow, tempCol))
            {
                lengthsMat[tempRow][tempCol][oppositeDirNum] = freeSquares;
            }
        }
    }


    /**
     * updates the paths matrix after a position was unoccupied ("freed")
     * @param board the board of amazons
     * @param lengthsMat the matrix of paths
     * @param newPositionedRow the row of the unoccupied square
     * @param newPositionedCol the column of the unoccupied square
     */
    public void updatePathsPieceRemoved(CellContent[][] board, int[][][] lengthsMat, int removedRow, int removedCol)
    {
        for (int directionNum = 0; directionNum < 8; directionNum++)
        {    
            int[] direction = DIRECTIONS[directionNum];
            int oppositeDirNum = oppositeDirectionNum(directionNum);
            int[] oppositeDir = DIRECTIONS[oppositeDirNum];
            int tempRow = removedRow, tempCol = removedCol;

            // iterating backwards - the most further empty square in the line
            while(isInBoard(tempRow+oppositeDir[0], tempCol+oppositeDir[1]) && board[tempRow + oppositeDir[0]][tempCol + oppositeDir[1]] == EMPTY)
            {
                tempRow += DIRECTIONS[oppositeDirNum][0];
                tempCol += DIRECTIONS[oppositeDirNum][1];
            }

            int freeSquares = 0;


            while (isInBoard(tempRow, tempCol) && board[tempRow][tempCol] == EMPTY)
            {
                lengthsMat[tempRow][tempCol][oppositeDirNum] = freeSquares;
                tempRow += direction[0];
                tempCol += direction[1];
                freeSquares++;
            }
            if(isInBoard(tempRow, tempCol))
            {
                lengthsMat[tempRow][tempCol][oppositeDirNum] = freeSquares;
            }
        }
    }

    /**
     * updates the paths matrix after a move was done on the board
     * used for efficietly getting the matrix after simulating a move
     * @param board the board after the move was performed
     * @param lengthsMat the matrix of paths
     * @param movePlayed the move that was played
     */
    public void updatePathsMatrix(CellContent[][] board, int[][][] lengthsMat, Move movePlayed)
    {
        updatePathsPieceRemoved(board, lengthsMat, movePlayed.getJumpsFromRow(), movePlayed.getJumpsFromCol());
        updatePathsPiecePositioned(board, lengthsMat, movePlayed.getJumpsToRow(), movePlayed.getJumpsToCol());
        updatePathsPiecePositioned(board, lengthsMat, movePlayed.getShootsAtRow(), movePlayed.getShootsAtCol());
    }

    /**
     * undoes the update of the paths matrix to before the move was played
     * used for efficietly getting the original matrix after simulating a move and changing the matrix
     * @param board the board before the move was performed
     * @param lengthsMat the matrix of paths
     * @param movePlayed the move that was played
     */
    public void undoUpdatePathsMatrix(CellContent[][] board, int[][][] lengthsMat, Move movePlayed)
    {
        updatePathsPieceRemoved(board, lengthsMat, movePlayed.getShootsAtRow(), movePlayed.getShootsAtCol());
        updatePathsPieceRemoved(board, lengthsMat, movePlayed.getJumpsToRow(), movePlayed.getJumpsToCol());
        updatePathsPiecePositioned(board, lengthsMat, movePlayed.getJumpsFromRow(), movePlayed.getJumpsFromCol());
    }

    /**
     * updates an array of amazons after a move was perfromed
     * @param playerAmazons array of positions of the player's amazons
     * @param movePlayed the move performed
     */
    public void updateAmazonArr(Position[] playerAmazons, Move movePlayed)
    {
        Position oldPos = new Position(movePlayed.getJumpsFromRow(), movePlayed.getJumpsFromCol());
        Position newPos = new Position(movePlayed.getJumpsToRow(), movePlayed.getJumpsToCol());
        for (int i = 0; i < playerAmazons.length; i++)
        {
            if(playerAmazons[i].equals(oldPos))
                playerAmazons[i] = newPos;
        }
    }

    /**
     * undoing the update of the amazons array to before the move was played
     * @param playerAmazons array of positions of the player's amazons
     * @param movePlayed the move performed
     */
    public void undoUpdateAmazonArr(Position[] playerAmazons, Move movePlayed)
    {
        Position oldPos = new Position(movePlayed.getJumpsFromRow(), movePlayed.getJumpsFromCol());
        Position newPos = new Position(movePlayed.getJumpsToRow(), movePlayed.getJumpsToCol());
        for (int i = 0; i < playerAmazons.length; i++)
        {
            if(playerAmazons[i].equals(newPos))
                playerAmazons[i] = oldPos;
        }
    }

    /**
     * Returns all the positions located on the edges of the board,
     * including corners, but not twice.
     * @return An array of positions on the edge of the board.
     */
    public Position[] boardSquareEdges()
    {
        Position[] edgeSquares = new Position[2*BOARD_ROWS + 2 * BOARD_COLS - 4]; // (The corners shouldn't appear twice, hence " - 4")

        Position[] downSquares = startingSquaresOfLines(-1, 0);
        Position[] upSquares = startingSquaresOfLines(1, 0);
        Position[] rightSquares = startingSquaresOfLines(0, -1);
        Position[] leftSquares = startingSquaresOfLines(0, 1);

        int index = 0;
        System.arraycopy(downSquares, 0, edgeSquares, index, BOARD_COLS);
        index += BOARD_COLS;
        System.arraycopy(upSquares, 0, edgeSquares, index, BOARD_COLS);

        index += BOARD_COLS;
        System.arraycopy(rightSquares, 1, edgeSquares, index, BOARD_ROWS - 2); // avoiding two corners that are already in edgeSquares
        index += BOARD_ROWS - 2;
        System.arraycopy(leftSquares, 1, edgeSquares, index, BOARD_ROWS - 2);

        return edgeSquares;
    }

    /**
     * Computes the starting squares for "sweeping along" a line in a given direction.
     * these squares are important for calculating and recalculating of the paths matrix.
     */
    public Position[] startingSquaresOfLines(int directionRow, int directionCol)
    {
        int rowOfBorder = directionRow == 1 ? 0 : BOARD_ROWS - 1; // upper or lower row of board
        int colOfBorder = directionCol == 1 ? 0 : BOARD_COLS - 1; // left or right column of board

        if(directionRow == 0) // horizontal
        {
            // returning the left squares for right direction, or the right squares for left direction
            Position[] edges = new Position[BOARD_ROWS];
            for (int i = 0; i < BOARD_ROWS; i++)
                edges[i] = new Position(i, colOfBorder);
            return edges;
        }

        if(directionCol == 0) // vertical
        {
            // returning the bottom squares for up direction, or the top squares for down direction
            Position[] edges = new Position[BOARD_COLS];
            for (int i = 0; i < BOARD_COLS; i++)
                edges[i] = new Position(rowOfBorder, i);
            return edges;
        }

        // diagonal
        // There's a corner square shared by the horizontal and vertical lines. It is only put once is the array
        Position[] edges = new Position[BOARD_ROWS + BOARD_COLS - 1];
        int counter = 0;

        // left/right line (horizontal)
        for (int i = 0; i < BOARD_ROWS; i++) // horizontal squares (including corners)
            edges[counter++] = new Position(i, colOfBorder);

        // upper/bottom line (vertical)
        // the corner shared with horizontal line is already in the array
        // if the direction is to the right, we start from col of 1 (instead of 0)
        // if the direction is to the left, we end in col 1 before the last (instead of the last)
        int leftStart = directionCol == 1 ? 1 : 0; // if the direction is to the right
        for (int i = leftStart; i < BOARD_COLS - 1 + leftStart; i++)
            edges[counter++] = new Position(rowOfBorder, i);

        return edges;
    }

    /**
     * Updates the access paths matrix for a line of squares starting at the given position, in the specified direction.
     * Counts consecutive empty squares and updates the metrix accordingly.
     * @param board
     * @param accessMatrix
     * @param directionNum
     * @param pos
     */
    private void updateLineInPathsMatrix(CellContent[][] board, int[][][] accessMatrix, int directionNum, Position pos)
    {
        // the square of row,col must be on one of the border lines, and the direction must match that line.
        int freeSquares = 0;
        int oppositeDirectionNum = oppositeDirectionNum(directionNum);
        int tempRow = pos.getRow(), tempCol = pos.getCol();
        while(isInBoard(tempRow, tempCol))
        {
            accessMatrix[tempRow][tempCol][oppositeDirectionNum] = freeSquares;
            if(board[tempRow][tempCol] == EMPTY)
                freeSquares += 1;
            else
                freeSquares = 0;

            tempRow += DIRECTIONS[directionNum][0];
            tempCol += DIRECTIONS[directionNum][1];
        }
    }

    /**
     * builds a new matrix of paths for a given board
     * @param board the board to check
     * @return the matrix of paths
     */
    public int[][][] pathsForWholeBoard(CellContent[][] board)
    {
        int pathsMat[][][] = new int [BOARD_ROWS][BOARD_COLS][8];
        // like calling haveAccessTo() for each square, but more efficiently
        // Going over every horizontal, vertical, and diagonal line, both ways, 
        // and counting how many empty squares were checked.
        recalculateEntirePathMatrix(board, pathsMat);
        return pathsMat;
    }

    /**
     * recalculate the entire matrix of paths
     */
    public void recalculateEntirePathMatrix(CellContent[][] board, int[][][] pathsMat)
    {
        Position[] edgeSquares = boardSquareEdges();
        for (int i = 0; i < edgeSquares.length; i++)
        {
            int row = edgeSquares[i].getRow(), col = edgeSquares[i].getCol();
            for (int k = 0; k < 8; k++)
            {
                pathsMat[row][col][k] = 0;
            }
        }

        for (int directionNum = 0; directionNum < 8; directionNum++)
        {
            int[] direction = DIRECTIONS[directionNum];
            Position[] startingSquares = startingSquaresOfLines(direction[0], direction[1]);
            for (int j = 0; j < startingSquares.length; j++)
            {
                updateLineInPathsMatrix(board, pathsMat, directionNum, startingSquares[j]);
            }
        }
    }

    /**
     * Returns the mobility values for each Amazon on the board.
     * It is an 8-long array of ints representing the legnths of paths 
     * @param pathsMat the matrix of paths
     * @param amazons an array of amazons to check
     * @return an array of paths arrays
     */
    public int[][] mobilitiesForAmazons(int[][][] pathsMat, Position[] amazons)
    {
        int[][] mobilities = new int[amazons.length][];
        for (int i = 0; i < mobilities.length; i++)
        {
            mobilities[i] = pathsMat[amazons[i].getRow()][amazons[i].getCol()];
        }
        return mobilities;
    }

    /**
     * Computes a heuristic mobility score based on how many empty squares each Amazon can move to.
     * value is between 0 to 27
     */
    public double mobilityScore(int[][] pathsForAmazons)
    {
        double score = 0;
        for (int i = 0; i < pathsForAmazons.length; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if(j < 4) // vertical or horizontal direction
                    score += Math.sqrt(pathsForAmazons[i][j]);
                else
                    score += 1.25 * Math.sqrt(pathsForAmazons[i][j]);
            }
        }

        return score;
    }

    /**
     * evaluates how good a board is for a player, before their opponent plays.
     * If the game is over for either player, returns ±2000 as a terminal score.
     * Otherwise, calculates the score based on mobility advantage.
     * @param board the board after simulating the move
     * @param pathsMat the matrix of paths after simulating the move
     * @param playerAms the positions of amazons after simulating the move
     * @param opponentAms the positions of oponnent amazons
     * @param playerToPlay the color of the playing player
     * @return a score between -2000 and 2000
     */
    public double easyEvaluationFunction(CellContent[][] board, int[][][] pathsMat, Position[] playerAms, Position[] opponentAms, PlayerColor playerToPlay)
    {
        PlayerColor opponentPlayer = otherColor(playerToPlay); // going to play now
        if(isGameOver(board, opponentPlayer))
            return 2000; // the opponent loses now
        if (isGameOver(board, playerToPlay))
            return -2000; // current player will lose next move

        
        int playerMobility[][] = mobilitiesForAmazons(pathsMat, playerAms);
        int opponentMobility[][] = mobilitiesForAmazons(pathsMat, opponentAms);

        double playerMobilityScore = mobilityScore(playerMobility);
        double opponentMobilityScore = mobilityScore(opponentMobility);

        double score = playerMobilityScore - opponentMobilityScore ;
        // System.out.println("player mobility: " + playerMobilityScore + ", opponent mobility: " + opponentMobilityScore + ", score:" + score);
        return score;
    }


    /**
     * for each square, we save whether the player can reach it with one or more queen jumps
     * implemented with a BFS algorithm of queen-like jumps
     * @param allPaths the matrix of paths
     * @param board the board
     * @param playerAmazons the positions of the playing player
     * @return the matrix of booleans
     */
    public boolean[][] reachableTerritory(int[][][] allPaths, CellContent[][] board, Position playerAmazons[])
    {
        boolean reachable[][] = new boolean[BOARD_ROWS][BOARD_COLS];
        Queue<Position> squaresToCheck = new LinkedList<Position>();

        for (int i = 0; i < playerAmazons.length; i++)
        {
            squaresToCheck.add(playerAmazons[i]);
            while(!squaresToCheck.isEmpty())
            {
                Position square = squaresToCheck.remove();
                for (int j = 0; j < 8; j++)
                {
                    int tempRow = square.getRow(), tempCol = square.getCol();
                    int[] directions = DIRECTIONS[j];
                    for (int k = 0; k < allPaths[tempRow][tempCol][j]; k++)
                    {
                        tempRow += directions[0];
                        tempCol += directions[1];
                        
                        if(reachable[tempRow][tempCol] == false)
                        {
                            reachable[tempRow][tempCol] = true;
                            squaresToCheck.add(new Position(tempRow, tempCol));
                        }
                        //if (!isInBoard(tempRow, tempCol)) break;
                    }
                }
            }
        }

        return reachable;
    }

    /**
     * Calculates how many squares are reachable by each player, 
     * and how many are exclusively reachable (not reachable by the opponent).
     * @param playerTerritory a matrix representing which squares can be reached by the player
     * @param opponentTerritory a matrix representing which squares can be reached by the opponent
     * @return an array of 4 values:
     * stats[0] Squares reachable by the player
     * stats[1]: Squares exclusively reachable by the player 
     * stats[2]: Squares reachable by the opponent 
     * stats[3]: Squares exclusively reachable by the opponent
     */
    public int[] reachabilityStats(boolean[][] playerTerritory, boolean[][] opponentTerritory)
    {
        int[] stats = new int[4]; // playerReachableCount, playerExclusiveCount, opponentReachableCount, opponentExclusiveCount
        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                if(playerTerritory[i][j])
                {
                    stats[0]++;
                    if(!opponentTerritory[i][j])
                        stats[1]++;
                }
                if(opponentTerritory[i][j])
                {
                    stats[2]++;
                    if(!playerTerritory[i][j])
                        stats[3]++;
                }
            }
        }

        return stats;
    }

    /**
     * soon to be implemented
     * counts how many amazons are trapped in a small area (small in relation to the free squares)
     * @param board the board
     * @param pathsMat the matrix of paths
     * @param playerAms the positions of the player's amazons
     * @param emptySquares how many empty squares
     * @return the number of amazons trapped in a small area
     */
    public int countTrappedInUnderThan(CellContent[][] board, int[][][] pathsMat, Position[] playerAms, int minimum)
    {
        int count = 0;
        for (int i = 0; i < playerAms.length; i++)
        {
            if(isTrappedInUnderThan(board, pathsMat, playerAms[i], minimum))
                count++;
        }
        return count;
    }

    public boolean isTrappedInUnderThan(CellContent[][] board, int[][][] pathsMat, Position amazon, int minimum)
    {
        int reachables = 0;
        boolean reachabilityMatrix[][] = new boolean[BOARD_ROWS][BOARD_COLS];
        Queue<Position> squaresToCheck = new LinkedList<Position>();

        squaresToCheck.add(amazon);
        while(reachables < minimum && !squaresToCheck.isEmpty())
        {
            Position square = squaresToCheck.remove();
            for (int j = 0; j < 8; j++)
            {
                int tempRow = square.getRow(), tempCol = square.getCol();
                int[] directions = DIRECTIONS[j];
                for (int k = 0; k < pathsMat[tempRow][tempCol][j]; k++)
                {
                    tempRow += directions[0];
                    tempCol += directions[1];
                    
                    if(reachabilityMatrix[tempRow][tempCol] == false)
                    {
                        reachables++;
                        reachabilityMatrix[tempRow][tempCol] = true;
                        squaresToCheck.add(new Position(tempRow, tempCol));
                    }
                    //if (!isInBoard(tempRow, tempCol)) break;
                }
            }
        }

        if (reachables < minimum)
            return true;
        return false;
    }

    /**
     * Main board evaluation function for the AI.
     * It gives a score for how good the board is for the player.
     * @param board the board to check
     * @param pathsMat the matrix of paths
     * @param playerAms array of the player's amazon positions
     * @param opponentAms array of the opponent's amazon positions
     * @param emptySquares the number of empty squares
     * @param playerToPlay the player that should play now
     * @return a score between -2000 and 2000
     */
    public double complexEvaluationFunction(CellContent[][] board, int[][][] pathsMat, Position[] playerAms, Position[] opponentAms, int emptySquares, PlayerColor playerToPlay, boolean debugPrint)
    {
        PlayerColor opponentPlayer = otherColor(playerToPlay); // going to play now
        if(isGameOver(board, opponentPlayer))
            return 2000; // the opponent loses now
        if (isGameOver(board, playerToPlay))
            return - 2000; // current player will lose next move

        int playerReachableCount = emptySquares, opponentReachableCount = emptySquares;
        int playerExclusiveCount = 0, opponentExclusiveCount = 0;
        int playerTrappedInSmallArea = 0, opponentTrappedInSmallArea = 0;

        // The following test are not relevant if the board is not divided.
        // They check which squares can be reached by each player, 
        // and by that it counts reachable ssquares, and exclusive squares.
        // It is also counts how many amazons of each player are trapped in a small area

        if(!isAllOneTerritory(pathsMat, board, emptySquares, playerAms, opponentAms))
        {
            // Matrix of booleans that represent whether or not a square can be reach by the player
            // The same, just for the opponent
            boolean[][] playerTerritory = reachableTerritory(pathsMat, board, playerAms);
            boolean[][] opponentTerritory = reachableTerritory(pathsMat, board, opponentAms);

            // the function finds the 4 following values, and returns them in an array
            int[] reachStats = reachabilityStats(playerTerritory, opponentTerritory);
            playerReachableCount = reachStats[0];
            playerExclusiveCount = reachStats[1];
            opponentReachableCount = reachStats[2];
            opponentExclusiveCount = reachStats[3];

            // number of amazons trapped in a small area. 
            playerTrappedInSmallArea = countTrappedInUnderThan(board, pathsMat, playerAms, (int) (0.15 * emptySquares));
            opponentTrappedInSmallArea = countTrappedInUnderThan(board, pathsMat, opponentAms, (int) (0.15 * emptySquares));
        }

        // almost certain win: score 1900 - 2000
        if(playerExclusiveCount > opponentReachableCount)
            return 1900 + playerExclusiveCount - opponentReachableCount;

        // almost certain loss: score (-1900) - (-2000)
        if(opponentExclusiveCount > playerReachableCount)
            return - 1900 + opponentExclusiveCount - playerReachableCount;


        // mobility scores for each player
        double playerMobilityScore = mobilityScore(mobilitiesForAmazons(pathsMat, playerAms));
        double opponentMobilityScore = mobilityScore(mobilitiesForAmazons(pathsMat, opponentAms));

        // final score
        double score = 0.5 * (playerMobilityScore - 0.9 * opponentMobilityScore)
         + 2 * (playerReachableCount - opponentReachableCount)
         + 10 * (playerExclusiveCount - opponentExclusiveCount)
         + 50 * (opponentTrappedInSmallArea - playerTrappedInSmallArea);

        if(debugPrint)
        {
            System.out.println("mobility scores:" + playerMobilityScore + ", " + opponentMobilityScore);
            System.out.println("Reachable count:" + playerReachableCount + ", " + opponentReachableCount);
            System.out.println("Exclusive count:" + playerExclusiveCount + ", " + opponentExclusiveCount);
            System.out.println("Trapped in small area:" + playerTrappedInSmallArea + ", " + opponentTrappedInSmallArea);
            System.out.println("Score:" + score);
        }

        return score;
    }

    /**
     * soon to be implemented
     * checkes if all the amazons of both players are in the same area
     * used for efficiency - if all are in the same area, no need to check which color can reach each square,
     * since for each square it will be either true or false for both of them
     * @param pathsMat the matrix of paths
     * @param board the board
     * @param playerAms the amazons of the player
     * @param opponentAms the amazons of the opponent
     * @return whether or not all the amazons are in the same area
     */
    private boolean isAllOneTerritory(int[][][] pathsMat, CellContent[][] board, int emptySquares, Position[] playerAms, Position[] opponentAms)
    {
        // finding an empty square
        int startRow = 0, startCol = 0;
        while(startRow < BOARD_ROWS)
        {
            if (board[startRow][startCol] == EMPTY)
                break;

            startCol++;
            if(startCol == BOARD_COLS)
            {
                startCol = 0;
                startRow++;
            }
        }


        Position emptySquare = new Position(startRow, startRow);
        int emptyChecked = 1;
        boolean reachable[][] = new boolean[BOARD_ROWS][BOARD_COLS];
        reachable[startRow][startCol] = true;
        Queue<Position> squaresToCheck = new LinkedList<Position>();
        squaresToCheck.add(emptySquare);
        
        // checking if all the free squares are connected
        while(!squaresToCheck.isEmpty())
        {
            Position square = squaresToCheck.remove();
            for (int j = 0; j < 8; j++)
            {
                int tempRow = square.getRow(), tempCol = square.getCol();
                int[] directions = DIRECTIONS[j];
                for (int k = 0; k < pathsMat[tempRow][tempCol][j]; k++)
                {
                    tempRow += directions[0];
                    tempCol += directions[1];
                    
                    if(reachable[tempRow][tempCol] == false)
                    {
                        emptyChecked++;
                        reachable[tempRow][tempCol] = true;
                        squaresToCheck.add(new Position(tempRow, tempCol));
                    }
                    if (!isInBoard(tempRow, tempCol)) break;
                }
            }
        }

        if(emptyChecked != emptySquares)
            return false; // not all free squares can be reached from the starting square

        // checking no amazon is isolated
        for (int i = 0; i < playerAms.length; i++)
        {
            if(isIsolatedAmzon(pathsMat, playerAms[i]))
                return false;
        }
        for (int i = 0; i < opponentAms.length; i++)
        {
            if(isIsolatedAmzon(pathsMat, playerAms[i]))
                return false;
        }

        return true;
    }

    /**
     * checks whether or not an amazon (or a cell in general) is isolated
     * @param pathsMat matrix of paths
     * @param amazon position of the amazon
     */
    public boolean isIsolatedAmzon(int[][][] pathsMat, Position amazon)
    {
        int[] amazonsPaths = pathsMat[amazon.getRow()][amazon.getCol()];
        for (int j = 0; j < 8; j++)
        {
            if(amazonsPaths[j] != 0)
                return false;
        }
        return true; // amazon is isolated
    }


    /**
     * prints the matrix of paths - used for debugging the code
     */
    public void printPaths(int[][][] pathsMat)
    {
        System.out.println("The directions are in this order:");
        System.out.println("down, up, right, left, down-right, up-left, down-left, up-right");
        for (int i = 0; i < pathsMat.length; i++)
        {
            System.out.print("\ndown, up, right, left, down-right, up-left, down-left, up-right");
            for (int j = 0; j < pathsMat[0].length; j++)
            {
                System.out.print("\n[row- " + i + ", col- " + j + "]: ");
                for (int k = 0; k < 8; k++)
                {
                    System.out.print(pathsMat[i][j][k] + ", ");
                }
            }
            System.out.println();
        }
    }

    /**
     * prints the matrix of paths in a different way - used for debugging the code
     */
    public void printPaths2(int[][][] pathsMat)
    {
        System.out.println("The directions are in this order:");
        String[] directions = {"down", "up", "right", "left", "down-right", "up-left", "down-left", "up-right"};
        for (int i = 0; i < 8; i++)
        {
            System.out.print("\n" + directions[i]);
            for (int j = 0; j < pathsMat.length; j++)
            {
                System.out.println();
                for (int k = 0; k < pathsMat[0].length; k++)
                {
                    System.out.print(pathsMat[j][k][i] + ", ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Creates a random board state, placing Amazons and arrows randomly.
     * The board is random, and may be unreachable in an actual game due to movement rules
     */
    public void randomBoard()
    {
        // The amazons are put randomally on the board
        // Arrows added randomally
        // The random board is not necessarily possible to get to in a game
        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                boardState[i][j] = EMPTY;
            }
        }

        int whiteAmazons = WHITE_AMAZON_NUMBER, blackAmazons = BLACK_AMAZON_NUMBER;
        int amazonlessCells = BOARD_ROWS * BOARD_COLS - whiteAmazons - blackAmazons;
        int arrowsNumber;
        if(amazonlessCells > 0)
            arrowsNumber = new Random().nextInt(amazonlessCells - 40) + 20;
        else
            arrowsNumber = 0; // However, this situation is not supposed to happen in a board...

        int blackArrows = arrowsNumber/2;
        int whiteArrows = arrowsNumber - blackArrows;

        ArrayList<Integer> freeCells = new ArrayList<Integer>();
        for (int i = 0; i < BOARD_ROWS * BOARD_COLS; i++)
            freeCells.add(i);

        int[] contentNumbers = {blackAmazons, whiteAmazons, blackArrows, whiteArrows};
        CellContent[] contentSigns = {BLACK_AMAZON, WHITE_AMAZON, BLACK_ARROW, WHITE_ARROW};

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < contentNumbers[i]; j++)
            {
                int random = new Random().nextInt(freeCells.size());
                int randomCell = freeCells.get(random);
                freeCells.remove(Integer.valueOf(randomCell));
                boardState[randomCell/BOARD_ROWS][randomCell%BOARD_ROWS] = contentSigns[i];
            }
        }

        if(whiteArrows == blackArrows)
            colorToPlayNow = WHITE;
        else
            colorToPlayNow = BLACK;
    }
}
