package controller;

/**
 * Represents a position on the game board with row and column coordinates.
 * Used for tracking amazon positions, move destinations, and arrow targets.
 * @author OFIR AVIANI | 27.03.2025
 */

public class Position 
{
    private int row; // row number
    private int column; // column number

    /**
     * constructs a Position object
     * @param row the row of the Position object
     * @param column the column of the Position object
     */
    public Position(int row, int column)
    {
        this.row = row;
        this.column = column;
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return column;
    }

    public boolean equals(Position otherPos)
    {
        if(otherPos == null)
            return false;
        return otherPos.getRow() == row && otherPos.getCol() == column;
    }

    /**
     *  Creates a readable string representation of the position
     */
    @Override
    public String toString()
    {
        return "[row - " + row + ", col - " + column + "]";
    }
}
