package dcopsolver.computations_graph;

import dcopsolver.dcop.Variable;

import java.util.ArrayList;
import java.util.List;

public class DFSNode {
    String name;
    Variable v;
    Boolean visited;
    List<DFSEdge> adjacent = new ArrayList<>();

    public DFSNode (Variable v){
        this.v = v;
        name = v.name;
        visited = false;
    }
}
