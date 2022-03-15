package logic.dataManagment;

import logic.DTO.GraphDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GraphManager {
    private final Set<GraphDTO> graphSet;

    public GraphManager() {
        graphSet = new HashSet<>();
    }

    public synchronized void addGraph(GraphDTO graph) {
        graphSet.add(graph);
    }

    public synchronized void removeGraph(GraphDTO graph) {
        graphSet.remove(graph);
    }

    public synchronized Set<GraphDTO> getGraphs() {
        return Collections.unmodifiableSet(graphSet);
    }

    public boolean isGraphExists(GraphDTO graph) {
        return graphSet.contains(graph);
    }
}
