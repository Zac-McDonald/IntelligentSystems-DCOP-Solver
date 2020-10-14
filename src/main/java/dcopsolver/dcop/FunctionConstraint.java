package dcopsolver.dcop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class FunctionConstraint extends Constraint {
    String expression;
    String source;

    public FunctionConstraint () {
        // JavaBeans compliance
    }

    public FunctionConstraint (String name, ArrayList<Variable> variables, String expression, String source) {
        this.name = name;
        this.variables = variables;
        this.expression = expression;
        this.source = source;

        // Validate expression
        HashMap<String, Integer> variableAssignments = new HashMap<String, Integer>();
        for (Variable v : variables) {
            variableAssignments.put(v.getName(), v.getDomain().iterator().next());
        }
        String assign = JavascriptEngine.getAssignment(variableAssignments);

        if (!JavascriptEngine.getInstance().validFloatExpression(assign + expression, source)) {
            throw new IllegalArgumentException("Expression \"" + expression + "\" does not return float.");
        }
    }

    public String getExpression () {
        return expression;
    }

    public String getSource () {
        return source;
    }

    public void setExpression (String expression) {
        this.expression = expression;
    }

    public void setSource (String source) {
        this.source = source;
    }

    @Override
    public Constraint slice (HashMap<String, Integer> variableAssignments) {
        // Check that assignment only contains associated variables
        if (!variable_names().containsAll(variableAssignments.keySet())) {
            // Warning: Unassociated variables present in slice
            // TODO: Should we throw an error here?
        }

        // Get remaining variables
        ArrayList<Variable> remainders = variables;
        remainders.removeIf(v -> (variableAssignments.containsKey(v.name)));

        // Replace used variables with their values
        String assign = JavascriptEngine.getAssignment(variableAssignments);;

        // Return new FunctionConstraint
        return new FunctionConstraint(name, remainders, assign + expression, source);
    }

    @Override
    public Float evaluate (HashMap<String, Integer> variableAssignments) {
        // Evaluate expression using J2V8
        String assign = JavascriptEngine.getAssignment(variableAssignments);
        return JavascriptEngine.getInstance().evaluateFloatExpression(assign + expression, source);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FunctionConstraint that = (FunctionConstraint) o;
        return Objects.equals(expression, that.expression) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode () {
        return Objects.hash(super.hashCode(), expression, source);
    }

    @Override
    public String toString () {
        return "FunctionConstraint{" +
                "name='" + name + '\'' +
                ", variables=" + variables +
                ", expression='" + expression + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    @Override
    public String prettyPrint () {
        StringBuilder pretty = new StringBuilder(
                "FunctionConstraint{\n" +
                "\tname='" + name + "' (#" + hashCode() + "),\n"
        );

        pretty.append("\tvariables=[\n");
        for (Variable v : variables)
        {
            pretty.append("\t\t").append(v.name).append(" (#").append(v.hashCode()).append(")\n");
        }
        pretty.append("\t],\n");
        pretty.append("\texpression='").append(expression).append("'\n");
        pretty.append("\tsource='").append(source).append("'\n");
        pretty.append("}");

        return pretty.toString();
    }

    public static void main (String[] args) {
        HashSet<Integer> dValues = new HashSet<Integer>();
        dValues.add(0); dValues.add(1); dValues.add(2); dValues.add(3);
        Domain d = new Domain("Test domain", "Integers", dValues);

        ArrayList<Variable> vars = new ArrayList<Variable>();
        vars.add(new Variable("v0", d, 0));
        vars.add(new Variable("v1", d, 0));
        vars.add(new Variable("v2", d, 0));

        // Print function
        FunctionConstraint fc = new FunctionConstraint("Test FC", vars, "v0 * v1 + v2", "");
        System.out.println(fc.prettyPrint());

        // Prepare slice
        HashMap<String, Integer> assignments = new HashMap<String, Integer>();
        assignments.put("v0", 2);

        // Print sliced function
        System.out.println(fc.slice(assignments).prettyPrint());
    }
}
