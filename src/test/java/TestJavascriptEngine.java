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
            DCOP dcop = loader.loadDCOP("./yaml/graph_coloring_basic.yaml");

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

        // Stress test J2V8
        try {
            int n = 1000;
            System.out.println("Press enter to begin stress test (n=" + n + "): ");
            System.in.read();

            System.out.println("Starting");
            long startTime = System.nanoTime();
            for (float x = 1; n > 0; n--) {
                String fnc = "x=" + x + ";Math.sin(x)*2";
                x += JavascriptEngine.getInstance().evaluateFloatExpression(fnc);
                //System.out.println("\t" + n + ": " + x);
            }
            long endTime = System.nanoTime();
            System.out.println("Finished in " + ((endTime - startTime) / 1000000) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
