import GUI.GUI;
import dcopsolver.computations_graph.DFSNode;
import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.DCOP;
import dcopsolver.dcop.JavascriptEngine;
import fileInput.YamlLoader;

import java.util.concurrent.TimeUnit;

public class GuiTest {
    public static void main(String[] args) throws Exception {
        JavascriptEngine.setupEngine("./temp/j2v8/guitest");

        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadDCOP("./yaml/DFSTreeTest.yaml");
        //DCOP dcop = loader.loadYAML("./yaml/graph_coloring_10vars.yaml");
        DFSTree tree = new DFSTree(dcop.getVariables(), dcop.getConstraints(), 4);

        //Thread guiThread = new Thread(new GUI(tree));
        //guiThread.start();

        int iter = 0;

        while(true){

            for (DFSNode n: tree.getNodes()){
                n.getVar().setInitialValue(iter);
            }
            iter ++;
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
