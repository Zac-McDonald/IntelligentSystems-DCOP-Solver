import dcopsolver.computations_graph.DFSEdge;
import dcopsolver.computations_graph.DFSNode;
import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;
import graphviz.Graph;

public class DFSTreeTest {

    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadYAML("DFSTreeTest.yaml");
        DFSTree tree = new DFSTree(dcop.variables, dcop.constraints);

        tree.OutputGraph();
    }
}
