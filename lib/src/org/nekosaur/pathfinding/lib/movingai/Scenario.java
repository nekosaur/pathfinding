package org.nekosaur.pathfinding.lib.movingai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.nekosaur.pathfinding.lib.common.Vertex;

import java.util.ArrayList;

/**
 * @author nekosaur
 */
public class Scenario {

    String fileName;
    String version;

    //LinkedList<Experiment> experiments;
    List<Experiment> experiments;

    public Scenario(File scenarioFile) {

    	experiments = new ArrayList<Experiment>();
                
        try (BufferedReader br = new BufferedReader(new FileReader(scenarioFile))) {

            String line;

            // version
            version = br.readLine().substring(8);

            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");

                if (version.equals("0.0")) {
                    experiments.add(new Experiment(
                            new Vertex(Integer.parseInt(data[2]), Integer.parseInt(data[3])),
                            new Vertex(Integer.parseInt(data[4]), Integer.parseInt(data[5])),
                            Integer.parseInt(data[0]),
                            Double.parseDouble(data[6]),
                            data[1]));
                } else if (version.equals("1.0")) {
                    experiments.add(new Experiment(
                            new Vertex(Integer.parseInt(data[4]), Integer.parseInt(data[5])),
                            new Vertex(Integer.parseInt(data[6]), Integer.parseInt(data[7])),
                            Integer.parseInt(data[2]),
                            Integer.parseInt(data[3]),
                            Integer.parseInt(data[0]),
                            Double.parseDouble(data[8]),
                            data[1]));
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        System.out.println("Experiments loaded: " + experiments.size());

    }

    public List<Experiment> getExperiments() {
        return experiments;
    }
    
    public Experiment getExperiment(int index) {
        return experiments.get(index);
    }
    
    public void addExperiment(Vertex start, Vertex goal, int mapSize, double optimalLength) {
        experiments.add(new Experiment(start, goal, mapSize, mapSize, 0,optimalLength, fileName));
    }
    
}
