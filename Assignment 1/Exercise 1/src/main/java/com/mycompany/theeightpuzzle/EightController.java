package com.mycompany.theeightpuzzle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;

/**
 * Controller (extends JLabel) for managing and validating moves and flips in the Eight Puzzle game.
 * 
 * @author Faxy
 */
public class EightController extends javax.swing.JLabel implements VetoableChangeListener, PropertyChangeListener{
    // Internal state tracking the current position of the hole
    private int holePosition;

    /**
     * Default constructor for EightController.
     * 
     * Initializes the controller with default text "START".
     */
    public EightController (){
        super("START");
    }
    
    /**
     * Constructor with initial hole position.
     * 
     * Initializes the controller with a specific initial hole position.
     * 
     * @param initialHolePosition The initial position of the hole.
     */
    public EightController(int initialHolePosition) {
        super("START");
        this.holePosition = initialHolePosition;
    }

    /**
     * Handles vetoable property changes.
     * 
     * Validates moves and flips according to the game rules (see report), 
     * and updates the controller's text accordingly.
     * 
     * @param evt The PropertyChangeEvent containing details about the event.
     * 
     * @throws PropertyVetoException If the move or flip is not allowed based on the game rules.
     */
    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if ("moveCheck".equals(evt.getPropertyName())) {
            int oldLabel = (int) evt.getOldValue();
            int newLabel = (int) evt.getNewValue(); // always HOLE_LABEL = 9

            //System.out.println("doing the veto for " + oldLabel);  //DEBUG print
            
            // If the tile is the current hole, veto the change
            if (oldLabel == newLabel) {
                setText("KO");
                throw new PropertyVetoException("Tile is the hole", evt);
            }

            EightTile tile = (EightTile) evt.getSource();
            // If the tile is not adjacent to the hole, veto the change
            if (!isAdjacentToHole(tile.getPosition())) {
                setText("KO");
                throw new PropertyVetoException("Tile is not adjacent to the hole", evt);
            }

            // If the move is valid set text to "OK"
            setText("OK");
            this.holePosition = tile.getPosition(); // The hole moves to the tile's position
        }
        if ("flip".equals(evt.getPropertyName())) {
            if (this.holePosition != 9) {
                setText("NOT ALLOWED");
                throw new PropertyVetoException("Flip not allowed unless hole is in position 9.", evt);
            }
            setText("FLIPPED");
        }
    }
    
    /**
     * Handles property change events.
     * 
     * Responds to the holePosition update event, labelled as "restartHole".
     * 
     * @param evt The PropertyChangeEvent containing details about the change.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("restartHole".equals(evt.getPropertyName())) {
            this.holePosition = (int) evt.getNewValue();
            setText("START");
        }
    }
    
    /**
     * Checks if a given position is adjacent to the hole.
     * 
     * Determines if the specified tile position is adjacent to the current hole position
     * based on a 3x3 grid representation of the puzzle.
     *   Positions: 1 2 3
     *              4 5 6
     *              7 8 9
     * 
     * @param tilePosition The position of the tile to check.
     * 
     * @retval True if the tile is adjacent to the hole.
     * @retval False if the tile is not adjacent to the hole.
     */
    private boolean isAdjacentToHole(int tilePosition) {
        return switch (this.holePosition) {
            case 1 -> tilePosition == 2 || tilePosition == 4;
            case 2 -> tilePosition == 1 || tilePosition == 3 || tilePosition == 5;
            case 3 -> tilePosition == 2 || tilePosition == 6;
            case 4 -> tilePosition == 1 || tilePosition == 5 || tilePosition == 7;
            case 5 -> tilePosition == 2 || tilePosition == 4 || tilePosition == 6 || tilePosition == 8;
            case 6 -> tilePosition == 3 || tilePosition == 5 || tilePosition == 9;
            case 7 -> tilePosition == 4 || tilePosition == 8;
            case 8 -> tilePosition == 5 || tilePosition == 7 || tilePosition == 9;
            case 9 -> tilePosition == 6 || tilePosition == 8;
            default -> false;
        };
    }
}
