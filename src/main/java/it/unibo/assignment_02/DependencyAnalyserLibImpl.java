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
        Vertx vx;
        try {
            vx = Vertx.currentContext().owner();
        } catch (NullPointerException e) {
            vx = Vertx.vertx();
        }
        Promise<ClassDepsReport> promise = Promise.promise();
        Vertx finalVx = vx;
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
                }).onFailure(promise::fail).onComplete(ar -> finalVx.close());
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
        Promise<ProjectDepsReport> promise = Promise.promise();
        this.getPackageDependencies(projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java").future().onComplete(
                packageDepsReportAsyncResult -> promise.complete(new ProjectDepsReport(packageDepsReportAsyncResult.result().getDependencies()))
        );
        return promise;
    }
}
