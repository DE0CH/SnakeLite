import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.scene.control.Cell;

import java.lang.Math;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * SpampedeData - Representation of the Board. Outside of the model, no one knows
 * how the Board is represented. They can only access the methods provided by the
 * class.
 *
 * @author CS60 instructors
 * @author modified by stephenchen
 */

enum CellType {
    // The first thing in an enum has to be a list of the objects.
    // NAME_OTHERS_WILL_REFERENCE(args to the constructor if applicable)
    WALL("*"), OPEN(" "), SPAM("X"), HEAD("H"), BODY("B");

    // CellTypes store a single instance variable:
    private final String displayChar;

    // Private constructor for the enumeration
    private CellType(String inputChar) {
        this.displayChar = inputChar;
    }

    // return a String representing the CellType
    public String getDisplayChar() {
        return this.displayChar;
    }

}

public class SpampedeData {
    /**
     * The collection of all the BoardCells in the program.
     * <p>
     * All BoardCells needed by the program are created by the
     * SpampedeData constructor, so you don't need to create any
     * new BoardCells in your code; you'll just pass around (references to)
     * existing cells, and change the contents of some of these cells.
     */

    public enum SnakeMode {
        // The first thing in an enum has to be a list of the objects.
        GOING_NORTH, GOING_SOUTH, GOING_EAST, GOING_WEST, AI_MODE
    }

    private BoardCell[][] boardCells2D;



    /**
     * The current movement "mode" of the snake, i.e., whether it's headed
     * in a particular direction or in AI mode.
     */
    private SnakeMode currentMode = SnakeMode.GOING_EAST;

    /**
     * A list of (references to) the cells that contain the snake.
     * The head is the last element of the list.
     */
    private LinkedList<BoardCell> snakeCells = new LinkedList<>();

    /**
     * Whether the game is done.
     */
    private boolean gameOver = false;

    /* -------------------------------------- */
    /* Constructor and initialization methods */
    /* -------------------------------------- */

    /**
     * Constructor; creates a "Board" with walls on the boundary
     * and open in the interior.
     */

    public SpampedeData() {
        int height = Preferences.NUM_CELLS_TALL + 2;
        int width = Preferences.NUM_CELLS_WIDE + 2;
        this.boardCells2D = new BoardCell[height][width];

        // Place walls around the outside
        this.addWalls();

        // Fill the remaining cells not already filled!
        this.fillRemainingCells();
        this.placeSnakeAtStartLocation();
    }

    /**
     * Adds WALL Boardcells around the edges of this.boardCells2DD.
     */
    private void addWalls() {
        int height = this.getNumRows();
        int width = this.getNumColumns();

        // Add Left and Right Walls
        for (int row = 0; row < height; row++) {
            this.boardCells2D[row][0] = new BoardCell(row, 0, CellType.WALL);
            this.boardCells2D[row][width - 1] = new BoardCell(row, width - 1,
                    CellType.WALL);
        }
        // Add top and bottom walls
        for (int column = 0; column < width; column++) {
            this.boardCells2D[0][column] = new BoardCell(0, column, CellType.WALL);
            this.boardCells2D[height - 1][column] = new BoardCell(height - 1,
                    column, CellType.WALL);
        }
    }

    /**
     * Finishes filling this.boardCells2D with OPEN BoardCells
     * (and set this.freeSpots to the number of OPEN cells).
     */
    private void fillRemainingCells() {
        int height = this.getNumRows();
        int width = this.getNumColumns();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (this.boardCells2D[row][column] == null) {
                    this.boardCells2D[row][column] =
                            new BoardCell(row, column, CellType.OPEN);
                }
            }
        }
    }

    /**
     * Puts the snake in the upper-left corner of the walls, facing east.
     */
    public void placeSnakeAtStartLocation() {
        BoardCell body = this.getCell(1, 1);
        BoardCell head = this.getCell(1, 2);
        this.snakeCells.addLast(body);
        this.snakeCells.addLast(head);
        head.becomeHead();
        body.becomeBody();
    }

    /* -------------------------------------------- */
    /* Methods to access information about the Board */
    /* -------------------------------------------- */

    /**
     * @return Are we in AI mode?
     */
    public boolean inAImode() {
        return this.currentMode == SnakeMode.AI_MODE;
    }

    /**
     * @return the height of the Board (including walls) in cells.
     */
    public int getNumRows() {
        return this.boardCells2D.length;
    }

    /**
     * @return The width of the Board (including walls) in cells.
     */
    public int getNumColumns() {
        return this.boardCells2D[0].length;
    }

    /**
     /* Access a cell at a particular location.
     * <p>
     * This should really be private. We make it public to allow
     * our unit tests to use it, but it shouldn't be called
     * from the SpampedeBrain or the SpampedeDisplay.
     *
     * @param r  between 0 and this.getNumRows()-1 inclusive
     * @param c  between 0 and this.getNumColumns()-1 inclusive
     * @return cell c in row r.
     */
    public BoardCell getCell(int r, int c) {
        if (r >= this.getNumRows() || c >= this.getNumColumns() || r < 0
                || c < 0) {
            System.err.println("Trying to access cell outside of the Board:");
            System.err.println("row: " + r + " col: " + c);
            System.exit(0);
        }
        return this.boardCells2D[r][c];
    }

    /* -------------------- */
    /* Spam-related Methods */
    /* -------------------- */

    public void spawnSpam() {
        boolean success;
        do {
            Random random = new Random();
            int x = random.nextInt(this.getNumColumns() - 2);
            int y = random.nextInt(this.getNumRows() - 2);
            success = boardCells2D[y + 1][x + 1].becomeSpam();
        } while (!success);
    }


    /* --------------------- */
    /* Snake movement methods */
    /* --------------------- */

    /**
     *
     * @return true if eat any spam.
     */
    public boolean move() {
        BoardCell nextCell;
        if (this.inAImode()) {
            nextCell = this.getNextCellFromBFS();
            if (nextCell == null) {
                return false;
            }
        } else {
            nextCell = this.getNextCellInDir();
        }
        if (nextCell.isWall() || nextCell.isBody()) {
            setGameOver();
        } else if (nextCell.isSpam()) {
            this.moveHead(nextCell);
            Access.game.remove(nextCell.oval);
            spawnSpam();
            return true;
        } else {
            this.moveHead(nextCell);
            this.removeTail();
        }
        return false;
    }

    public void moveHead(BoardCell nextCell) {
        this.snakeCells.getLast().becomeBody();
        nextCell.becomeHead();
        this.snakeCells.addLast(nextCell);
    }

    public void removeTail() {
        this.snakeCells.getFirst().becomeOpen();
        Access.game.remove(this.snakeCells.getFirst().rec);
        this.snakeCells.removeFirst();
    }


    /* -------------------------------------- */
    /* Methods to support movement without AI */
    /* -------------------------------------- */

    /**
     * @return the cell to the north of the given cell,
     *         which must not be on the boundary.
     */
    public BoardCell getNorthNeighbor(BoardCell cell) {
        try {
            return boardCells2D[cell.getRow() - 1][cell.getColumn()];
        } catch (IndexOutOfBoundsException e){return null;}
    }

    /**
     * @return the cell to the south of the given cell,
     *         which must not be on the boundary.
     */
    public BoardCell getSouthNeighbor(BoardCell cell) {
        try {
            return boardCells2D[cell.getRow()+1][cell.getColumn()];
        } catch (IndexOutOfBoundsException e){return null;}
    }

    /**
     * @return the cell to the east of the given cell,
     *         which must not be on the boundary.
     */
    public BoardCell getEastNeighbor(BoardCell cell) {
        try {
            return boardCells2D[cell.getRow()][cell.getColumn()+1];
        } catch (IndexOutOfBoundsException e){return null;}
    }

    /**
     * @return the cell to the west of the given cell,
     *         which must not be on the boundary.
     */
    public BoardCell getWestNeighbor(BoardCell cell) {
        try {
            return boardCells2D[cell.getRow()][cell.getColumn()-1];
        } catch (IndexOutOfBoundsException e){return null;}
    }

    /**
     * @return the cell to the north of the snake's head
     */
    public BoardCell getNorthNeighbor() {
        return this.getNorthNeighbor(this.getSnakeHead());
    }

    /**
     * @return the cell to the south of the snake's head
     */
    public BoardCell getSouthNeighbor() {
        return this.getSouthNeighbor(this.getSnakeHead());
    }

    /**
     * @return the cell to the east of the snake's head
     */
    public BoardCell getEastNeighbor() {
        return this.getEastNeighbor(this.getSnakeHead());
    }

    /**
     * @return the cell to the west of the snake's head
     */
    public BoardCell getWestNeighbor() {
        return this.getWestNeighbor(this.getSnakeHead());
    }

    /**
     * @return a cell North, South, East or West of the snake head
     *         based upon the current direction of travel (this.currentMode).
     *         (We won't call this function if we're in AI_MODE, though
     *         Java wants this function to return a value even then.)
     */
    public BoardCell getNextCellInDir() {
        switch (this.currentMode) {
            case GOING_EAST:
                return getEastNeighbor();
            case GOING_NORTH:
                return getNorthNeighbor();
            case GOING_SOUTH:
                return getSouthNeighbor();
            case GOING_WEST:
                return getWestNeighbor();
            case AI_MODE:
                return boardCells2D[0][0];

        }
        return boardCells2D[0][0];
    }

    /* -------------------------------------------------- */
    /* Public methods to get all or one (random) neighbor */
    /* -------------------------------------------------- */

    /**
     * @return an array of the four neighbors of the given cell
     *         (suitable for iterating through with a for-each loop).
     */
    public BoardCell[] getNeighbors(BoardCell center) {
        BoardCell[] neighborsArray = { getNorthNeighbor(center),
                getSouthNeighbor(center),
                getEastNeighbor(center),
                getWestNeighbor(center) };
        return neighborsArray;
    }

    /**
     * @return an open neighbor of the given cell
     *         (or some other neighbor if there are no open neighbors)
     */
    public BoardCell getRandomNeighboringCell(BoardCell start) {
        BoardCell[] neighborsArray = getNeighbors(start);
        for (BoardCell mc : neighborsArray) {
            if (mc.isOpen() || mc.isSpam()) {
                return mc;
            }
        }
        // If we didn't find an open space, just return the first neighbor.
        return neighborsArray[0];
    }

    /* ----------------------------------------- */
    /* Methods to set the snake's (movement) mode */
    /* ----------------------------------------- */

    /**
     * Makes the snake head north.
     */
    public void setDirectionNorth() {
        this.currentMode = SnakeMode.GOING_NORTH;
    }

    /**
     * Makes the snake head south.
     */
    public void setDirectionSouth() {
        this.currentMode = SnakeMode.GOING_SOUTH;
    }

    /**
     * Makes the snake head east.
     */
    public void setDirectionEast() {
        this.currentMode = SnakeMode.GOING_EAST;
    }

    /**
     * Makes the snake head west.
     */
    public void setDirectionWest() {
        this.currentMode = SnakeMode.GOING_WEST;
    }

    /**
     * Makes the snake switch to AI mode.
     */
    public void setMode_AI(Boolean tf) {
        if (tf) {
            this.currentMode = SnakeMode.AI_MODE;
        } else {
            this.currentMode = calcHeading();
        }
    }

    /**
     * Picks an initial movement mode for the snake.
     */
    public void setStartDirection() {
        this.setDirectionEast();
    }

    /* ------------------- */
    /* snake Access Methods */
    /* ------------------- */


    public LinkedList<BoardCell> getSnakeCells(){
        return this.snakeCells;
    }
    /**
     * @return the current snake heading
     */
    public SnakeMode getSnakeMode() {
        return currentMode;
    }

    /**
     * @return the cell containing the snake's head
     */
    public BoardCell getSnakeHead() {
        return this.snakeCells.peekLast();
    }

    /**
     * @return the cell containing the snake's tail
     */
    public BoardCell getSnakeTail() {
        return this.snakeCells.peekFirst();
    }

    /**
     * @return the snake body cell adjacent to the head
     */
    public BoardCell getSnakeNeck() {
        int lastSnakeCellIndex = this.snakeCells.size() - 1;
        return this.snakeCells.get(lastSnakeCellIndex - 1);
    }

    /* ------------------------------ */
    /* Helper method used by the view */
    /* ------------------------------ */

    /* ---------------------------- */
    /* Helper method(s) for reverse */
    /* ---------------------------- */


    public void reverseSnake() {
        // Step 1: unlabel the head
        this.snakeCells.getLast().becomeBody();
        // Step 2: reverse the body parts
        LinkedList<BoardCell> temp_reverseCells = new LinkedList<BoardCell>();
        for (int i=this.snakeCells.size()-1; i>=0; i--) {
            temp_reverseCells.add(this.snakeCells.get(i));
        }
        this.snakeCells = temp_reverseCells;
        // Step 3: relabel the head
        this.snakeCells.getLast().becomeHead();
        // Step 4: calculate the new direction after reversing!
        this.currentMode = calcHeading();
    }

    public SnakeMode calcHeading() {
        switch (this.snakeCells.getLast().getRow()-this.snakeCells.get(this.snakeCells.size()-2).getRow()) {
            case 1:
                return SnakeMode.GOING_SOUTH;
            case -1:
                return SnakeMode.GOING_NORTH;
        }
        switch (this.snakeCells.getLast().getColumn()-this.snakeCells.get(this.snakeCells.size()-2).getColumn()) {
            case 1:
                return SnakeMode.GOING_EAST;
            case -1:
                return SnakeMode.GOING_WEST;
        }
    return null;
    }





    /* ------------------------------------- */
    /* Methods to reset the model for search */
    /* ------------------------------------- */

    /**
     * Clears the search-related fields in all the cells,
     * in preparation for a new breadth-first search.
     */
    public void resetCellsForNextSearch() {
        for (BoardCell[] row : this.boardCells2D) {
            for (BoardCell cell : row) {
                cell.clear_RestartSearch();
            }
        }
    }

    /* ---------------- */
    /* Game-Over Status */
    /* ---------------- */

    /**
     * Sets the game-over flag.
     */
    public void setGameOver() {
        this.gameOver = true;
    }

    /**
     * @return Should we display the game-over message?
     */
    public boolean getGameOver() {
        return this.gameOver;
    }


    /**
     * Uses BFS to search for the spam closest to the snake head.
     *
     * @return Where to move the snake head, if we want to head
     *         *one step* along the shortest path to (the nearest)
     *         spam cell.
     */
    public BoardCell getNextCellFromBFS() {
        // Initialize the search.
        resetCellsForNextSearch();

        // Initialize the cellsToSearch queue with the snake head;
        // as with any cell, we mark the head cells as having been added
        // to the queue
        Queue<BoardCell> cellsToSearch = new LinkedList<BoardCell>();
        BoardCell snakeHead = getSnakeHead();
        snakeHead.setAddedToSearchList();
        cellsToSearch.add(snakeHead);

        // Variable to hold the closest spam cell, once we've found it.
        BoardCell closestSpamCell = null;

        // Search!
        outerLoop:
        while (true) {
            BoardCell currentSearch = cellsToSearch.poll();
            if (currentSearch == null) {
                // If the search fails, just move somewhere.
                return this.getRandomNeighboringCell(snakeHead);
            }
            BoardCell[] orderedCells = {this.getNorthNeighbor(currentSearch), this.getSouthNeighbor(currentSearch),
                    this.getEastNeighbor(currentSearch), this.getWestNeighbor(currentSearch)};
            for (BoardCell cell : orderedCells) {
                if (cell == null) {
                    continue;
                }
                if (cell.inSearchListAlready()) {
                    continue;
                }
                if (cell.isSpam()) {
                    cell.setParent(currentSearch);
                    closestSpamCell = cell;
                    break outerLoop;
                } else if (cell.isOpen()) {
                    cellsToSearch.add(cell);
                    cell.setParent(currentSearch);
                    cell.setAddedToSearchList();
                }
            }
        }
        return getFirstCellInPath(closestSpamCell);

        // Note: we encourage you to write the helper method
        // getFirstCellInPath below to do the backtracking to calculate the next cell!

    }

    /**
     * Follows parent pointers back from the closest spam cell
     * to decide where the head should move. Specifically,
     * follows the parent pointers back from the spam until we find
     * the cell whose parent is the snake head (and which must therefore
     * be adjacent to the previous snake head location).
     * <p>
     * Recursive or looping solutions are possible.
     *
     * @param start   where to start following spam pointers; this will
     *                 be (at least initially, if you use recursion) the
     *                 location of the spam closest to the head.
     * @return the new cell for the snake head.
     */
    private BoardCell getFirstCellInPath(BoardCell start) {
        BoardCell currentCell = start;
        while (true) {
            BoardCell parent = currentCell.getParent();
            if (parent.isHead()) {
                return currentCell;
            } else {
                currentCell = parent;
            }
        }
    }
}


class BoardCell {
    // Basic contents of a BoardCell

    /** the graphic rectangle on the screen **/
    public GRect rec;
    public GOval oval;

    /** the row of this cell within the Board ( >= 0 ) */
    private int row;

    /** the column of this cell within the Board ( >= 0 ) */
    private int column;

    /** the current contents of this cell */
    private CellType myCellType;

    // Additional instance variables to be used during search

    /** Has this cell been added to the search queue yet? */
    private boolean addedToSearchList = false;

    /** Where did we came from, when search first reached this BoardCell? */
    private BoardCell parent  = null;


    /**
     * Constructor.
     * @param inputRow     the row of this cell
     * @param inputColumn  the column of this cell
     * @param type         the initial contents of this cell
     */
    public BoardCell (int inputRow, int inputColumn, CellType type) {
        this.row = inputRow;
        this.column = inputColumn;
        this.myCellType = type;
        rec = new GRect((this.getColumn() -1) * Preferences.SNAKEBODY_SIZE,
                (this.getRow() - 1) * Preferences.SNAKEBODY_SIZE,
                Preferences.SNAKEBODY_SIZE,
                Preferences.SNAKEBODY_SIZE
        );
        ((GRect)rec).setFilled(true);
        rec.setColor(Preferences.SNAKEBODY_COLOR);
        oval = new GOval((this.getColumn() -1) * Preferences.SNAKEBODY_SIZE,
                (this.getRow() - 1) * Preferences.SNAKEBODY_SIZE,
                Preferences.BALL_SIZE,
                Preferences.BALL_SIZE
        );
        ((GOval)oval).setFilled(true);
        oval.setColor(Preferences.BALL_COLOR);

    }



    /* ------------------------------------- */
    /* Access basic information about a cell */
    /* ------------------------------------- */

    /** @return the row of this BoardCell */
    public int getRow() {
        return this.row;
    }

    /** @return the column of this BoardCell */
    public int getColumn() {
        return this.column;
    }

    /** @return Is this cell a wall? */
    public boolean isWall() {
        return this.myCellType == CellType.WALL;
    }

    /** @return Is this cell open (not a wall or a snake body part)? */
    public boolean isOpen() {
        return this.myCellType == CellType.OPEN || this.isSpam();
    }

    /** @return Does this cell contain spam? */
    public boolean isSpam() {
        return this.myCellType == CellType.SPAM;
    }

    /** @return Does this cell contain part of the snake (not the head)? */
    public boolean isBody() {
        return this.myCellType == CellType.BODY;
    }

    /** @return Does this cell contain the head of the snake? */
    public boolean isHead() {
        return this.myCellType == CellType.HEAD;
    }


    /* ------------------------------ */
    /* Modify basic info about a cell */
    /* ------------------------------ */

    /** Marks this BoardCell as spam. */
    public boolean becomeSpam() {
        if ((this.myCellType == CellType.BODY) || (this.myCellType == CellType.HEAD) || (this.myCellType == CellType.SPAM)) {
            return false;
        }
        this.myCellType = CellType.SPAM;
        Access.game.add(oval);
        return true;
    }

    /** Marks this BoardCell as open */
    public void becomeOpen() {
        this.myCellType = CellType.OPEN;
    }

    /** Marks this BoardCell as the snake's head */
    public void becomeHead() {
        this.myCellType = CellType.HEAD;
        Access.game.add(rec);
    }
    /** Marks this BoardCell as part of the snake's body */
    public void becomeBody() {
        this.myCellType = CellType.BODY;
        Access.game.add(rec);
    }

    /* ------------------------------------------ */
    /* Methods used to access and set search info */
    /* ------------------------------------------ */

    /** Marks this cell as having been added to our BFS search queue */
    public void setAddedToSearchList() {
        this.addedToSearchList = true;
    }

    /** @return Has this cell been added to our BFS search queue yet? */
    public boolean inSearchListAlready() {
        return this.addedToSearchList;
    }

    /** Clear the search-related info for this cell (to allow a new search) */
    public void clear_RestartSearch() {
        this.addedToSearchList = false;
        this.parent = null;
    }

    /** Set the parent of this cell */
    public void setParent(BoardCell p) {
        this.parent = p;
    }

    /** @return the parent of this cell */
    public BoardCell getParent() {
        return this.parent;
    }

    /* ---------------------------- */
    /* Helper functions for testing */
    /* ---------------------------- */

    /** @return the cell as a string. */
    public String toString() {
        return "[" + this.row + ", " + this.column + ", " + this.toStringType() + "]";
    }

    /** @return the contents of the cell, as a single character. */
    public String toStringType() {
        return this.myCellType.getDisplayChar();
    }

    /** @return  the parent of a cell, as a string */
    public String toStringParent(){
        if (this.parent == null){
            return "[null]";
        }
        else {
            return "[" + this.parent.row + ", " + this.parent.column + "]";
        }
    }


}

