package controller;

/**
 * Represents a complete move in the Game of Amazons
 * Each move contains source position, destination position, and arrow target position.
 * Also stores a score for AI evaluation purposes.
 * @author OFIR AVIANI | 18.03.2025
 */

public class Move
{
    private int jumpsFromRow, jumpsFromCol;
    private int jumpsToRow, jumpsToCol;
    private int shootsAtRow, shootsAtCol;
    private double score;

    /**
     * constructs a Move object
     * @param jumpsFromRow row of source poisiton
     * @param jumpsFromCol column of source poisiton
     * @param jumpsToRow row of destination position
     * @param jumpsToCol column of destination position
     * @param shootsAtRow row of arrow target position
     * @param shootsAtCol coulmn of arrow target position
     */
    public Move(int jumpsFromRow, int jumpsFromCol, int jumpsToRow, int jumpsToCol, int shootsAtRow, int shootsAtCol)
    {
        this.jumpsFromRow = jumpsFromRow;
        this.jumpsFromCol = jumpsFromCol;
        this.jumpsToRow = jumpsToRow;
        this.jumpsToCol = jumpsToCol;
        this.shootsAtRow = shootsAtRow;
        this.shootsAtCol = shootsAtCol;
        
        // a move that wasn't checked shouldn't be chosen
        this.score = - 4001;
    }

    /**
     * constructs a Move object
     * @param jumpsFrom source poisiton
     * @param jumpsTo destination position
     * @param shootsAt target position
     */
    public Move(Position jumpsFrom, Position jumpsTo, Position shootsAt)
    {
        this.jumpsFromRow = jumpsFrom.getRow();
        this.jumpsFromCol = jumpsFrom.getCol();
        this.jumpsToRow = jumpsTo.getRow();
        this.jumpsToCol = jumpsTo.getCol();
        this.shootsAtRow = shootsAt.getRow();
        this.shootsAtCol = shootsAt.getCol();
        
        // a move that wasn't checked shouldn't be chosen
        this.score = - 4001; 
    }


    public void setScore(double score){
        this.score = score;
    }

    public double getScore(){
        return this.score;
    }

    
    public int getJumpsFromRow() {
        return jumpsFromRow;
    }

    public int getJumpsFromCol() {
        return jumpsFromCol;
    }

    public int getJumpsToRow() {
        return jumpsToRow;
    }

    public int getJumpsToCol() {
        return jumpsToCol;
    }

    public int getShootsAtRow() {
        return shootsAtRow;
    }

    public int getShootsAtCol() {
        return shootsAtCol;
    }

    
    /**
     * Creates a readable string representation of the move
     */
    @Override
    public String toString()
    {
        return "Move [Amazon jumps from (" + jumpsFromRow+","+jumpsFromCol + "), " +
        "Jumps to (" + jumpsToRow+","+jumpsToCol+ "), " +
        "Shoots to (" + shootsAtRow+","+shootsAtCol + "), " +
        "Score: " + score + "]";
    }
}
