package dcopsolver.computations_graph;

public class DFSEdge {

    // represents edge of DFS tree
    // span - boolean if its a span edge
    // parent - Parent node
    // child - Child node

    Boolean span;
    DFSNode parent;
    DFSNode child;

    public DFSEdge () {
        // JavaBeans compliance
    }

    public Boolean getSpan () {
        return span;
    }

    public void setSpan (Boolean span) {
        this.span = span;
    }

    public DFSNode getParent () {
        return parent;
    }

    public void setParent (DFSNode parent) {
        this.parent = parent;
    }

    public DFSNode getChild () {
        return child;
    }

    public void setChild (DFSNode child) {
        this.child = child;
    }

    public DFSEdge(DFSNode parent, DFSNode child){
        this.parent = parent;
        this.child = child;
        span = false;
    }

    //returns opposite end of edge
    public DFSNode EndNode(DFSNode startEnd){
        if (startEnd.equals(parent))
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
