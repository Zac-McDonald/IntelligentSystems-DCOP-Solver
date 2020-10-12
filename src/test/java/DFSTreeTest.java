import dcopsolver.computations_graph.DFSTree;
import dcopsolver.computations_graph.HostDivider;
import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

public class DFSTreeTest {

    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadYAML("DFSTreeTest.yaml");
        DFSTree tree = new DFSTree(dcop.variables, dcop.constraints);

        tree.OutputGraph();
        HostDivider h = new HostDivider(tree.nodes, 5);
        h.Print();
    }
}
