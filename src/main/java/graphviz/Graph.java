package graphviz;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
    public Boolean directed;
    public RankDir rankDirection;

    public String graphDefaults;
    public String nodeDefaults;
    public String edgeDefaults;

    public String nodeFormat;
    public String edgeFormat;

    private HashMap<String, Node> nodes;
    private ArrayList<Edge> edges;

    public Graph (Boolean directed) {
        this.directed = directed;
        this.rankDirection = RankDir.TB;

        graphDefaults = "";
        nodeDefaults = "";
        edgeDefaults = "";

        nodeFormat = "";
        edgeFormat = "";

        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    public void setDefaults (String graph, String node, String edge) {
        graphDefaults = graph;
        nodeDefaults = node;
        edgeDefaults = edge;
    }

    public void setNodeFormat (String format) {
        nodeFormat = format;
    }

    public void setEdgeFormat (String format) {
        edgeFormat = format;
    }

    public void addNode (String name) {
        addNode(name, "");
    }

    public void addNode (String name, String attributes) {
        nodes.put(name, new Node(name, attributes));
    }

    public void addNodeFormatted (String name) {
        addNode(name, nodeFormat);
    }

    public void addEdge (String from, String to) {
        addEdge(from, to, "");
    }

    public void addEdge (String from, String to, String attributes) {
        edges.add(new Edge(nodes.get(from), nodes.get(to), attributes));
    }

    public void addEdgeFormatted (String from, String to) {
        addEdge(from, to, edgeFormat);
    }

    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append(directed ? "digraph" : "graph");
        sb.append(" tree\n{");
        sb.append("\n\tfontsize = 12");
        sb.append("\n\trankdir = ").append(rankDirection.toString());

        // Add defaults
        sb.append("\n\n\tgraph [ ").append(graphDefaults).append(" ];");
        sb.append("\n\tnode [ ").append(nodeDefaults).append(" ];");
        sb.append("\n\tedge [ ").append(edgeDefaults).append(" ];");

        // Add Nodes
        sb.append("\n\n");
        for (Node n : nodes.values()) {
            sb.append("\t").append(n.toString()).append("\n");
        }

        // Add Edges
        sb.append("\n");
        for (Edge e : edges) {
            sb.append("\t").append(e.toString()).append("\n");
        }

        sb.append("}\n");

        return sb.toString();
    }

    public void outputToFile (String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(toString());
            writer.close();
            System.out.println("Successfully output to " + fileName);
        } catch (IOException e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }

    public enum RankDir { TB, BT, LR, RL }

    private class Node {
        public String name;
        public String attributes;

        public Node (String name, String attributes) {
            this.name = name;
            this.attributes = attributes;
        }

        public String toString () {
            return name + " [ " + attributes + "]";
        }
    }

    private class Edge {
        public String attributes;
        public Node start;
        public Node end;

        public Edge (Node start, Node end, String attributes) {
            this.start = start;
            this.end = end;
            this.attributes = attributes;
        }

        public String toString () {
            return start.name + " -> " + end.name + " [ " + attributes + "]";
        }
    }
}
