package it.unibo.assignment_02;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                            Graph<String, DefaultEdge> g = createGraphFromJson("ClassGraphResultExpected.json");
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
                            Graph<String, DefaultEdge> g = createGraphFromJson("PackageGraphResultExpected.json");
                            assertEquals(getStringFromGraph(packageDepsReportAsyncResult.result().getDependencies()), getStringFromGraph(g));
                            testContext.completeNow();
                        }));
    }

    @Test
    void getProjectDependencies() {
    }

    private Graph<String, DefaultEdge> createGraphFromJson(String jsonPath) throws IOException {
        InputStream in= Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(jsonPath);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(in, Map.class);
        List<String> vertices = (List<String>) jsonMap.get("vertices");
        List<Map<String, String>> edges = (List<Map<String, String>>) jsonMap.get("edges");
        Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        for (String vertex: vertices) {
            g.addVertex(vertex);
        }
        for (Map<String, String> edge : edges) {
            g.addEdge(edge.get("source"), edge.get("target"));
        }
        return g;
    }

    private String getStringFromGraph(Graph<String, DefaultEdge> graph) {
        List<String> result = new ArrayList<>();
        for(DefaultEdge e : graph.edgeSet()){
            result.add(graph.getEdgeSource(e) + " --> " + graph.getEdgeTarget(e) + "\n");}
        return result.stream().sorted().toList().toString();
    }
}