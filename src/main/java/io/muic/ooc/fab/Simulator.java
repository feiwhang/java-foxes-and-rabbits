package io.muic.ooc.fab;


import io.muic.ooc.fab.view.SimulatorView;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Simulator {

    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // Lists of animals in the field.
    private final List<Animal> animals;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // Random generator
    private static final Random RANDOM = new Random();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        // set color for each animal type
        for (AnimalType animalType: AnimalType.values()) {
            view.setColor(animalType.getAnimalClass(), animalType.getColor());
        }
        view.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period
     * (4000 steps).
     */
    public void runLongSimulation() {
        simulate(4000);
    }

    /**
     * Run the simulation for the given number of steps. Stop before the given
     * number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        for (int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            // delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step. Iterate over
     * the whole field updating the state of each fox and rabbit.
     */
    public void simulateOneStep() {
        step++;

        // Provide space for newborn rabbits.
        List<Animal> newAnimals = new ArrayList<>();
        // Let all animals act.
        for (Iterator<Animal> it = animals.iterator(); it.hasNext();) {
            Animal animal =  it.next();
            animal.action(newAnimals);
            if (!animal.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        animals.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate() {
        
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                // probability to create fox or rabbit
                double random = RANDOM.nextDouble();
                Location location = new Location(row, col);
                double cumulativeProbability = 0;
                for (AnimalType animalType: AnimalType.values()) {
                    cumulativeProbability += animalType.getSpawnProbability();
                    if (random <= cumulativeProbability) {
                        Animal animal = AnimalFactory.createAnimal(animalType, true, field, location);
                        animals.add(animal);
                        break; // exit after animal is created
                    }
                }
                // leave the location empty.
            }
        }
    }

    /**
     * Pause for a given time.
     *
     * @param millisec The time to pause for, in milliseconds
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ie) {
            // wake up
        }
    }
}
