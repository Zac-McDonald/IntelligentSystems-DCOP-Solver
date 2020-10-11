import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

public class DFSTreeTest {

    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadYAML("yamlToDcopTest.yaml");
        DFSTree tree = new DFSTree(dcop.variables, dcop.constraints);

    }
}
