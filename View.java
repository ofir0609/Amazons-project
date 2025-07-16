package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Image;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import controller.Move;
import controller.Controller;
import controller.Constants;
import controller.Position;
import static controller.Constants.PlayerColor.*;
import static controller.Constants.CellContent.*;
import static controller.Constants.MoveState.*;
import static controller.Constants.OpponentType.*;

/**
 * The View class is the graphical interface (GUI) component for the Game of the Amazons.
 * It acts as the View in the MVC pattern
 * It extends JFrame and implements IView, serving as the main window that users interact with.
 * This class is responsible for:
 * Building and presenting the game board
 * Handling user input (mouse clicks).
 * Managing buttons, labels, and menus.
 * @author OFIR AVIANI | 23.02.2025
 */

public class View extends JFrame implements IView, Constants
{

    private Controller controller;
    private JLabel lblInfo;

    private JButton[][] boardButtonMatrix;
    private JButton newGameButton, chooseOpponentButton, createBoardButton;

    private OpponentType opponent;

    private boolean wasGameStarted;
    private Position[] gameMovePositions; // The positions of the move - origin / destination / target
    private MoveState turnState; // what should the player do now - choose/move/shoot
    private ImageIcon icon_black_amazon, icon_white_amazon, icon_black_arrow, icon_white_arrow;

    /**
     * consturcts a View object
     * Initializes the window, loads icons, sets up the UI layout
     * It also handles interaction buttons and their behavior, and initializes the state of the game.
     */
    public View()
    {
        // load assets
        loadIcons();
        
        createOpponentSelectionRadioButtons();

        createBoardCreationMenuBar();

        JPanel actionsPanel = new JPanel(new FlowLayout());
        prepareActionsPanel(actionsPanel);

        JPanel pnlbuttons = new JPanel(new GridLayout(BOARD_ROWS, BOARD_COLS));
        prepareBoardButtons(pnlbuttons);
        
        lblInfo = new JLabel("  ?'s Turn");
        lblInfo.setFont(FONT_LABEL);

        // prepare window
        add(actionsPanel, BorderLayout.NORTH);
        add(pnlbuttons, BorderLayout.CENTER);
        add(lblInfo, BorderLayout.SOUTH);
        setTitle("Amazons-Game"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);


        // reset game variables 
        gameMovePositions = new Position[3];
        wasGameStarted = false;
        turnState = WAITING_FOR_SELECTION;
        opponent = HUMAN;
    }

    /**
     * Loads and resizes game piece and arrow images from resources.
     */
    private void loadIcons()
    {
        icon_black_amazon = loadImage("/assets/BlackAmazonRemovedBG.png", ICON_SIZE, ICON_SIZE);
        icon_white_amazon = loadImage("/assets/WhiteAmazonRemovedBG.png", ICON_SIZE, ICON_SIZE);
        icon_black_arrow = loadImage("/assets/BlackArrowRemovedBG.png", ICON_SIZE, ICON_SIZE);
        icon_white_arrow = loadImage("/assets/WhiteArrowRemovedBG.png", ICON_SIZE, ICON_SIZE);
    }

    /**
     * Builds a dialog with radio buttons to choose an opponent
     * (Human or AI of various difficulties)
     * Result is stored in opponent.
     */
    private void createOpponentSelectionRadioButtons()
    {
        chooseOpponentButton = new JButton("Choose Opponent");
        JRadioButton humanBtn = new JRadioButton("Human");
            JRadioButton randomBtn = new JRadioButton("Random");
            JRadioButton easyBtn = new JRadioButton("Easy");
            JRadioButton mediumBtn = new JRadioButton("Medium");
            JRadioButton hardBtn = new JRadioButton("Hard");

            ButtonGroup group = new ButtonGroup();
            group.add(humanBtn);
            group.add(randomBtn);
            group.add(easyBtn);
            group.add(mediumBtn);
            group.add(hardBtn);

            JPanel radioPanel = new JPanel(new GridLayout(0, 1));
            radioPanel.add(humanBtn);
            radioPanel.add(randomBtn);
            radioPanel.add(easyBtn);
            radioPanel.add(mediumBtn);
            radioPanel.add(hardBtn);

        chooseOpponentButton.addActionListener(e -> {
            setUIEnabled(false); // disable all UI
            int result = JOptionPane.showConfirmDialog(this, radioPanel, "Select Opponent Type", JOptionPane.OK_CANCEL_OPTION);
            setUIEnabled(true); // re-enable after dialog closes


            if (result == JOptionPane.OK_OPTION) {
                if (humanBtn.isSelected()) opponent = HUMAN;
                else if (randomBtn.isSelected()) opponent = RANDOM;
                else if (easyBtn.isSelected()) opponent = EASY;
                else if (mediumBtn.isSelected()) opponent = MEDIUM;
                else if (hardBtn.isSelected()) opponent = HARD;
            }
        });
    }

    /**
     * Creates a menu of defined boards to choose from
     * The menue is displayed when createBoardButton is pressed
     */
    private void createBoardCreationMenuBar()
    {
        createBoardButton = new JButton("create board");
        JMenuBar menuBar = new JMenuBar();
        JMenu boardMenu = new JMenu("Create Board");

        JMenuItem randomBoard = new JMenuItem("Random Board");
        JMenuItem goodForWhite = new JMenuItem("Good for White");
        JMenuItem goodForBlack = new JMenuItem("Good for Black");

        randomBoard.addActionListener(e -> preparedBoard("random"));
        goodForWhite.addActionListener(e -> preparedBoard("white"));
        goodForBlack.addActionListener(e -> preparedBoard("black"));

        boardMenu.add(randomBoard);
        boardMenu.add(goodForWhite);
        boardMenu.add(goodForBlack);
        menuBar.add(boardMenu);
        

        // Use JPanel to embed the menu bar inside the action panel
        JPanel boardMenuPanel = new JPanel(new BorderLayout());
        boardMenuPanel.add(menuBar, BorderLayout.CENTER);

        // actual button logic
        createBoardButton.addActionListener(e -> {
            setUIEnabled(false);
            JOptionPane.showMessageDialog(this, boardMenuPanel, "Select Board Type", JOptionPane.PLAIN_MESSAGE);
            setUIEnabled(true);
        });
    }

    /**
     * Adds "New Game", "Choose Opponent", and "Create Board" buttons to the top panel.
     * The function also sets the fonts of the buttons.
     * @param actionsPanel
     */
    private void prepareActionsPanel(JPanel actionsPanel)
    {
        newGameButton = new JButton("new game");
        newGameButton.addActionListener(e -> clearBoard());

        newGameButton.setFont(FONT_BUTTONS);
        chooseOpponentButton.setFont(FONT_BUTTONS);
        createBoardButton.setFont(FONT_BUTTONS);

        actionsPanel.add(newGameButton);
        actionsPanel.add(chooseOpponentButton);
        actionsPanel.add(createBoardButton);
    }

    /**
     * Initializes the game board buttons with light/dark colors and click behavior.
     */
    private void prepareBoardButtons(JPanel pnlbuttons)
    {
        boardButtonMatrix = new JButton[BOARD_ROWS][BOARD_COLS];
        for (int row = 0; row < BOARD_ROWS; row++)
        {
            for (int col = 0; col < BOARD_COLS; col++)
            {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(50, 50));
                btn.setName(row + "," + col);
                btn.setMargin(new Insets(0,0,0,0));
                
                btn.addActionListener(e-> {
                    String[] btnLocation = btn.getName().split(",");
                    int btnRow = Integer.parseInt(btnLocation[0]);
                    int btnCol = Integer.parseInt(btnLocation[1]);
                    boardButtonClicked(btnRow, btnCol);
                    });

                if((col+row)%2 == 0)
                    btn.setBackground(lightBrownBackGround);
                else
                    btn.setBackground(darkBrownBackGround);
                
                pnlbuttons.add(btn);   
                boardButtonMatrix[row][col] = btn;
            }
        }
    }

    /**
     * enables or disables buttons and board input.
     * Used to prevent interaction during dialogs or AI processing.
     */
    private void setUIEnabled(boolean enabled)
    {
        newGameButton.setEnabled(enabled);
        chooseOpponentButton.setEnabled(enabled && !wasGameStarted);
        createBoardButton.setEnabled(enabled && !wasGameStarted);
        
        if(enabled == true)
            updateClickability();
        else
        {
            for (int row = 0; row < BOARD_ROWS; row++)
            {
                for (int col = 0; col < BOARD_COLS; col++)
                {
                    boardButtonMatrix[row][col].setEnabled(false);
                }
            }
        }
    }

    /**
     * Links the controller so user actions in the view will trigger logic updates.
     */
    public void setController(Controller controller)
    {
       this.controller = controller;
    }

    /**
     * fetches an AI move from the controller and plays it.
     * Highlights the move with green background.
     */
    public void AIMove()
    {
        if(!controller.isTheGameOver())
        {
            Move aiMove = controller.getAIMove(opponent, controller.getCurrentPlayerSign());
            doMove(aiMove);
            boardButtonMatrix[aiMove.getJumpsFromRow()][aiMove.getJumpsFromCol()].setBackground(greenAiMoveColor);
            boardButtonMatrix[aiMove.getJumpsToRow()][aiMove.getJumpsToCol()].setBackground(greenAiMoveColor);
            boardButtonMatrix[aiMove.getShootsAtRow()][aiMove.getShootsAtCol()].setBackground(greenAiMoveColor);
            repaint();
        }
    }

    /**
     * Resets the UI and game state to start a new game,
     * and calls the controller to reset the model.
     */
    @Override
    public void clearBoard()
    {
        System.out.println("Board cleared");
        controller.resetGameBoard();
        turnState = WAITING_FOR_SELECTION;
        lblInfo.setText(labelText());
        wasGameStarted = false;
        createBoardButton.setEnabled(true); // enabling choosing a board
        chooseOpponentButton.setEnabled(true); // enabling changing the opponent

        updateScreen();
        repaint();
    }

    /**
     * Loads an image from resources and resizes it.
     * Used for game pieces.
     */
    private ImageIcon loadImage(String fileName, int width, int height)
    {
        // loading the image from assessts folder
        ImageIcon imgIcon = new ImageIcon(getClass().getResource(fileName));
    
        if (width != -1 || height != -1)
            imgIcon = new ImageIcon(imgIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    
        return imgIcon;
    }
 
    /**
     * Returns the corresponding icon based on the cell content (white amazon for example).
     */
    public ImageIcon getSignIcon(CellContent sign)
    {
        if(sign == WHITE_AMAZON)
            return icon_white_amazon;
        if(sign == BLACK_AMAZON)
            return icon_black_amazon;
        if(sign == WHITE_ARROW)
            return icon_white_arrow;
        if(sign == BLACK_ARROW)
            return icon_black_arrow;
        return null;
    }

    /**
     * returning the next state in a cycle
     * selection -> movement -> shooting
     */
    public MoveState nextState(MoveState state)
    {
        if(state == WAITING_FOR_SELECTION)
            return WAITING_FOR_JUMPING;
        if(state == WAITING_FOR_JUMPING)
            return WAITING_FOR_SHOOTING;
        return WAITING_FOR_SELECTION;
    }

    /**
     * Locks the UI to prevent changing the opponent or board type after the game begins.
     */
    public void gameStarted()
    {
        wasGameStarted = true;
        createBoardButton.setEnabled(false);
        chooseOpponentButton.setEnabled(false);
    }

    /**
     * Plays a move using the controller.
     * Updates the screen, and checks for game over.
     */
    public void doMove(Move moveToPlay)
    {
        if(!wasGameStarted)
            gameStarted();

        controller.playAMove(moveToPlay);
        gameMovePositions = new Position[3];
        turnState = WAITING_FOR_SELECTION;
        updateScreen();
        if(controller.isTheGameOver())
            gameEnded();
    }

    /**
     * returns whether the given board position contains a black or white Amazon.
     */
    public boolean isCellAmazon(Position pos)
    {
        if(!controller.isInsideBoard(pos))
            System.out.println("Out of board!");
    
        CellContent[][] boardCopy = controller.getGameBoardCopy();
        if(boardCopy[pos.getRow()][pos.getCol()] == WHITE_AMAZON)
            return true;
        if(boardCopy[pos.getRow()][pos.getCol()] == BLACK_AMAZON)
            return true;
        return false;
    }

    /**
     * Handles clicks based on the current turnState
     * - First click: selects Amazon.
     * - Second click: selects destination, or repeat First click
     * - Third click: selects arrow target and completes the move.
     * Triggers AI move if opponent is a computer.
     */
    public void boardButtonClicked(int row, int col)
    {
        Position pos = new Position(row, col);
        if(turnState == WAITING_FOR_SELECTION)
        {
            gameMovePositions[0] = pos;
            // add an icon of selected
            turnState = nextState(turnState);
            updateScreen();
        }
        else if(turnState == WAITING_FOR_JUMPING)
        {
            if(isCellAmazon(pos)) // player changed their mind and chose a different amazon
                gameMovePositions[0] = pos;
            else
            {            
                gameMovePositions[1] = pos;
                turnState = nextState(turnState);
            }
            updateScreen();
        }
        else
        {
            gameMovePositions[2] = pos;
            Move moveToPlay = new Move(gameMovePositions[0], gameMovePositions[1], gameMovePositions[2]);
            doMove(moveToPlay);

            // The opponent is one of the computer players
            if(opponent != HUMAN)
                AIMove();
        }
    }

    /**
     * Returns the correct status message for lblInfo, based on game state and player turn.
     */
    public String labelText()
    {
        if (controller.isTheGameOver())
            return "  Game over  -   " + controller.getWinner().name() + " won!";

        String label;
        if(controller.getCurrentPlayerSign() == WHITE)
            label = "  White's turn  -  ";
        else
            label = "  Black's turn  -  ";
        
        if(turnState == WAITING_FOR_SELECTION)
            label = label + "select an amazon";
        else if(turnState == WAITING_FOR_JUMPING)
            label = label + "select a destination";
        else
            label = label + "select a target";

        return label;
    }

    /**
     * This method updates the UI according to the current board state:
     * Placing icons on the buttons.
     * Coloring buttons accordingly
     * updating lblInfo
     */
    @Override
    public void updateScreen() // called each time after a move
    {
        CellContent[][] boardState = controller.getGameBoardCopy();
        lblInfo.setText(labelText());
        
        for (int row = 0; row < BOARD_ROWS; row++)
        {
            for (int col = 0; col < BOARD_COLS; col++)
            {
                JButton btn = boardButtonMatrix[row][col];
                btn.setIcon(getSignIcon(boardState[row][col]));
                btn.setDisabledIcon(getSignIcon(boardState[row][col])); // Prevents graying out
            }
        }
        updateClickability();
        lblInfo.setText(labelText());
        repaint();
    }

    /**
     * Returns the positions of squares that should be clickable
     * Based on the move phase (selection / jumping / shooting)
     */
    public Position[] clickableSquares(PlayerColor colorToPlaySign)
    {
        if(turnState == WAITING_FOR_SELECTION)
            return controller.getFreeAmazonPositions(colorToPlaySign);

        else if(turnState == WAITING_FOR_JUMPING)
        {
            Position[] squaresToJumpTo = controller.getReachables(gameMovePositions[0]); // To jump
            Position[] freeAmazons = controller.getFreeAmazonPositions(colorToPlaySign); // To choose a different amazon
            return controller.combineArrs(freeAmazons, squaresToJumpTo);
        }
        else if(turnState == WAITING_FOR_SHOOTING)
        {
            return controller.getShootablesAfterJump(gameMovePositions[0], gameMovePositions[1]);
        }
        System.out.println("Error");
        return null;
    }

    /**
     * updates the clickabilty to the buttons of the board squares
     * and colors the backgrounds accordingly
     */
    public void updateClickability()
    {
        for (int i = 0; i < BOARD_ROWS; i++)
        {
            for (int j = 0; j < BOARD_COLS; j++)
            {
                boardButtonMatrix[i][j].setEnabled(false);
                if((i+j) % 2 == 0)
                    boardButtonMatrix[i][j].setBackground(lightBrownBackGround);
                else
                    boardButtonMatrix[i][j].setBackground(darkBrownBackGround);
            }
        }

        Position[] clickablesPositions = clickableSquares(controller.getCurrentPlayerSign());
        for (int i = 0; i < clickablesPositions.length; i++)
        {
            Position pos = clickablesPositions[i];
            JButton btn = boardButtonMatrix[pos.getRow()][pos.getCol()];
            btn.setEnabled(true);
            if(!(isCellAmazon(pos) && turnState != WAITING_FOR_SELECTION))
                btn.setBackground(clickableBackGround);
        }
    }

    /**
     * Ask a game board for the specified type, and updates the UI
    * @param boardType the type of board to create (random, good for white, etc.)
     */
    private void preparedBoard(String boardType)
    {
        controller.createABoard(boardType);
        turnState = WAITING_FOR_SELECTION;
        gameStarted();
        updateScreen();
        if(controller.isTheGameOver()) // unlikely but possible
        {
            gameEnded();
        }
    }

    /**
     * Handles the end-of-game UI logic by enabling relevant buttons,
     * and updating the info label.
     */
    public void gameEnded()
    {
        createBoardButton.setEnabled(true);
        chooseOpponentButton.setEnabled(true);
        lblInfo.setText(labelText());
    }
}
