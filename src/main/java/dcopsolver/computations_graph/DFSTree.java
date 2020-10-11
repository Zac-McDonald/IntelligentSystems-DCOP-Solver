package dcopsolver.computations_graph;

import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DFSTree {
    List<DFSNode> nodes = new ArrayList<>();
    List<DFSEdge> edges = new ArrayList<>();

    public DFSTree(HashMap<String, Variable> variables, HashMap<String, Constraint> constraints){
        for (Variable v : variables.values()){
            nodes.add(new DFSNode(v));
        }
        for (Constraint c : constraints.values()){
            List<DFSNode> temp = new ArrayList<>();
            for (Variable v : c.variables){
                assert nodes != null;
                for (DFSNode n : nodes){
                    if ((n.v == v) && !(temp.contains(n))){
                        temp.add(n);
                    }
                }
            }

            Iterator<DFSNode> iterA = temp.iterator();

            while(iterA.hasNext()){
                DFSNode a = iterA.next();
                Iterator<DFSNode> iterB;
                iterB = iterA;
                while(iterB.hasNext()){
                    DFSNode b = iterB.next();
                    if (!EdgeExists(a,b)) {
                        DFSEdge e = new DFSEdge(a, b);
                        edges.add(e);
                        a.adjacent.add(e);
                        b.adjacent.add(e);
                    }
                }
            }
        }
        assert nodes != null;
        for (DFSNode n: nodes)
            if (!n.visited)
                Visit(n);
    }

    public Boolean EdgeExists(DFSNode a, DFSNode b){
        for (DFSEdge e: edges){
            if(((e.endA == a) && (e.endB == b)) || ((e.endA == b) && e.endB == a))
                return true;
        }
        return false;
    }

    public void Visit(DFSNode u){
        u.visited = true;
        for (DFSEdge e: u.adjacent){
            DFSNode v = e.EndNode(u);
            if (!v.visited){
                e.span = true;
                Visit(v);
            }
        }
    }
}
