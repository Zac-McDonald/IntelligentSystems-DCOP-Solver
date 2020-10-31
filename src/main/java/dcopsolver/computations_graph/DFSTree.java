package dcopsolver.computations_graph;

import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.Variable;
import graphviz.Graph;

import java.util.*;

public class DFSTree {

    // Main class for the DFS tree
    // nodes - All nodes in the graph
    // edges - All edges in the graph
    // hD - divides nodes into hosts
    // hosts - number of hosts

    List<DFSNode> nodes = new ArrayList<>();
    List<DFSEdge> edges = new ArrayList<>();
    HostDivider hD;
    int hosts;
    List<DFSNode> topNodes = new ArrayList<>();

    public DFSTree () {
        // JavaBeans compliance
    }

    public DFSTree(HashMap<String, Variable> variables, HashMap<String, Constraint> constraints, int hosts){

        this.hosts = hosts;
        //populate variables
        for (Variable v : variables.values()){
            nodes.add(new DFSNode(v));
        }

        //populate edges
        for (Constraint c : constraints.values()){

            //this is used to track all variables that share a constraint
            List<DFSNode> temp = new ArrayList<>();
            for (Variable v : c.getVariables()){
                assert nodes != null;
                for (DFSNode n : nodes){
                    if ((n.var.equals(v)) && !(temp.contains(n))){
                        temp.add(n);
                    }
                }
            }

            //Iterator to keep location in Node list
            ListIterator<DFSNode> iterA = temp.listIterator();

            while(iterA.hasNext()){
                DFSNode a = iterA.next();

                //creates second iterator to establish edges with first, starting at current position
                ListIterator<DFSNode> iterB = temp.listIterator(iterA.nextIndex());
                while(iterB.hasNext()){
                    DFSNode b = iterB.next();

                    //checks for existing edge
                    if (!a.IsNeighboursWith(b)) {
                        DFSEdge e = new DFSEdge(a, b);
                        edges.add(e);
                        a.adjacent.add(e);
                        b.adjacent.add(e);
                    }
                }
            }
        }

        //starts DFS tree algorithm
        assert nodes != null;

        //check for node with most adjacent nodes
        DFSNode temp = null;
        for (DFSNode n: nodes) {
            if (temp == null)
                temp = n;
            if (n.adjacent.size() > temp.adjacent.size())
                temp = n;
        }
        if (temp != null)
            topNodes.add(temp);
        assert temp != null;
        Visit(temp);

        //check for separated networks
        for (DFSNode n: nodes) {
            if (!n.visited)
                topNodes.add(n);
                Visit(n);
        }
        DivideHosts();
    }

    public List<DFSNode> getNodes () {
        return nodes;
    }

    public void setNodes (List<DFSNode> nodes) {
        this.nodes = nodes;
    }

    public List<DFSEdge> getEdges () {
        return edges;
    }

    public void setEdges (List<DFSEdge> edges) {
        this.edges = edges;
    }

    public HostDivider gethD () {
        return hD;
    }

    public void sethD (HostDivider hD) {
        this.hD = hD;
    }

    public int getHosts () {
        return hosts;
    }

    public void setHosts (int hosts) {
        this.hosts = hosts;
    }

    public List<DFSNode> getTopNodes() { return topNodes; }

    public void setTopNode(List<DFSNode> topNodes) { this.topNodes = topNodes; }

    // recursive algorithm to create tree
    public void Visit(DFSNode u){
        u.visited = true;
        //look for edge with node not visited
        for (DFSEdge e: u.adjacent){
            DFSNode v = e.EndNode(u);
            if (!v.visited){
                //if parent and child wrong way around swap
                if (e.parent != u)
                    e.SwapParent();
                e.span = true;
                Visit(v);
            }
            //checks for back edge
            else if (!e.span){
                if (e.parent != u)
                    e.SwapParent();
            }
        }
    }

    //returns node that is holding variable
    public DFSNode GetNodeFromVariable(Variable var){
        for (DFSNode n: nodes){
            if (n.var.equals(var))
                return n;
        }
        return null;
    }

    //returns either real neighbours or pseudo neighbors of a variable
    public List<Variable> GetNeighbours(Variable var, boolean Pseudo){
        List<Variable> list =  new ArrayList<>();
        DFSNode node = GetNodeFromVariable(var);
        for (DFSNode neighbour: node.GetNeighbours(Pseudo))
            list.add(neighbour.var);
        return list;
    }

    //returns all neighbours of variable
    public List<Variable> GetAllNeighbours(Variable var){
        List<Variable> list =  new ArrayList<>();
        DFSNode node = GetNodeFromVariable(var);
        for (DFSNode neighbour: node.GetAllNeighbours())
            list.add(neighbour.var);
        return list;
    }

    //returns either real or pseudo children
    public List<Variable> GetChildren(Variable var, boolean Pseudo){
        List<Variable> list =  new ArrayList<>();
        DFSNode node = GetNodeFromVariable(var);
        for (DFSNode child: node.GetChildren(Pseudo))
            list.add(child.var);
        return list;
    }

    //returns all children
    public List<Variable> GetAllChildren(Variable var){
        List<Variable> list =  new ArrayList<>();
        DFSNode node = GetNodeFromVariable(var);
        for (DFSNode child: node.GetAllChildren())
            list.add(child.var);
        return list;
    }

    //returns either real or pseudo parents
    public List<Variable> GetParents(Variable var, boolean Pseudo){
        List<Variable> list =  new ArrayList<>();
        DFSNode node = GetNodeFromVariable(var);
        for (DFSNode parent: node.GetParents(Pseudo))
            list.add(parent.var);
        return list;
    }

    //returns all parents
    public List<Variable> GetAllParents(Variable var){
        List<Variable> list =  new ArrayList<>();
        DFSNode node = GetNodeFromVariable(var);
        for (DFSNode parent: node.GetAllParents())
            list.add(parent.var);
        return list;
    }

    public void DivideHosts(){
        hD = new HostDivider(nodes, hosts);
    }

    // creates a dot file to visualise the graph
    public void OutputGraph(){
        Graph graph = new Graph(true);
        graph.setDefaults("", "shape = circle", "arrowhead = normal");
        String[] colors = {
                "red",
                "blue",
                "green",
                "purple",
                "orange",
                "yellow",
                "cyan"
        };

        int i = 0;
        for (List<DFSNode> list: hD.hostNodes){
            for (DFSNode n: list){
                graph.addNode(n.name, "color = " + colors[i]);
            }
            i++;
        }

        for (DFSEdge e: edges){
            if (e.span)
                graph.addEdge(e.parent.name, e.child.name, "");
            else
                graph.addEdge(e.parent.name, e.child.name, "style = dotted, arrowhead = none, dir=back");
        }

        graph.outputToFile("DFSTreeGraph.dot");
    }

    public void PrintHosts(){
        hD.Print();
    }
}
