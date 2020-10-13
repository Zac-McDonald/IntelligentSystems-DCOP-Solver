package dcopsolver.computations_graph;

import dcopsolver.dcop.Variable;

import java.util.ArrayList;
import java.util.List;

public class DFSNode {
    String name;
    Variable var;
    Boolean visited;
    List<DFSEdge> adjacent = new ArrayList<>();

    public DFSNode (Variable v){
        this.var = v;
        name = v.name;
        visited = false;
    }

    public DFSNode GetParent(){
        for (DFSEdge e: this.adjacent){
            //finds parent node of highest node in set
            if((e.child == this) && e.span){
                return e.parent;
            }
        }
        return null;
    }

    public List<DFSNode> GetChildren(boolean withPseudo){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if ((e.span || withPseudo) && (e.parent == this)) {
                list.add(e.child);
            }
        }
        return list;
    }

    public List<DFSNode> GetParents(boolean withPseudo){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if ((e.span || withPseudo) && (e.child == this)) {
                list.add(e.parent);
            }
        }
        return list;
    }

    public List<DFSNode> GetNeighbours(boolean withPseudo){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if (e.span || withPseudo) {
                list.add(e.EndNode(this));
            }
        }
        return list;
    }

    public boolean IsNeighboursWith(DFSNode neighbour){
        for (DFSEdge e: adjacent){
            if(e.EndNode(this).equals(neighbour))
                return true;
        }
        return false;
    }

    public boolean IsBottomNode(){
        for (DFSEdge e: adjacent){
            if (e.span && (e.parent.equals(this)) && (!e.child.visited))
                return false;
        }
        return true;
    }
}
