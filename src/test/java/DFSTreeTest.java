import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

public class DFSTreeTest {

    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        //DCOP dcop = loader.loadDCOP("./yaml/graph_coloring_basic.yaml");
        DCOP dcop = loader.loadDCOP("./yaml/graph_coloring_10vars.yaml");
        DFSTree tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), 3);

        tree.OutputGraph();
        tree.PrintHosts();
    }
}
