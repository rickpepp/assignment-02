package it.unibo.assignment_02.gui_program;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GUIDependencies extends JPanel implements Controller {

    public SingleGraph graph = new SingleGraph("Dependency Graph");
    private int counter = 0;
    private JLabel classCounter; // Contatore per i nomi dei nodi
    private DependenciesModel model;

    private List<String> testList = new ArrayList<>();

    public GUIDependencies() {
        this.model = new DependenciesModel(this);

        // Configurazione del grafo iniziale
        graph.setStrict(false);
        graph.setAutoCreate(true);

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        View view = viewer.addDefaultView(false);
        setLayout(new BorderLayout());
        add((Component) view, BorderLayout.CENTER);

        // Pulsante per aggiungere vertici e spigoli
        JButton addButton = new JButton("Aggiungi Vertice");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPathToAnalyze();
            }
        });
        add(addButton, BorderLayout.SOUTH);

        this.classCounter = new JLabel("Class count = 0");
        add(this.classCounter, BorderLayout.NORTH);

    }



    public synchronized void addNode(String nodeName) {
        if (graph.getNode(nodeName) == null && !nodeName.isEmpty()){
            Node n = graph.addNode(nodeName);
            n.setAttribute("ui.label", nodeName);
            this.counter++;
            //System.out.println(counter);
        }
    }
    
    public synchronized void addEdge(String sourceNode, String destinationNode) {
        graph.addEdge("Edge" + sourceNode + destinationNode, sourceNode, destinationNode);
    }

    @Override
    public void clear() {
        graph.clear();
    }

    private void selectPathToAnalyze() {
        // Aggiungi un nuovo nodo
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // set the selection mode to directories only
        j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int r = j.showOpenDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            counter = 0;
            // set the label to the path of the selected directory
            this.model.calcDependency(j.getSelectedFile().getAbsolutePath()).delay(100, TimeUnit.MILLISECONDS).subscribe(
                    graph -> {
                        graph.vertexSet().forEach(this::addNode);
                        graph.edgeSet().forEach(edge -> this.addEdge(
                            graph.getEdgeSource(edge), graph.getEdgeTarget(edge)
                    ));
                    },
                    Throwable::printStackTrace
            );;
        }

        this.classCounter.setText("Class counter = " + counter);
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        JFrame frame = new JFrame("Dependency graph");
        GUIDependencies example = new GUIDependencies();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(example);
        frame.setVisible(true);
    }
}
