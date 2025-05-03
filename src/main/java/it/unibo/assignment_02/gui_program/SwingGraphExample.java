package it.unibo.assignment_02.gui_program;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class SwingGraphExample extends JPanel {

    public SingleGraph graph = new SingleGraph("Test graph");
    private Random random = new Random();
    private int nodeCount = 0; // Contatore per i nomi dei nodi

    public SwingGraphExample() {
        // Configurazione del grafo iniziale
        graph.setStrict(false);
        graph.setAutoCreate(true);

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        View view = viewer.addDefaultView(false);
        setLayout(new BorderLayout());
        add((Component) view, BorderLayout.CENTER);

        // Pulsante per aggiungere vertici e spigoli
        JButton addButton = new JButton("Aggiungi Vertice");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRandomNodeAndEdge();
            }
        });
        add(addButton, BorderLayout.SOUTH);

    }

    private void addRandomNodeAndEdge() {
        // Aggiungi un nuovo nodo
        String newNodeName = "Node" + nodeCount++;
        graph.addNode(newNodeName);

        // Aggiungi un arco casuale a un nodo esistente, se ci sono nodi nel grafo
        if (nodeCount > 1) {
            int existingNodeIndex = random.nextInt(nodeCount - 1); // Scegli un nodo esistente
            String existingNodeName = "Node" + existingNodeIndex;
            graph.addEdge("Edge" + existingNodeIndex + newNodeName, existingNodeName, newNodeName);
        }
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        JFrame frame = new JFrame("Graph Example");
        SwingGraphExample example = new SwingGraphExample();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(example);
        frame.setVisible(true);
    }
}
