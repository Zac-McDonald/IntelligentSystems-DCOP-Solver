package dcopsolver.computations_graph;

public class DFSEdge {

    // represents edge of DFS tree
    // span - boolean if its a span edge
    // parent - Parent node
    // child - Child node

    Boolean span;
    DFSNode parent;
    DFSNode child;

    public DFSEdge(DFSNode a, DFSNode b){
        parent = a;
        child = b;
        span = false;
    }

    //returns opposite end of edge
    public DFSNode EndNode(DFSNode a){
        if (a == parent)
            return child;
        else
            return parent;
    }

    //swaps child/parent
    public void SwapParent(){
        DFSNode temp = child;
        child = parent;
        parent = temp;
    }
}
