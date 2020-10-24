import GUI.GUI;
import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

public class GuiTest {
    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadYAML("./yaml/DFSTreeTest.yaml");
        //DCOP dcop = loader.loadYAML("./yaml/graph_coloring_10vars.yaml");
        DFSTree tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), 4);

        GUI gui = new GUI(tree);
    }
}
