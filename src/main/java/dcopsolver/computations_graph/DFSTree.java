package dcopsolver.computations_graph;

import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DFSTree {

    // Main class for the DFS tree
    // nodes - All nodes in the graph
    // edges - All edges in the graph

    List<DFSNode> nodes = new ArrayList<>();
    List<DFSEdge> edges = new ArrayList<>();

    public DFSTree(HashMap<String, Variable> variables, HashMap<String, Constraint> constraints){

        //populate variables
        for (Variable v : variables.values()){
            nodes.add(new DFSNode(v));
        }

        //populate edges
        for (Constraint c : constraints.values()){

            //this is used to track all variables that share a constraint
            List<DFSNode> temp = new ArrayList<>();
            for (Variable v : c.variables){
                assert nodes != null;
                for (DFSNode n : nodes){
                    if ((n.v == v) && !(temp.contains(n))){
                        temp.add(n);
                    }
                }
            }

            //Iterator to keep location in Node list
            Iterator<DFSNode> iterA = temp.iterator();

            while(iterA.hasNext()){
                DFSNode a = iterA.next();

                //creates second iterator to establish edges with first, starting at current position
                Iterator<DFSNode> iterB;
                iterB = iterA;
                while(iterB.hasNext()){
                    DFSNode b = iterB.next();

                    //checks for existing edge
                    if (!EdgeExists(a,b)) {
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
        for (DFSNode n: nodes)
            //to catch if there is more than one network of variables
            if (!n.visited)
                Visit(n);
    }

    //checks if edge between 2 nodes exists
    public Boolean EdgeExists(DFSNode a, DFSNode b){
        for (DFSEdge e: edges){
            if(((e.parent == a) && (e.child == b)) || ((e.parent == b) && e.child == a))
                return true;
        }
        return false;
    }

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
}
