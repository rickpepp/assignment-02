package it.unibo.assignment_02.gui_program;

public interface Controller {
    public void addNode(String nodeName);
    public void addEdge(String sourceNode, String destinationNode);
    public void clear();
}
