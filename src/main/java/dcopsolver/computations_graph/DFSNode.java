package dcopsolver.computations_graph;

import dcopsolver.dcop.Variable;

import java.util.ArrayList;
import java.util.List;

public class DFSNode {
    String name;
    Variable var;
    Boolean visited;
    List<DFSEdge> adjacent = new ArrayList<>();

    public DFSNode () {
        // JavaBeans compliance
    }

    public DFSNode (Variable v){
        this.var = v;
        name = v.name;
        visited = false;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Variable getVar () {
        return var;
    }

    public void setVar (Variable var) {
        this.var = var;
    }

    public Boolean getVisited () {
        return visited;
    }

    public void setVisited (Boolean visited) {
        this.visited = visited;
    }

    public List<DFSEdge> getAdjacent () {
        return adjacent;
    }

    public void setAdjacent (List<DFSEdge> adjacent) {
        this.adjacent = adjacent;
    }

    public List<DFSNode> GetChildren(boolean Pseudo){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if ((e.span ^ Pseudo) && (e.parent == this)) {
                list.add(e.child);
            }
        }
        return list;
    }

    public List<DFSNode> GetAllChildren(){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if (e.parent == this) {
                list.add(e.child);
            }
        }
        return list;
    }

    public List<DFSNode> GetParents(boolean Pseudo){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if ((e.span ^ Pseudo) && (e.child == this)) {
                list.add(e.parent);
            }
        }
        return list;
    }

    public List<DFSNode> GetAllParents(){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if (e.child == this) {
                list.add(e.parent);
            }
        }
        return list;
    }

    public List<DFSNode> GetNeighbours(boolean Pseudo){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            if (e.span ^ Pseudo) {
                list.add(e.EndNode(this));
            }
        }
        return list;
    }

    public List<DFSNode> GetAllNeighbours(){
        List<DFSNode> list = new ArrayList<>();
        for (DFSEdge e : adjacent) {
            list.add(e.EndNode(this));
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
