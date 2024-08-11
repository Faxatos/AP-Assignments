package com.mycompany.theeightpuzzle;

import javax.swing.JButton;
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
 *
 * @author Faxy
 */
public class EightTile extends JButton implements PropertyChangeListener{
    
    // Private properties for Position and Label
    private final int position; // Constant value set at startup
    private int label;          // Current label of the tile
    
    private static int HOLE_LABEL = 9; // Label of empty hole tile.

    // Support for bound and constrained properties
    private final PropertyChangeSupport pcs;
    private final VetoableChangeSupport vcs;
    
    /**
     * Default constructor for EightTile.
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
     * Constructor
     * @param position
     * @param label
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
    
    // Getter for Position (constant)
    public int getPosition() {
        return position;
    }

    // Getter and setter for Label
    @Override
    public String getLabel() {
        return String.valueOf(label);
    }

    public void setLabel(int newLabel){
        this.label = newLabel;

        // Update the visual appearance of the tile
        updateAppearance();
    }
    
    // Add property change listener
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if(this.pcs != null){
            this.pcs.addPropertyChangeListener(listener);
        }
    }

    // Add vetoable change listener
    @Override
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        if(this.pcs != null){
            this.vcs.addVetoableChangeListener(listener);
        }
    }
    
    // Method to update the appearance of the tile
    private void updateAppearance() {
        // Set the text based on the label
        setText(label == HOLE_LABEL ? "" : String.valueOf(label));
        // Set the color based on label and position
        setBackground(label == HOLE_LABEL ? Color.GRAY : (label == position ? Color.GREEN : Color.YELLOW));
    }
    
    // Method to handle the tile click event
    public void handleClick() {
        try {
            int oldLabel = this.label;
            
             // Check if the move is legal
            vcs.fireVetoableChange("moveCheck", oldLabel, HOLE_LABEL);
            // If it is, update the clicked tile
            setLabel(HOLE_LABEL);
            // And fire a property change event to notify listeners (adjacent tiles) to update the hole
            pcs.firePropertyChange("labelChange", oldLabel, HOLE_LABEL);

            updateAppearance();
        } catch (PropertyVetoException e) { // If the move is vetoed (illegal move)
            flashRed(); //flash the tile
            //System.out.println(e.getMessage());
        }
    }
    
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
    
    // Subscribe adjacent tiles to listen for property changes
    public void setAdjacentTiles(List<EightTile> adjacentTiles) {
        for (EightTile tile : adjacentTiles) {
            this.addPropertyChangeListener(tile::propertyChange);
        }
    }
    
    // Flash the tile red for half a second if move is vetoed
    private void flashRed() {
        Color originalColor = getBackground();
        setBackground(Color.RED);
        Timer timer = new Timer(500, e -> setBackground(originalColor));
        timer.setRepeats(false);
        timer.start();
    }
    
    // Method to support the "restart" action, setting the label to an initial value
    public void restart(int initialLabel) {
        setLabel(initialLabel);
        /*try {
            setLabel(initialLabel);
        } catch (PropertyVetoException e) {
            // Handle if restart logic needs to be adjusted
        }*/
    }
    
    // Util for position and label validity
    private static boolean isValid(int val) {
        return val > 0 && val <= 9;
    }
}