package com.mycompany.theeightpuzzle;

import javax.swing.JLabel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
/**
 *
 * @author Faxy
 */
public class EightController extends JLabel implements VetoableChangeListener, PropertyChangeListener{
    // Internal state tracking the current position of the hole
    private int holePosition;

    // Default constructor
    public EightController (){
        super("START");
    }
    
    // Constructor to initialize the controller with the initial hole position
    public EightController(int initialHolePosition) {
        super("START");
        this.holePosition = initialHolePosition;
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if ("moveCheck".equals(evt.getPropertyName())) {
            EightTile tile = (EightTile) evt.getSource();
            int oldLabel = (int) evt.getOldValue();

            //System.out.println("doing the veto for " + oldLabel);  //DEBUG print
            
            // If the tile is the current hole, veto the change
            if (oldLabel == 9) {
                setText("KO");
                throw new PropertyVetoException("Tile is the hole", evt);
            }

            // If the tile is not adjacent to the hole, veto the change
            if (!isAdjacentToHole(tile.getPosition())) {
                setText("KO");
                throw new PropertyVetoException("Tile is not adjacent to the hole", evt);
            }

            // If the move is valid set text to "OK"
            setText("OK");
            holePosition = tile.getPosition(); // The hole moves to the tile's position
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("restartHole".equals(evt.getPropertyName())) {
            this.holePosition = (int) evt.getNewValue();
            setText("START");
        }
    }
    
    // Method to check if a given position is adjacent to the hole
    private boolean isAdjacentToHole(int tilePosition) {
        // Assuming the board is represented as a 3x3 grid:
        // Positions: 1 2 3
        //            4 5 6
        //            7 8 9
        return switch (holePosition) {
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
    
    // Method to reset the controller's state when the game is restarted
    public void restart(int initialHolePosition) {
        this.holePosition = initialHolePosition;
        setText("START");
    }
}
