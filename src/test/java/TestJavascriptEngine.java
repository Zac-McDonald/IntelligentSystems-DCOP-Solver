import dcopsolver.dcop.DCOP;
import dcopsolver.dcop.JavascriptEngine;
import dcopsolver.dcop.Variable;
import fileInput.YamlLoader;

import java.util.HashMap;

public class TestJavascriptEngine {
    public static void main (String[] args) {
        // Test validate function
        Boolean valid = JavascriptEngine.getInstance().validFloatExpression("10 * 2.5");
        Boolean invalid = JavascriptEngine.getInstance().validFloatExpression("2 * \"abc\"");

        // Test execute function
        Float result = JavascriptEngine.getInstance().evaluateFloatExpression("10 * 2");

        System.out.println(valid + ", " + invalid + ", " + result);

        // Test using DCOP
        try {
            YamlLoader loader = new YamlLoader();
            DCOP dcop = loader.loadYAML("./yaml/graph_coloring_basic.yaml");

            // Assign values
            HashMap<String, Integer> assignment = new HashMap<String, Integer>();
            for (Variable v : dcop.getVariables().values()) {
                assignment.put(v.getName(), v.getDomain().iterator().next());
            }

            System.out.println(dcop.prettyPrint());
            System.out.println(dcop.solutionCost(assignment));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
