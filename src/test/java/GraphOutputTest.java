import graphviz.Graph;

public class GraphOutputTest {
    public static void main (String[] args) {
        Graph graph = new Graph(true);
        graph.setDefaults("", "shape = circle", "arrowhead = normal");

        for (Integer i = 1; i <= 12; i++) {
            graph.addNode(i.toString());
        }

        // Add main edges
        graph.setEdgeFormat("");
        graph.addEdgeFormatted("1", "3");
        graph.addEdgeFormatted("3", "5");
        graph.addEdgeFormatted("5", "7");
        graph.addEdgeFormatted("3", "6");
        graph.addEdgeFormatted("6", "2");
        graph.addEdgeFormatted("2", "8");
        graph.addEdgeFormatted("2", "4");
        graph.addEdgeFormatted("4", "10");
        graph.addEdgeFormatted("1", "9");
        graph.addEdgeFormatted("9", "11");
        graph.addEdgeFormatted("11", "12");

        // Add back edges
        graph.setEdgeFormat("style = dotted");
        graph.addEdgeFormatted("5", "1");
        graph.addEdgeFormatted("7", "3");
        graph.addEdgeFormatted("8", "3");
        graph.addEdgeFormatted("6", "1");
        graph.addEdgeFormatted("12", "9" );

        graph.outputToFile("graphviz_test.dot");
    }
}
