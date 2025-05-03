package it.unibo.assignment_02.lib;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class ClassDepsReport {
    private final Graph<String, DefaultEdge> dependencyGraph;

    public ClassDepsReport(Graph<String, DefaultEdge> dependencyCollection) {
        this.dependencyGraph = dependencyCollection;
    }

    public Graph<String, DefaultEdge> getDependencies() {
        return this.dependencyGraph;
    }
}
