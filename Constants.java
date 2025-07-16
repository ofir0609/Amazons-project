package controller;
import java.awt.Font;
import java.awt.Color;

/**
 * Interface containing all constants used throughout the Game of Amazons project.
 * Includes board dimensions, starting positions, colors, fonts, and enums for game states.
  * @author OFIR AVIANI | 18.03.2025
 */

public interface Constants
{
    public static final int BOARD_ROWS = 10;
    public static final int BOARD_COLS = 10;
    public static final int WHITE_AMAZONS_START_UP[][] = {{0,3}, {0,6}, {3,0}, {3,9}};
    public static final int BLACK_AMAZONS_START_UP[][] = {{6,0}, {6,9}, {9,3}, {9,6}};
    public static final int WHITE_AMAZON_NUMBER = WHITE_AMAZONS_START_UP.length;
    public static final int BLACK_AMAZON_NUMBER = BLACK_AMAZONS_START_UP.length;

    // Array of 8 directions for amazon movement (orthogonal and diagonal):
    // down, up, right, left, down-right, up-left, down-left, up-right 
    public static final int DIRECTIONS[][] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1},  {1, -1}, {-1, 1}};

    // some constants for view
    public static final Font FONT_BUTTONS = new Font("Arial Unicode MS", Font.PLAIN, 15);
    public static final Font FONT_LABEL = new Font("Arial", Font.PLAIN, 20);
    public static final int ICON_SIZE = 50;
    public static final Color lightBrownBackGround = new Color(240, 220, 150);
    public static final Color darkBrownBackGround = new Color(100, 50, 20);
    public static final Color clickableBackGround = new Color(200, 150, 95);
    public static final Color greenAiMoveColor = new Color(30, 180, 60);


    // temporary variables for shorter and more organized board matrices
    public static final CellContent empty = CellContent.EMPTY; 
    public static final CellContent wh_am = CellContent.WHITE_AMAZON;
    public static final CellContent bl_am = CellContent.BLACK_AMAZON; 
    public static final CellContent wh_ar = CellContent.WHITE_ARROW;
    public static final CellContent bl_ar = CellContent.BLACK_ARROW; 


    public static final CellContent[][] goodForWhite = new CellContent[][] { 
        {empty, empty, empty, empty, wh_am, empty, empty, empty, empty, empty},
        {empty, empty, wh_ar, bl_ar, empty, empty, empty, empty, empty, empty},
        {empty, bl_am, empty, bl_ar, empty, empty, empty, empty, empty, empty},
        {empty, wh_ar, wh_ar, bl_ar, bl_ar, bl_am, wh_am, bl_ar, wh_ar, empty},
        {empty, empty, empty, empty, empty, wh_am, bl_am, empty, empty, empty},
        {empty, empty, empty, empty, wh_ar, wh_ar, empty, empty, empty, bl_ar},
        {empty, empty, empty, empty, empty, empty, wh_ar, empty, bl_ar, empty},
        {empty, empty, empty, empty, empty, empty, empty, empty, wh_ar, empty},
        {empty, empty, bl_am, bl_ar, wh_ar, empty, wh_am, empty, empty, empty},
        {empty, empty, empty, bl_ar, empty, empty, wh_ar, empty, empty, empty},
    };

    // go
    public static final CellContent[][] goodForBlack = new CellContent[][]
    { 
        {empty, empty, empty, empty, empty, empty, empty, bl_ar, empty, wh_ar},
        {empty, empty, empty, empty, empty, empty, bl_am, empty, empty, empty},
        {empty, wh_ar, empty, bl_ar, wh_ar, empty, wh_ar, bl_ar, wh_ar, wh_am},
        {empty, empty, empty, wh_ar, bl_ar, wh_am, bl_ar, bl_am, empty, empty},
        {empty, wh_am, empty, wh_ar, empty, empty, empty, empty, empty, wh_ar},
        {bl_ar, bl_am, bl_ar, bl_ar, empty, empty, empty, empty, empty, empty},
        {empty, empty, bl_ar, wh_ar, bl_ar, wh_am, empty, empty, empty, empty},
        {empty, empty, empty, wh_ar, wh_ar, bl_ar, empty, empty, empty, empty},
        {empty, empty, wh_ar, empty, empty, empty, bl_am, empty, empty, empty},
        {empty, empty, empty, empty, empty, empty, empty, empty, empty, empty},
    };

    // represents a player's color
    enum PlayerColor 
    {
        WHITE, 
        BLACK
    }

    // represents what is in a board cell
    enum CellContent {
        EMPTY,
        WHITE_AMAZON,
        BLACK_AMAZON,
        WHITE_ARROW,
        BLACK_ARROW
    }

    // represents different move phases
    enum MoveState {
        WAITING_FOR_SELECTION,
        WAITING_FOR_JUMPING,
        WAITING_FOR_SHOOTING
    }

    // representes the different types of opponent
    enum OpponentType{
        HUMAN,
        RANDOM,
        EASY,
        MEDIUM,
        HARD
    }
}
