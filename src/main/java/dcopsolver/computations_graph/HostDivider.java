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

    public HostDivider () {
        // JavaBeans compliance
    }

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
                if (!n.visited && n.IsBottomNode()) {
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

    public int getHostNum () {
        return hostNum;
    }

    public void setHostNum (int hostNum) {
        this.hostNum = hostNum;
    }

    public double getAim () {
        return aim;
    }

    public void setAim (double aim) {
        this.aim = aim;
    }

    public double getRange () {
        return range;
    }

    public void setRange (double range) {
        this.range = range;
    }

    public List<DFSNode> getNodes () {
        return nodes;
    }

    public void setNodes (List<DFSNode> nodes) {
        this.nodes = nodes;
    }

    public List<List<DFSNode>> getHostNodes () {
        return hostNodes;
    }

    public void setHostNodes (List<List<DFSNode>> hostNodes) {
        this.hostNodes = hostNodes;
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

                //move up tree one node
                topNode = topNode.GetParents(false).get(0);

                if (topNode == null)
                    //current node has no parents
                    return tempList;
                else
                    //add nodes below new top node
                    AddNodes(tempList, topNode);
            }
        }
    }

    //recursive function to find all child nodes that arent in list
    public void AddNodes(List<DFSNode> l, DFSNode n){
        l.add(n);
        //searches for child nodes

        for (DFSNode child: n.GetChildren(false)){
            if (!l.contains(child) && !child.visited)
                AddNodes(l, child);
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
