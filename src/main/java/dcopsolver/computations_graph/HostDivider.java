package dcopsolver.computations_graph;

import java.util.ArrayList;
import java.util.List;

//decides how to split up variables among n hosts
// hostNum - number of required hosts
// aim - desired variables per host
// range - acceptable range or variables
// nodes - all nodes
// hostNodes - set of sets of variables

public class HostDivider {
    int hostNum;
    double aim;
    double range = 0.5;
    List<DFSNode> nodes;
    List<List<DFSNode>> hostNodes = new ArrayList<>();

    public HostDivider(List<DFSNode> v, int hostNumber){
        hostNum = hostNumber;
        nodes = v;
        aim = nodes.size() / (double) hostNumber;

        //reuses the visited field
        for (DFSNode n: nodes)
            n.visited = false;

        //while nodes not assigned to a host
        while(NodesLeft()) {

            //checks if theres only one host left
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

            //finds the next set of variables
            for (DFSNode n : nodes) {
                //searches for a node at bottom of tree
                if (!n.visited && IsBottomNode(n)) {
                    List<DFSNode> l = FindBranch(n);
                    //checks if list is within range
                    if (Math.abs(aim-l.size()) < range) {
                        for (DFSNode tempNode : l) {
                            tempNode.visited = true;
                        }
                        hostNodes.add(l);
                        updateAim();
                        //reset range (still hits the increase so will be 0.5 at next loop)
                        range = -0.5;
                    }
                }
            }
            //increases range if no list is found
            range ++;
        }
    }

    //changes aim depending on current variables and hosts left
    public void updateAim(){
        int variables = 0;
        for (DFSNode n: nodes){
            if (!n.visited){
                variables ++;
            }
        }
        if ((hostNum - hostNodes.size()) != 0) {
            double size = hostNodes.size();
            aim = variables / (hostNum - size);
        }
    }

    //checks if there are unassigned variables
    public Boolean NodesLeft(){
        for (DFSNode n: nodes) {
            if (!n.visited)
                return true;
        }
        return false;
    }

    //checks if node is at the bottom of tree
    public Boolean IsBottomNode(DFSNode n){
        for (DFSEdge e: n.adjacent){
            if (e.span && (e.parent == n) && (!e.child.visited))
                return false;
        }
        return true;
    }

    //finds potential list of variables for new host
    public List<DFSNode> FindBranch(DFSNode n){
        List<DFSNode> list = new ArrayList<>();
        List<DFSNode> tempList = new ArrayList<>();
        tempList.add(n);
        list.add(n);
        DFSNode topNode = n;

        //loops until a list is returned
        while(true){
            //if list has gone over aim size, returns a list
            if (tempList.size() > aim){
                //checks if the list before or after are closer to the aim
                double upperDif = tempList.size() - aim;
                double lowerDif = aim - list.size();

                if ((upperDif - lowerDif) >= 0)
                    return list;
                else
                    return tempList;
            }
            //add more nodes to the list
            else{
                //updates previous list to current list
                list.clear();

                list.addAll(tempList);

                //check if at top node
                boolean topTest = true;
                //adds nodes to current list
                for (DFSEdge e: topNode.adjacent){
                    //finds parent node of highest node in set
                    if((e.child == topNode) && e.span){
                        topNode = e.parent;
                        AddNodes(tempList, topNode);
                        topTest = false;
                    }
                }
                if (topTest)
                    return tempList;
            }
        }
    }

    //recursive function to find all child nodes that arent in list
    public void AddNodes(List<DFSNode> l, DFSNode n){
        l.add(n);
        //searches for child nodes
        for (DFSEdge e : n.adjacent) {
            if (e.span && (e.parent == n) && !e.child.visited && !l.contains(e.child)) {
                AddNodes(l, e.child);
            }
        }
    }

    //prints sets of nodes
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
