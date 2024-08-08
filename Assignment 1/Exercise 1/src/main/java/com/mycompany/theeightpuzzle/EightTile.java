package com.mycompany.theeightpuzzle;

import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.beans.VetoableChangeListener;

/**
 *
 * @author Faxy
 */
public class EightTile extends JButton{
    
    // Private properties for Position and Label
    private final int position; // Constant value set at startup
    private int label;          // Current label of the tile
     private final int holeLabel = 9; // Label of empty hole tile.

    // Support for bound and constrained properties
    private PropertyChangeSupport pcs;
    private VetoableChangeSupport vcs;
    
    // Default constructor
    public EightTile() {
        super();
        this.position=-1;
    }
    
    // Constructor
    public EightTile(int position, int label) {
        if(!isValid(position) || !isValid(label)) //params validity check
            throw new IllegalArgumentException();
        
        this.position = position;
        this.label = label;

        // Initialize property change support
        pcs = new PropertyChangeSupport(this);
        vcs = new VetoableChangeSupport(this);

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
        pcs.addPropertyChangeListener(listener);
    }

    // Add vetoable change listener
    @Override
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vcs.addVetoableChangeListener(listener);
    }
    
    // Method to update the appearance of the tile
    private void updateAppearance() {
        // Set the text based on the label
        if (label == holeLabel) {
            setText("");
            setBackground(Color.GRAY); // Hole
        } else {
            setText(String.valueOf(label));
            if (position == label) {
                setBackground(Color.GREEN); // Correct position
            } else {
                setBackground(Color.YELLOW); // Incorrect position
            }
        }
    }
    
    // Method to handle the tile click event
    public void handleClick() {
        try { // Validate if the move is legal (not vetoed)
            int oldLabel = this.label;
            
             // Attempt to change the label (to the hole)
            vcs.fireVetoableChange("label", oldLabel, holeLabel);
            this.label = holeLabel;
            pcs.firePropertyChange("label", oldLabel, holeLabel);

            // If successful, update the appearance
            updateAppearance();
        } catch (PropertyVetoException e) { // If the move is vetoed (illegal move)
            flashRed(); //flash the tile
        } catch (NumberFormatException e){
            System.out.println(e);
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