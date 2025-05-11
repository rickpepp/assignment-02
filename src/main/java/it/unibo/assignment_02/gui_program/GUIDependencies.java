package it.unibo.assignment_02.gui_program;

import io.reactivex.rxjava3.core.Flowable;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;


public class GUIDependencies extends JPanel implements Controller {

    public SingleGraph graph = new SingleGraph("Dependency Graph");
    private int counterClass = 0;
    private int counterDependence = 0;
    private final JLabel classAndDependencyCounterLabel;
    private final DependenciesModel model;

    private final String labelClassCounter = "Number of Classes/Interfaces = ";
    private final String labelDependenciesCounter = "Numbero of Dependencies = ";

    public GUIDependencies() {
        this.model = new DependenciesModel(this);

        graph.setStrict(false);
        graph.setAutoCreate(true);

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        View view = viewer.addDefaultView(false);
        setLayout(new BorderLayout());
        add((Component) view, BorderLayout.CENTER);

        JButton addButton = new JButton("Open folder");
        addButton.addActionListener(e -> selectPathToAnalyze());
        add(addButton, BorderLayout.SOUTH);

        this.classAndDependencyCounterLabel = new JLabel(labelClassCounter + " 0 " + labelDependenciesCounter + "0");
        add(this.classAndDependencyCounterLabel, BorderLayout.NORTH);
    }



    public synchronized void addNode(String nodeName) {
        if (graph.getNode(nodeName) == null && !nodeName.isEmpty()){
            Node n = graph.addNode(nodeName);
            n.setAttribute("ui.label", nodeName);
            this.counterClass++;
        }
    }
    
    public synchronized void addEdge(String sourceNode, String destinationNode) {
        graph.addEdge("Edge" + sourceNode + destinationNode, sourceNode, destinationNode);
        this.counterDependence++;
    }

    @Override
    public void clear() {
        graph.clear();
    }

    private void selectPathToAnalyze() {
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int r = j.showOpenDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            counterClass = 0;
            counterDependence = 0;
            Flowable<Graph<String, DefaultEdge>> flow = this.model.calcDependency(j.getSelectedFile().getAbsolutePath());
            flow.buffer(20).subscribe(
                    graphs -> {
                        graphs.forEach(graph -> {
                            graph.vertexSet().forEach(this::addNode);
                            graph.edgeSet().forEach(edge -> this.addEdge(
                                    graph.getEdgeSource(edge), graph.getEdgeTarget(edge)
                            ));
                            this.classAndDependencyCounterLabel.setText(labelClassCounter +
                                    counterClass + " " + labelDependenciesCounter + counterDependence);
                        });

                    },
                    Throwable::printStackTrace
            );;
        }

        this.classAndDependencyCounterLabel.setText(labelClassCounter + counterClass +
                " " + labelDependenciesCounter + counterDependence );
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        JFrame frame = new JFrame("Dependency graph");
        SwingUtilities.invokeLater(()->{
            GUIDependencies example = new GUIDependencies();
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(example);
            frame.setVisible(true);
        });

    }
}
