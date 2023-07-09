/*
 * [MapSerializer.java]
 * @author Alan Tang, Jaeyong Lee
 * @version Apr 28, 2022
 * A helper class to read from/write to files 
 */

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class TownMapSerializer {
    private static final String TOWN = "Town";
    private static final String EDGE = "Edge";

    public static void writeMapToFile(Map<Town, Set<Town>> map, File outputFile) {
        try {
            PrintWriter fileWriter = new PrintWriter(outputFile);
            // adding towns 
            for (Town currentTown : map.keySet()) {
                String pointString = String.format("{%d %d}", (int) currentTown.getOrigin().getX(),
                        (int) currentTown.getOrigin().getY());
                fileWriter.println("Town " + currentTown.getName() + pointString);
            }

            // adding edges 
            for (Map.Entry<Town, Set<Town>> entry : map.entrySet()) {
                Town currentTown = entry.getKey();
                for (Town nextTown : entry.getValue()) {
                    fileWriter.println("Edge " + currentTown.getName() + " " + nextTown.getName());
                }
            }
            fileWriter.close();
        } catch (FileNotFoundException fileNotFound) {
            System.err.println(String.format("Error opening file to read: file %s not found", outputFile.getName()));
        }
    }

    public static Map<Town, Set<Town>> readMapFromFile(File inputFile) {
        Map<Town, Set<Town>> map = new HashMap<>();

        // used to find towns that already exist
        // we need to do this since only have a town's coordinates when reading it as a node,
        // and not when connecting them as edges
        // so we use this to a match town id to its respective town with coord information
        Map<String, Town> townFinder = new HashMap<>();

        try {
            Scanner fileReader = new Scanner(inputFile);
            while (fileReader.hasNextLine()) {
                String[] data = fileReader.nextLine().split("[ {}]"); // split spaces and curly braces
                String type = data[0];
                if (type.equals(TOWN)) {
                    // we are reading a town's information
                    String id = data[1];
                    int xCoord = Integer.parseInt(data[2]);
                    int yCoord = Integer.parseInt(data[3]);
                    Town newTown = new Town(id, new Point(xCoord, yCoord));
                    map.put(newTown, new HashSet<>());
                    townFinder.put(id, newTown);
                } else if (type.equals(EDGE)) {
                    // we are reading an edge
                    String id1 = data[1];
                    String id2 = data[2];
                    Town town1 = townFinder.get(id1); // use the town with a matching id that has coordinate info
                    Town town2 = townFinder.get(id2);
                    map.get(town1).add(town2);
                    map.get(town2).add(town1);
                }
            }
            fileReader.close();
        } catch (FileNotFoundException fileNotFound) {
            System.err.println(String.format("Error opening file to read: file %s not found", inputFile.getName()));
        }
        return map;
    }
}