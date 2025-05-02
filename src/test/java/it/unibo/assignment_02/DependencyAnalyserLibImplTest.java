package it.unibo.assignment_02;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class DependencyAnalyserLibImplTest {

    @Test
    void getClassDependenciesTest(Vertx vertx, VertxTestContext testContext) {
        DependencyAnalyserLib lib = new DependencyAnalyserLibImpl();
        String path = "src/test/resources";
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
        testContext.assertComplete( lib.getClassDependencies(absolutePath + "/pps-lab01b/src/main/java/e1/OverDraftDecorator.java").future() )
                .onComplete(classDepsReportAsyncResult ->
                        testContext.verify(() -> {
                            Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
                            g.addVertex("OverDraftDecorator");
                            g.addVertex("BaseDecoratorBankAccount");
                            g.addVertex("BankAccount");
                            g.addVertex("IllegalStateException");
                            g.addEdge("OverDraftDecorator", "BaseDecoratorBankAccount");
                            g.addEdge("OverDraftDecorator", "BankAccount");
                            g.addEdge("OverDraftDecorator", "IllegalStateException");
                            assertEquals(getStringFromGraph(classDepsReportAsyncResult.result().getDependencies()), getStringFromGraph(g));
                            testContext.completeNow();
                        }));
    }

    @Test
    void getPackageDependencies(Vertx vertx, VertxTestContext testContext) {
        DependencyAnalyserLib lib = new DependencyAnalyserLibImpl();
        String path = "src/test/resources";
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
        testContext.assertComplete( lib.getPackageDependencies(absolutePath + "/pps-lab01b/src/main/java/e1").future() )
                .onComplete(packageDepsReportAsyncResult ->
                        testContext.verify(() -> {
                            Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
                            g.addVertex("OverDraftDecorator");
                            g.addVertex("BaseDecoratorBankAccount");
                            g.addVertex("BankAccount");
                            g.addVertex("IllegalStateException");
                            g.addVertex("BankAccountFactory");
                            g.addVertex("WithdrawFunctionFeeDecorator");
                            g.addVertex("CoreBankAccount");
                            g.addVertex("Function");
                            g.addEdge("BankAccountFactory", "BankAccount");
                            g.addEdge("BankAccountFactory", "WithdrawFunctionFeeDecorator");
                            g.addEdge("BankAccountFactory", "OverDraftDecorator");
                            g.addEdge("BankAccountFactory", "CoreBankAccount");
                            g.addEdge("BankAccountFactory", "BaseDecoratorBankAccount");
                            g.addEdge("BaseDecoratorBankAccount", "BankAccount");
                            g.addEdge("CoreBankAccount", "BankAccount");
                            g.addEdge("OverDraftDecorator", "BaseDecoratorBankAccount");
                            g.addEdge("OverDraftDecorator", "BankAccount");
                            g.addEdge("OverDraftDecorator", "IllegalStateException");
                            g.addEdge("WithdrawFunctionFeeDecorator", "BaseDecoratorBankAccount");
                            g.addEdge("WithdrawFunctionFeeDecorator", "Function");
                            g.addEdge("WithdrawFunctionFeeDecorator", "BankAccount");
                            assertEquals(getStringFromGraph(packageDepsReportAsyncResult.result().getDependencies()), getStringFromGraph(g));
                            testContext.completeNow();
                        }));
    }

    @Test
    void getProjectDependencies() {
    }

    private String getStringFromGraph(Graph<String, DefaultEdge> graph) {
        List<String> result = new ArrayList<>();
        for(DefaultEdge e : graph.edgeSet()){
            result.add(graph.getEdgeSource(e) + " --> " + graph.getEdgeTarget(e) + "\n");}
        return result.stream().sorted().toList().toString();
    }
}