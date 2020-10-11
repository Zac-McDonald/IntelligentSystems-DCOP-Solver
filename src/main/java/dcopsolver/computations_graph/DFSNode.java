package dcopsolver.computations_graph;

import dcopsolver.dcop.Variable;

import java.util.List;

public class DFSNode {
    Variable v;
    Boolean visited;
    List<DFSEdge> adjacent;

    public DFSNode (Variable v){
        this.v = v;
        visited = false;
    }
}
