package it.unibo.assignment_02;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

public class DependencyAnalyserLibImpl implements DependencyAnalyserLib {
    @Override
    public Promise<ClassDepsReport> getClassDependencies(String filePath) {
        Vertx vx = Vertx.vertx();
        Promise<ClassDepsReport> promise = Promise.promise();
        vx.fileSystem().readFile(filePath)
                .onComplete(buffer -> {
                    CompilationUnit cu = StaticJavaParser.parse(buffer.result().toString());
                    DependencyVisitor dv =  new DependencyVisitor();
                    dv.visit(cu,null);
                    Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
                    g.addVertex("base");
                    Set<String> imports = dv.getSet();
                    for (String importDeclaration : imports) {
                        g.addVertex(importDeclaration);
                        g.addEdge("base", importDeclaration);
                    }
                    promise.complete(new ClassDepsReport(g));
                    vx.close();
                });
        return promise;
    }

    @Override
    public Promise<PackageDepsReport> getPackageDependencies() {
        return null;
    }

    @Override
    public Promise<ProjectDepsReport> getProjectDependencies() {
        return null;
    }
}

class main {
    public static void main(String[] args) {
        String file = "/home/rick/Documenti/Università/Unibo/Programmazione ad Oggetti/OOP22-puzbob-main/src/main/java/it/unibo/puzbob/controller/GameLoop.java";
        DependencyAnalyserLib lib = new DependencyAnalyserLibImpl();
        lib.getClassDependencies(file)
                .future().onComplete(result -> {
            for(DefaultEdge e : result.result().getDependencies().edgeSet()){
                System.out.println(result.result().getDependencies().getEdgeSource(e) + " --> " + result.result().getDependencies().getEdgeTarget(e));
            }
        }).onFailure(result -> {
            System.out.println(result.toString());
        });
    }
}
