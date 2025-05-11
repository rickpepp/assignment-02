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
    private String pathToAnalyze;
    private final JLabel classAndDependencyCounterLabel;
    private final DependenciesModel model;
    private final JButton startButton;
    private final JLabel pathLabel;
    private final String labelClassCounter = "Number of Classes/Interfaces = ";
    private final String labelDependenciesCounter = "Numbero of Dependencies = ";

    public GUIDependencies() {
        this.model = new DependenciesModel(this);

        // Graph part
        graph.setStrict(false);
        graph.setAutoCreate(true);
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        View view = viewer.addDefaultView(false);
        setLayout(new BorderLayout());
        add((Component) view, BorderLayout.CENTER);

        // Dependency, class/interfaces counter labels and path label
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        this.classAndDependencyCounterLabel = new JLabel(labelClassCounter + " 0 " + labelDependenciesCounter + "0");
        labelsPanel.add(this.classAndDependencyCounterLabel);
        this.pathLabel = new JLabel("");
        this.pathLabel.setVisible(false);
        labelsPanel.add(this.pathLabel);
        add(labelsPanel, BorderLayout.NORTH);


        // Creating Buttons
        this.startButton = new JButton("Start To Analyze");
        this.startButton.setVisible(false);
        startButton.addActionListener(e -> {
            counterClass = 0;
            counterDependence = 0;
            Flowable<Graph<String, DefaultEdge>> flow = this.model.calcDependency(this.pathToAnalyze);
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
        });
        JButton addButton = new JButton("Open folder");
        addButton.addActionListener(e -> selectPathToAnalyze());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(startButton);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        add(buttonPanel, BorderLayout.SOUTH);
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
        // If the directory is chosen
        if (r == JFileChooser.APPROVE_OPTION) {
            pathToAnalyze = j.getSelectedFile().getAbsolutePath();
            this.startButton.setVisible(true);
            this.pathLabel.setVisible(true);
            this.pathLabel.setText("Path to analyze: " + pathToAnalyze);
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
