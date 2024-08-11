package com.mycompany.theeightpuzzle;

import javax.swing.Timer;

import java.awt.Color;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.beans.VetoableChangeListener;

import java.util.List;

/**
 * Implementation (extending JButton) of a tile in the Eight Puzzle game.
 * 
 * @author Faxy
 */
public class EightTile extends javax.swing.JButton implements PropertyChangeListener{
    // Private properties for Position and Label
    private final int position; // Constant value set at startup
    private int label;          // Current label of the tile
    
    private static int HOLE_LABEL = 9; // Label of empty hole tile.

    // Support for bound and constrained properties
    private final PropertyChangeSupport pcs;
    private final VetoableChangeSupport vcs;
    
    /**
     * Default constructor for EightTile.
     * 
     * Initializes the tile with default values for properties of the class.
     */
    public EightTile() {
        super();
        this.position = -1;
        this.label = -1;
        
        // Initialize property and vetoable change support
        this.pcs = new PropertyChangeSupport(this);
        this.vcs = new VetoableChangeSupport(this);
        
        // Add a click listener
        this.addActionListener(e -> handleClick());
    }
    
    /** 
     * Constructor with position and label.
     * 
     * Initializes the tile with a specific position and label.
     * 
     * @param position The position of the tile in the puzzle.
     * @param label The label value displayed on the tile.
     * 
     * @throws IllegalArgumentException If the position or label are not valid values.
     */
    public EightTile(int position, int label) {
        if(!isValid(position) || !isValid(label)) // Parameters validity check
            throw new IllegalArgumentException();
        
        this.position = position;
        this.label = label;

        // Initialize property and vetoable change support
        this.pcs = new PropertyChangeSupport(this);
        this.vcs = new VetoableChangeSupport(this);
        
        // Add a click listener
        this.addActionListener(e -> handleClick());

        // Inizialize the tile apparence
        updateAppearance();
    }
    
    /**
     * Getter for position.
     */
    public int getPosition() {
        return position;
    }

    /** 
     * Getter for label.
     */
    @Override
    public String getLabel() {
        return String.valueOf(label);
    }

    /** 
     * Setter for label.
     */
    public void setLabel(int newLabel){
        this.label = newLabel;

        // Update the visual appearance of the tile
        updateAppearance();
    }
    
    /** 
     * Add a property change listener.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if(this.pcs != null){
            this.pcs.addPropertyChangeListener(listener);
        }
    }

    /** 
     * Add vetoable change listener.
     */
    @Override
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        if(this.pcs != null){
            this.vcs.addVetoableChangeListener(listener);
        }
    }
    
    /**
     * Updates the appearance of the tile.
     * 
     * Sets the text and background color of the tile based on its label and position:
     * the background color of a tile must be grey if the Label is 9,
     * green if position == label (and they are different from 9), and yellow otherwise.
     */
    private void updateAppearance() {
        // Set the text based on the label
        setText(this.label == HOLE_LABEL ? "" : String.valueOf(this.label));
        // Set the color based on label and position
        setBackground(this.label == HOLE_LABEL ? Color.GRAY : (this.label == this.position ? Color.GREEN : Color.YELLOW));
    }
    
    /**
     * Handles the tile click event.
     * 
     * Manages the behavior when the tile is clicked, 
     * including validating the move and updating the hole if the move is valid.
     */
    public void handleClick() {
        try {
            int oldLabel = this.label;
            
             // Check if the move is legal
            this.vcs.fireVetoableChange("moveCheck", oldLabel, HOLE_LABEL);
            // If it is, update the clicked tile
            setLabel(HOLE_LABEL);
            // And fire a property change event to notify listeners (adjacent tiles) to update the hole
            this.pcs.firePropertyChange("labelChange", oldLabel, HOLE_LABEL);

            updateAppearance();
        } catch (PropertyVetoException e) { // If the move is vetoed (illegal move)
            flashRed(); //flash the tile
            //System.out.println(e.getMessage());
        }
    }
    
    /**
     * Property change event handler.
     * 
     * Responds to property changes from other tiles or game components.
     * 
     * @param evt The PropertyChangeEvent containing event details.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (this.label == HOLE_LABEL && "labelChange".equals(evt.getPropertyName())) {
            int newLabel = (int) evt.getOldValue();
            //System.out.println("changing lable for " + newLabel); //DEBUG print
            setLabel(newLabel);
        }
        else if ("restart".equals(evt.getPropertyName())){
            List<Integer> randomLabels = (List<Integer>) evt.getNewValue();
            // Update the label based on the tile's position
            setLabel(randomLabels.get(this.position - 1));
        }
        else if ((this.position == 1 || this.position == 2) && "flip".equals(evt.getPropertyName())) {
            if (this.position == 1) {
                // NewValue = right label value of the following call: pcs.firePropertyChange("flip", label1, label2)
                setLabel((int) evt.getNewValue());
            } else if (this.position == 2) {
                // OldValue = left label value of the following call: pcs.firePropertyChange("flip", label1, label2);
                setLabel((int) evt.getOldValue());
            }
        }
    }
    
    /**
     * Sets adjacent tiles for listening to property changes.
     * 
     * Subscribes adjacent tiles to listen for firePropertyChange events.
     * Only adjacent tiles are subscribed since the "labelChange" can only affect adjacent tiles.
     * 
     * @param adjacentTiles A list of adjacent EightTile objects.
     */
    public void setAdjacentTiles(List<EightTile> adjacentTiles) {
        for (EightTile tile : adjacentTiles) {
            this.addPropertyChangeListener(tile::propertyChange);
        }
    }
    
    /**
     * Flashes the tile red when a move is vetoed.
     * 
     * Temporarily changes the tile's background color to red for half a second.
     */
    private void flashRed() {
        Color originalColor = getBackground();
        setBackground(Color.RED);
        Timer timer = new Timer(500, e -> setBackground(originalColor));
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Validates the position or label value - static function.
     * 
     * Checks if the given value is within the valid range for a tile.
     * 
     * @param val The value to validate.
     * 
     * @return True if the value is valid.
     * @return Dalse if the value is not valid.
     */
    private static boolean isValid(int val) {
        return val > 0 && val <= 9;
    }
}