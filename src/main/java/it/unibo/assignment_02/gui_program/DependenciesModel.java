package it.unibo.assignment_02.gui_program;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.Vertx;
import it.unibo.assignment_02.DependencyVisitor;
import org.apache.commons.io.FileUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependenciesModel {
    private Controller controller;
    Observable<Graph<String, DefaultEdge>> observable;

    public DependenciesModel(Controller controller) {
        this.controller = controller;
    }

    public void calcDependency(String srcPath) {
        controller.clear();
        Observable.create(emitter -> {
            try (Stream<Path> stream = Files.walk(Paths.get(srcPath))) {
                stream.filter(e -> Files.isRegularFile(e) && e.getFileName().toString().endsWith(".java")).map(e -> e.toFile().getAbsolutePath()).forEach(emitter::onNext);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        }).map(filePath -> {


            Path path = Paths.get((String) filePath);

            BufferedReader reader = Files.newBufferedReader(path);
            String line;
            StringBuilder content = new StringBuilder();

            // Leggi ogni riga fino alla fine del file
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator()); // Aggiungi la riga e un separatore di linea
            }
            return content.toString();
        }).map(this::getDependencyGraph).subscribe(
                graph -> {
                    graph.vertexSet().forEach(this.controller::addNode);
//                    graph.edgeSet().forEach(edge -> this.controller.addEdge(
//                            graph.getEdgeSource(edge), graph.getEdgeTarget(edge)
//                    ));
                },
                Throwable::printStackTrace
        );
    }

    private Graph<String, DefaultEdge> getDependencyGraph(String javaFileString) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFileString);
            DependencyVisitor dv =  new DependencyVisitor();
            dv.visit(cu,null);
            Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
            g.addVertex(dv.getClassName());
            Set<String> imports = dv.getSet();
            for (String importDeclaration : imports) {
                g.addVertex(importDeclaration);
                g.addEdge(dv.getClassName(), importDeclaration);
            }
            return g;
        } catch (Exception e) {
            return new DefaultDirectedGraph<>(DefaultEdge.class);
        }

    }
}
