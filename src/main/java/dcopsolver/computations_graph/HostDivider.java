package dcopsolver.computations_graph;

import org.apache.xpath.operations.Bool;

import java.util.ArrayList;
import java.util.List;

public class HostDivider {
    int hostNum;
    double aim;
    int range = 1;
    List<DFSNode> nodes;
    List<List<DFSNode>> hostNodes = new ArrayList<>();

    public HostDivider(List<DFSNode> v, int hostNumber){
        hostNum = hostNumber;
        nodes = v;
        aim = nodes.size() / hostNumber;

        for (DFSNode n: nodes)
            n.visited = false;

        while(NodesLeft()) {
            
            if (hostNodes.size() == (hostNumber - 1)) {
                List<DFSNode> l = new ArrayList<>();
                for (DFSNode n : nodes) {
                    if (!n.visited) {
                        n.visited = true;
                        l.add(n);
                    }
                }
                hostNodes.add(l);
            }
            
            for (DFSNode n : nodes) {
                if (!n.visited && IsBottomNode(n)) {
                    List<DFSNode> l = FindBranch(n);
                    if (Math.abs(aim-l.size()) < range) {
                        for (DFSNode tempNode : l) {
                            tempNode.visited = true;
                        }
                        hostNodes.add(l);
                        updateAim();
                        range = 1;
                    }
                    else{
                        for (DFSNode tempNode : l) {
                            tempNode.visited = false;
                        }
                    }
                }
            }
            range ++;
        }
    }

    public void updateAim(){
        int variables = 0;
        for (DFSNode n: nodes){
            if (!n.visited){
                variables ++;
            }
        }
        if ((hostNum - hostNodes.size()) != 0)
            aim = variables / (hostNum - hostNodes.size());
    }

    public Boolean NodesLeft(){
        for (DFSNode n: nodes) {
            if (!n.visited)
                return true;
        }
        return false;
    }

    public Boolean IsBottomNode(DFSNode n){
        for (DFSEdge e: n.adjacent){
            if (e.span && (e.parent == n) && (!e.child.visited))
                return false;
        }
        return true;
    }

    public List<DFSNode> FindBranch(DFSNode n){
        List<DFSNode> list;
        List<DFSNode> tempList = new ArrayList<>();
        tempList.add(n);
        list = tempList;
        n.visited = true;
        DFSNode topNode = n;

        while(true){
            if (!NodesLeft())
                return tempList;
            else if (tempList.size() >= aim){
                double upperDif = tempList.size() - aim;
                double lowerDif = aim - list.size();

                if ((upperDif - lowerDif) > 0)
                    return list;
                else
                    return tempList;
            }
            else{
                list = tempList;

                for (DFSEdge e: topNode.adjacent){
                    if((e.child == topNode) && e.span){
                        topNode = e.parent;
                        tempList = AddNodes(list, topNode);
                    }
                }
            }
        }
    }

    public List<DFSNode> AddNodes(List<DFSNode> l, DFSNode n){
        List<DFSNode> list = l;
        n.visited = true;
        list.add(n);
        for (DFSEdge e : n.adjacent) {
            if (e.span && (e.parent == n) && !e.child.visited) {
                list = AddNodes(list, e.child);
            }
        }
        return list;
    }

    public void Print(){
        for (List<DFSNode> list: hostNodes){
            System.out.print("Host: ");
            for (DFSNode n: list){
                System.out.print(n.name + ", ");
            }
            System.out.println();
        }
    }
}
