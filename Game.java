package edu.msu.beemanch.exambeemanch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    /**
     * Percentage of the display width or height that
     * is occupied by the puzzle
     */
    final static float SCALE_IN_VIEW = 0.8f;

    /**
     * Paint for the outline of the game
     */
    private Paint outlinePaint;

    /**
     * Paint for the score
     */
    private Paint scorePaint;

    /**
     * Nodes on the board
     */
    private ArrayList<ArrayList<Node>> nodes = new ArrayList<>();

    /**
     * Current Score of the game
     */
    private int score;

    /**
     * Margins of the actual game square
     */
    int marginX;
    int marginY;

    /**
     * Size of the game
     */
    int gameSize;

    /**
     * Whether or not a move was able to occur
     */
    private boolean moved = false;

    /**
     * Number of nodes currently on the board
     */
    private int numNodes;

    public Game(Context context) {
        // Paint to draw board outline
        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0x69696969);
        outlinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outlinePaint.setStrokeWidth(5);

        // Paint to draw score
        scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scorePaint.setColor(Color.BLACK);
        scorePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scorePaint.setTextSize(50f);
        scorePaint.setTextAlign(Paint.Align.CENTER);

        // Randomizer to get random row and col for 2 initial nodes
        Random rand = new Random();

        // Initialize the grid with 2 randomly placed nodes
        int row1 = rand.nextInt(4); int col1 = rand.nextInt(4);
        int row2 = rand.nextInt(4); int col2 = rand.nextInt(4);

        // Ensure the nodes aren't assigned to same index
        while (row1==row2 && col1==col2) {
            row2 = rand.nextInt(4); col2 = rand.nextInt(4);
        }

        // Initialize the board with random nodes
        for (int row = 0; row < 4; row++) {
            ArrayList<Node> newNodes = new ArrayList<>();
            for (int col=0; col < 4; col++) {
                if (row==row1 && col==col1) {
                    newNodes.add(new Node(2, row1, col1));
                } else if (row==row2 && col==col2) {
                    newNodes.add(new Node(2, row2, col2));
                } else {
                    newNodes.add(null);
                }
            }
            nodes.add(newNodes);
        }

        numNodes = 2;
    }

    /**
     * Draw the game
     * @param canvas
     */
    public void draw(Canvas canvas) {
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the min of the two dimensions
        int minDim  = wid < hit ? wid : hit;

        gameSize = (int)(minDim * SCALE_IN_VIEW);

        marginX = (wid - gameSize) / 2;
        marginY = (hit - gameSize) / 2;

        float scaleFactor = 1;

        // Draw the score
        float fontSize = hit*50/wid;
        scorePaint.setTextSize(fontSize);
        canvas.drawText("Score: " + this.score, marginX + gameSize/2, marginY/2, scorePaint);

        //
        // Draw the outline of the puzzle
        //
        canvas.drawRect(marginX, marginY, marginX+gameSize, marginY+gameSize, outlinePaint);

        canvas.save();
        canvas.translate(marginX, marginY);
        canvas.scale(scaleFactor, scaleFactor);
        canvas.restore();

        // Draw the nodes on board
        for (int row=0; row<4; row++) {
            for (int col=0; col<4; col++) {
                if (nodes.get(row).get(col) != null) {
                    nodes.get(row).get(col).draw(canvas, marginX, marginY, gameSize, scaleFactor, (wid-2*marginX) / 4, (hit-2*marginY)/4);
                }
            }
        }

    }

    /**
     * Makes sure all nodes are able to be added on again
     */
    public void nextTurn() {
        for (int row=0; row<4; row++) {
            for (int col=0; col<4; col++) {
                if (nodes.get(row).get(col) != null) {
                    nodes.get(row).get(col).setAdded(false);
                }
            }
        }
        moved = false;
    }

    /**
     * Adds a new node to the grid
     */
    public void addNewNode() {
        Random rand = new Random();
        int row = rand.nextInt(4); int col = rand.nextInt(4);
        while (nodes.get(row).get(col) != null) {
            row = rand.nextInt(4); col = rand.nextInt(4);
        }

        Node node = new Node(2, row, col);
        moveNode(row, col, node);
        numNodes++;
    }

    /**
     * Move a node in the grid
     * @param row Row to move to
     * @param col Col to move to
     * @param node Node being moved
     */
    private void moveNode(int row, int col, Node node) {
        if (node.getRow() != row || node.getCol() != col) {
            moved = true;
        }
        node.setLocation(row, col);
        nodes.get(row).set(col, node);
    }

    /**
     * Add 2 nodes together
     * @param target The node being added to
     * @param moved The node that is to be added then disappear
     * @return True if an add was able to occur
     */
    private boolean addNodes(Node target, Node moved) {
        if (isValid(target, moved)){

            target.setValue(target.getValue() + moved.getValue());
            target.setAdded(true);
            score += target.getValue();
            this.moved = true;
            numNodes--;
            return true;

        } return false;
    }

    /**
     * See if a move is valid
     * @param target Node being added to
     * @param moved Node being moved
     * @return True if the move is valid
     */
    private boolean isValid(Node target, Node moved) {
        return !target.getAdded() && !moved.getAdded() && target.getValue() == moved.getValue();
    }

    /**
     * Perform a move
     * @param move Type of move
     * @return True if a move can occur
     */
    public boolean move(int move) {
        switch(move) {
            case 0:
                return moveLeft();
            case 1:
                return moveRight();
            case 2:
                return moveUp();
            case 3:
                return moveDown();
        }
        return false;
    }

    /**
     * Move the nodes to the right
     */
    private boolean moveRight() {
        for (int row=0; row<4; row++) {
            Node previous = nodes.get(row).get(3);
            // C is column index where to move a node
            int c = 2;
            // Previous is the further-most node
            if (previous == null) { c = 3; }

            // Loop starts at 2 because the further-most node cant be moved so dont need to check
            for (int col=2; col>=0; col--) {
                // Node is the current node being visited
                Node node = nodes.get(row).get(col);
                // The further most node and the current node
                if (previous != null && node != null) {
                    if (!previous.getAdded()) {
                        if (addNodes(previous, node)) {
                            // If they can be added, replace the visited node with null
                            nodes.get(row).set(col, null);
                            // Next column to move a node to is the one before next further most location
                            c = previous.getCol() - 1;
                        } else {
                            nodes.get(row).set(col, null);
                            // 2 nodes couldnt be added
                            if (nodes.get(row).get(c) != null) {
                                // Move the current node to column next to previous
                                // New column to move to is now where node was moved
                                moveNode(row, c-1, node);
                                c--;
                            } else {
                                moveNode(row, c, node);
                            }
                        } 
                        // Since move has occured, update further most node
                        previous = nodes.get(row).get(c);
                    }
                } // Move current node to be the further most node 
                else if (node != null && previous == null) {
                    moveNode(row, c, node);
                    nodes.get(row).set(col, null);
                    previous = node;
                }
            }
        }
        return moved;
    }

    /**
     * Move the nodes to the left
     */
    private boolean moveLeft() {
        for (int row=0; row<4; row++) {
            // Further-most node to the left
            Node previous = nodes.get(row).get(0);
            // Next available column index to place a node
            int c = 1;
            if (previous == null) { c = 0; }
            // Start at 1 because node at index 0 wont be moved
            for (int col = 1; col <= 3; col++) {
                Node node = nodes.get(row).get(col);
                if (previous != null && node != null) {
                    if (!previous.getAdded()) {
                        if (addNodes(previous, node)) {
                            // If possible, adds 2 nodes together
                            // Update c to next available index
                            nodes.get(row).set(col, null);
                            c = previous.getCol() + 1;

                        } else {
                            nodes.get(row).set(col, null);
                            if (nodes.get(row).get(c) != null) {
                                moveNode(row, c+1, node);
                                c++;
                            } else {
                                moveNode(row, c, node);
                            }

                        } previous = nodes.get(row).get(c);
                    }
                } else if (node != null && previous == null) {
                    moveNode(row, c, node);
                    nodes.get(row).set(col, null);
                    previous = node;
                }
            }
        }
        return moved;
    }

    /**
     * Move the nodes up
     */
    private boolean moveUp() {
        for (int col=0; col<4; col++) {
            // Further-most node to the left
            Node previous = nodes.get(0).get(col);
            // Next available column index to place a node
            int r = 1;
            if (previous == null) { r = 0; }
            // Start at 1 because node at index 0 wont be moved
            for (int row = 1; row <= 3; row++) {
                Node node = nodes.get(row).get(col);
                if (previous != null && node != null) {
                    if (!previous.getAdded()) {
                        if (addNodes(previous, node)) {
                            // If possible, adds 2 nodes together
                            // Update r to next available index
                            nodes.get(row).set(col, null);
                            r = previous.getRow() + 1;
                        } else {
                            nodes.get(row).set(col, null);
                            if (nodes.get(r).get(col) != null) {
                                moveNode(r+1, col, node);
                                r++;
                            } else {
                                moveNode(r, col, node);
                            }
                        }
                        previous = nodes.get(r).get(col);
                    }
                } else if (node != null && previous == null) {
                    moveNode(r, col, node);
                    nodes.get(row).set(col, null);
                    previous = node;
                }
            }
        }
        return moved;
    }

    /**
     * Move the nodes down
     */
    private boolean moveDown() {
        for (int col=0; col<4; col++) {
            // Further-most node to the left
            Node previous = nodes.get(3).get(col);
            // Next available column index to place a node
            int r = 2;
            if (previous == null) { r = 3; }
            // Start at 1 because node at index 0 wont be moved
            for (int row = 2; row >= 0; row--) {
                Node node = nodes.get(row).get(col);
                if (previous != null && node != null) {
                    if (!previous.getAdded()) {
                        if (addNodes(previous, node)) {
                            // If possible, adds 2 nodes together
                            // Update r to next available index
                            nodes.get(row).set(col, null);
                            r = previous.getRow() - 1;
                        } else {
                            nodes.get(row).set(col, null);
                            if (nodes.get(r).get(col) != null) {
                                moveNode(r - 1, col, node);
                                r--;
                            } else {
                                moveNode(r, col, node);
                            }
                        }
                        previous = nodes.get(r).get(col);
                    }
                } else if (node != null && previous == null) {
                    moveNode(r, col, node);
                    nodes.get(row).set(col, null);
                    previous = node;
                }
            }
        }
        return moved;
    }

    /**
     * Reset the turn
     */
    public void setMoved() {
        moved = false;
    }

    /**
     * Checks if the game is over
     * @return True if there are no more valid moves
     */
    public boolean checkGameOver() {
        if (numNodes == 16) {
            // Check first row
            int r = 0;
            for (int col=0; col<4; col++){
                if (col != 3) {
                    // Check over right and down one
                    if (isValid(nodes.get(r).get(col), nodes.get(r).get(col+1)) ||
                        isValid(nodes.get(r+1).get(col), nodes.get(r).get(col))) {
                        return false;
                    }
                } else {
                    // End of column, just check down 1
                    if (isValid(nodes.get(r+1).get(col), nodes.get(r).get(col))) {
                        return false;
                    }
                }
            }
            // Check 3rd row
            r = 3;
            for (int col=0; col < 4; col++) {
                if (col != 3) {
                    if (isValid(nodes.get(r).get(col), nodes.get(r).get(col+1)) ||
                        isValid(nodes.get(r-1).get(col), nodes.get(r).get(col))) {
                        return false;
                    }
                } else {
                    if (isValid(nodes.get(r-1).get(col), nodes.get(r).get(col))) {
                        return false;
                    }
                }
            }
            // Check middle rows
            for (int row=1; row<3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (isValid(nodes.get(row).get(col), nodes.get(r).get(col+1))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
