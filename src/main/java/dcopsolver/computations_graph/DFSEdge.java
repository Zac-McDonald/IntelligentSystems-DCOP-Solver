package dcopsolver.computations_graph;

import java.util.List;

public class DFSEdge {
    Boolean span;
    DFSNode endA;
    DFSNode endB;

    public DFSEdge(DFSNode a, DFSNode b){
        endA = a;
        endB = b;
        span = false;
    }

    public DFSNode EndNode(DFSNode a){
        if (a == endA)
            return endB;
        else
            return endA;
    }
}
