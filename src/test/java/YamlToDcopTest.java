import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

import java.util.HashMap;

public class YamlToDcopTest {
    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        //DCOP dcop = loader.loadYAML("./yaml/yamlToDcopTest.yaml");
        DCOP dcop = loader.loadYAML("./yaml/graph_coloring_basic.yaml");
        //dcop.print();
        System.out.println(dcop.prettyPrint());

        HashMap<String, Integer> assignment = new HashMap<String, Integer>();
        dcop.getVariables().forEach((k, v) -> {
            assignment.put(k, v.getInitialValue());
        });
        System.out.println("Default cost: " + dcop.solutionCost(assignment));
    }
}