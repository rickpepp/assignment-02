package it.unibo.assignment_02.gui_program;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.assignment_02.DependencyVisitor;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

public class DependenciesModel {
    private Controller controller;
    Observable<Graph<String, DefaultEdge>> observable;

    public DependenciesModel(Controller controller) {
        this.controller = controller;
    }

    public @NonNull Flowable<Graph<String, DefaultEdge>> calcDependency(String srcPath) {
        controller.clear();
        return Observable
                .create(emitter -> {
                    try (Stream<Path> stream = Files.walk(Paths.get(srcPath))) {
                        stream.filter(e -> Files.isRegularFile(e) &&
                                e.getFileName().toString().endsWith(".java"))
                                    .map(e -> e.toFile().getAbsolutePath()).forEach(emitter::onNext);
                        emitter.onComplete();
                    } catch (IOException e) {
                        emitter.onError(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .map(filePath -> {
                    Path path = Paths.get((String) filePath);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(String.valueOf(path)),
                            StandardCharsets.UTF_8));
                    String line;
                    StringBuilder content = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        content.append(line).append(System.lineSeparator());
                    }
                    return content.toString();
                })
                .map(javaPath -> {
                    StaticJavaParser.setConfiguration(new ParserConfiguration()
                            .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21));
                    CompilationUnit cu;
                    try {
                        cu = StaticJavaParser.parse(javaPath);
                        DependencyVisitor dv =  new DependencyVisitor();
                        dv.visit(cu,null);
                        return dv;
                    } catch (Exception e) {
                        return new DependencyVisitor();
                    }
                })
                .map(dv -> {
                    Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
                    g.addVertex(dv.getClassName());
                    Set<String> imports = dv.getSet();
                    for (String importDeclaration : imports) {
                        g.addVertex(importDeclaration);
                        g.addEdge(dv.getClassName(), importDeclaration);
                    }
                    return g;
                }).toFlowable(BackpressureStrategy.BUFFER);
    }
}
