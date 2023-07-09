/*
 * [FireStation.java]
 * @author Alan Tang, Jaeyong Lee
 * @version Apr 20, 2022
 * The class containg the functionality and algorithm to solve the given problem
 */

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FireStationSolver {
    private Map<Town, Set<Town>> townsMap;

    public FireStationSolver(Map<Town, Set<Town>> townsMap) {
        this.townsMap = townsMap;
    }

    public Map<Town, Set<Town>> getTownsMap() {
        return townsMap;
    }

    public Set<Town> solve() {
        // create a copy of keyset
        Set<Town> townSet = new HashSet<>();
        townSet.addAll(townsMap.keySet());

        // create a set to hold stations
        Set<Town> stationSet = new HashSet<>();

        // greedily choose the town with most unsatisfied neighbours as the next station 
        while (!townSet.isEmpty()) {
            Town greatestTown = new Town("-1");
            int greatestScore = -1;

            for (Town currentTown : townSet) {
                int score = calculateScore(currentTown, townSet); // calculate score here
                if (score > greatestScore) {
                    greatestScore = score;
                    greatestTown = currentTown;
                }
            }

            townSet.remove(greatestTown);
            Set<Town> neighbours = townsMap.get(greatestTown);
            townSet.removeAll(neighbours);
            stationSet.add(greatestTown);
        }

        return stationSet;
    }

    private int calculateScore(Town currentTown, Set<Town> townSet) {
        int score = 0;
        for (Town neighbour : townsMap.get(currentTown)) {
            if (townSet.contains(neighbour)) {
                score = score + 1;
            }
        }
        return score;
    }
}
