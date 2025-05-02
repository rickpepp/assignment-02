package it.unibo.assignment_02;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependencyAnalyserLibImpl implements DependencyAnalyserLib {
    @Override
    public Promise<ClassDepsReport> getClassDependencies(String filePath) {
        Vertx vx = Vertx.currentContext().owner();
        Promise<ClassDepsReport> promise = Promise.promise();
        vx.fileSystem().readFile(filePath)
                .onComplete(buffer -> {
                    CompilationUnit cu = StaticJavaParser.parse(buffer.result().toString());
                    DependencyVisitor dv =  new DependencyVisitor();
                    dv.visit(cu,null);
                    Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
                    g.addVertex(dv.getClassName());
                    Set<String> imports = dv.getSet();
                    for (String importDeclaration : imports) {
                        g.addVertex(importDeclaration);
                        g.addEdge(dv.getClassName(), importDeclaration);
                    }
                    promise.complete(new ClassDepsReport(g));
                }).onFailure(promise::fail).onComplete(ar -> vx.close());
        return promise;
    }

    @Override
    public Promise<PackageDepsReport> getPackageDependencies(String packagePath) {
        Vertx vx = Vertx.vertx();
        Promise<PackageDepsReport> promise = Promise.promise();
        Promise<Collection<String>> promisResult = (Promise<Collection<String>>) vx.executeBlocking(filePromise -> {
            try (Stream<Path> stream = Files.walk(Paths.get(packagePath))) {
                filePromise.complete(stream.filter(e -> Files.isRegularFile(e) && e.getFileName().toString().endsWith(".java")).map(e -> e.toFile().getAbsolutePath()).collect(Collectors.toSet()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        promisResult.future().onComplete(e -> {
            CompositeFuture f = Future.all(e.result().stream().map(this::getClassDependencies).map(Promise::future).collect(Collectors.toList()));
            f.onSuccess(compositeFuture -> {
                List<ClassDepsReport> totalresult = compositeFuture.result().list();
                ClassDepsReport r = totalresult.stream().reduce((a, b) -> {
                    Graphs.addGraph(a.getDependencies(),b.getDependencies());
                    return a;
                }).get();
                promise.complete(new PackageDepsReport(r.getDependencies()));
            });
        });
        return promise;
    }

    @Override
    public Promise<ProjectDepsReport> getProjectDependencies(String projectPath) {
        Vertx vx = Vertx.vertx();
        Promise<ProjectDepsReport> promise = Promise.promise();
        this.getPackageDependencies(projectPath + File.separator + "src").future().onComplete(
                packageDepsReportAsyncResult -> promise.complete(new ProjectDepsReport(packageDepsReportAsyncResult.result().getDependencies()))
        );
        return promise;
    }
}

class main {
    public static void main(String[] args) {
        /*String file = "/home/rick/Documenti/Università/Unibo/Programmazione ad Oggetti/OOP22-puzbob-main/src/main/java/it/unibo/puzbob/controller/GameLoop.java";
        DependencyAnalyserLib lib = new DependencyAnalyserLibImpl();
        lib.getClassDependencies(file)
                .future().onComplete(result -> {
            for(DefaultEdge e : result.result().getDependencies().edgeSet()){
                System.out.println(result.result().getDependencies().getEdgeSource(e) + " --> " + result.result().getDependencies().getEdgeTarget(e));
            }
        }).onFailure(result -> {
            System.out.println(result.toString());
        });*/
        /*DependencyAnalyserLib lib = new DependencyAnalyserLibImpl();
        lib.getPackageDependencies("/home/rick/Documenti/Università/Unibo/Programmazione ad Oggetti/OOP22-puzbob-main/src/main/java/it/unibo/puzbob/controller/")
                .future().onComplete(result -> {
                    for(DefaultEdge e : result.result().getDependencies().edgeSet()){
                        System.out.println(result.result().getDependencies().getEdgeSource(e) + " --> " + result.result().getDependencies().getEdgeTarget(e));
                    }
                }).onFailure(result -> {
                    System.out.println(result.toString());
                });*/
        DependencyAnalyserLib lib = new DependencyAnalyserLibImpl();
        lib.getProjectDependencies("/home/rick/Documenti/Università/Unibo/Programmazione ad Oggetti/OOP22-puzbob-main").future().onComplete(result -> {
            for(DefaultEdge e : result.result().getDependencies().edgeSet()){
                System.out.println(result.result().getDependencies().getEdgeSource(e) + " --> " + result.result().getDependencies().getEdgeTarget(e));
            }
        }).onFailure(result -> {
            System.out.println(result.toString());
        });
    }
}
