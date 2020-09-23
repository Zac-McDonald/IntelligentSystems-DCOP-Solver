package dcopsolver.dcop;

import java.util.ArrayList;

public class Constraint {
    String name;
    ArrayList<Variable> variables;

    public Constraint (String name, Variable[] variables) {
        //
    }

    float evaluate () {
        // Runs J2V8 -- should this be in constraint???
        return 0;
    }
}
