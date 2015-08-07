package org.nekosaur.pathfinding.lib.movingai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author nekosaur
 */
public class MovingAI {
	
	public static int[][] loadMap(String dataFilePath) {
		return MovingAI.loadMap(new File(dataFilePath));
	}

    public static int[][] loadMap(File dataFile) {
    
		try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {

			String line;

			// type
			String type = br.readLine();
			// height
			int height = Integer.parseInt(br.readLine().substring(7));
			// width
			int width = Integer.parseInt(br.readLine().substring(6));
			// map start
			br.readLine();

			int y = 0;
			int[][] data = new int[height][width];
			// map data
			while ((line = br.readLine()) != null) {

				for (int x = 0; x < line.length(); x++) {
					switch (line.charAt(x)) {
						case '.':
							data[y][x] = 0;
							break;
						case 'G':
							data[y][x] = 0;
							break;
						case '@':
							data[y][x] = 1;
							break;
						case 'O':
							data[y][x] = 1;
							break;
						case 'T':
							data[y][x] = 1;
							break;
						case 'W':
							data[y][x] = 1;
							break;
						case 'S':
							data[y][x] = 1;
							break;
						default:
							data[y][x] = 1;
							break;
					}
				}

				y++;
			}

			return data;

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
    public static void saveMap(int[][] data, File mapFile) {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(mapFile))) {
    		
    		bw.write("type octile\n");
    		bw.write("height " + data.length + "\n");
    		bw.write("width " + data[0].length + "\n");
    		bw.write("map\n");
    		
    		for (int y = 0; y < data.length; y++) {
    			for (int x = 0; x < data[0].length; x++) {
    				if (data[y][x] == 0) {
    					bw.write('.');
    				} else if (data[y][x] == 1) {
    					bw.write('@');
    				}
    			}
    			bw.write('\n');
    		}
    		
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}
    }
    
    public static Scenario loadScenario(String scenarioFilePath) {
    	File f = new File(scenarioFilePath);
    	return new Scenario(f);
    }
}