package com.mycompany.theeightpuzzle;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeSupport;
import java.beans.PropertyVetoException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Faxy
 */
public class EightBoard extends JFrame {
    private final EightTile[] tiles;
    private final EightController controller;
    private final JButton restartButton;
    private final JButton flipButton;
    
    private final PropertyChangeSupport pcs;
    private final VetoableChangeSupport vcs;
    
    public EightBoard() {
        
        /*  
        // Uncomment these (along with the main() method at the bottom of the file) 
        // to make EightBoard a stand-alone executable board
        
        super("The 8 Puzzle Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());*/

        this.pcs = new PropertyChangeSupport(this);
        this.vcs = new VetoableChangeSupport(this);
        
        // Initialize the tiles array and controller
        tiles = new EightTile[9];
        controller = new EightController(9);
        vcs.addVetoableChangeListener(controller);

        // Create the main panel to hold the tiles in a 3x3 grid layout
        JPanel tilePanel = new JPanel();
        tilePanel.setLayout(new GridLayout(3, 3));

        // Initialize the tiles and add them to the panel
        for (int i = 0; i < 9; i++) {
            tiles[i] = new EightTile(i + 1, i + 1); // Position and initial label are the same
            tiles[i].addVetoableChangeListener(controller); // Register controller as VetoableChangeListener
            pcs.addPropertyChangeListener(tiles[i]); // Register tiles as listeners to the Restart event
            tilePanel.add(tiles[i]);
        }
        
         // Register controller as listener to the Restart event
        pcs.addPropertyChangeListener(controller);
        
        // Set PropertyChangeListener for adjacent tiles
        setAdjacencies();

        // Add the tile panel to the center of the frame
        this.add(tilePanel, BorderLayout.CENTER);

        // Create a panel for the buttons and controller label
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        // Initialize the RESTART button
        restartButton = new JButton("RESTART");
        restartButton.addActionListener((ActionEvent e) -> {
            initializeBoard();
        });

        // Initialize the FLIP button
        flipButton = new JButton("FLIP");
        flipButton.addActionListener((ActionEvent e) -> {
            flipBoard();
        });

        // Add components to the control panel as required
        controlPanel.add(controller);
        controlPanel.add(restartButton);
        controlPanel.add(flipButton);

        // Add the control panel to the bottom of the frame
        this.add(controlPanel, BorderLayout.SOUTH);

        // Initialize the board with a random configuration
        initializeBoard();

        // Set the frame size and make it visible
        this.setSize(400, 400);
        this.setVisible(true);
    }
    
    // Method to initialize or restart the board with a random configuration
    private void initializeBoard() {
        // Generate a random permutation of [1, 9]
        List<Integer> randomLabels = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            randomLabels.add(i);
        }
        Collections.shuffle(randomLabels);

        // Fire the restart update event to notify the tiles
        pcs.firePropertyChange("restart", null, randomLabels);
        
        // Calculate the new hole position
        int newHolePosition = randomLabels.indexOf(9) + 1;

        // Fire the holePosition update event to notify the controller
        pcs.firePropertyChange("restartHole", null, newHolePosition);
    }

    // Method to flip the board (see documentation)
    private void flipBoard() {
        try {
            // Check if flipping is allowed by firing a vetoable change event
            vcs.fireVetoableChange("flip", null, null);

            // If allowed, switch the labels of tiles in position 1 and 2
            int label1 = Integer.parseInt(tiles[0].getLabel());
            int label2 = Integer.parseInt(tiles[1].getLabel());

            // Fire a property change event to swap the labels
            pcs.firePropertyChange("flip", label1, label2);
        }
        catch (PropertyVetoException e) {
            // Flip not allowed
            //System.out.println(e.getMessage());
        }
    }
    
    private void setAdjacencies() {
        // Defining adjacency based on the 3x3 grid
        tiles[0].setAdjacentTiles(Arrays.asList(tiles[1], tiles[3]));     // Tile 1
        tiles[1].setAdjacentTiles(Arrays.asList(tiles[0], tiles[2], tiles[4])); // Tile 2
        tiles[2].setAdjacentTiles(Arrays.asList(tiles[1], tiles[5]));     // Tile 3
        tiles[3].setAdjacentTiles(Arrays.asList(tiles[0], tiles[4], tiles[6])); // Tile 4
        tiles[4].setAdjacentTiles(Arrays.asList(tiles[1], tiles[3], tiles[5], tiles[7])); // Tile 5
        tiles[5].setAdjacentTiles(Arrays.asList(tiles[2], tiles[4], tiles[8])); // Tile 6
        tiles[6].setAdjacentTiles(Arrays.asList(tiles[3], tiles[7]));     // Tile 7
        tiles[7].setAdjacentTiles(Arrays.asList(tiles[4], tiles[6], tiles[8])); // Tile 8
        tiles[8].setAdjacentTiles(Arrays.asList(tiles[5], tiles[7]));     // Tile 9
    }
    
    public static void main(String[] args) { // main here for execution purposes
        new EightBoard();
    }
}
