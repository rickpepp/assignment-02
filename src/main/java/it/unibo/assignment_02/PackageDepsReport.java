package it.unibo.assignment_02;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class PackageDepsReport {
    private final Graph<String, DefaultEdge> dependencyGraph;

    public PackageDepsReport(Graph<String, DefaultEdge> dependencyCollection) {
        this.dependencyGraph = dependencyCollection;
    }

    public Graph<String, DefaultEdge> getDependencies() {
        return this.dependencyGraph;
    }
}
