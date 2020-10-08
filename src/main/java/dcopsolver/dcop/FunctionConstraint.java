package dcopsolver.dcop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FunctionConstraint extends Constraint {
    // TODO: Expression validation
    //       Regex for variables/functions is: ^[\p{L}\p{Nl}$_][\p{L}\p{Nl}$\p{Mn}\p{Mc}\p{Nd}\p{Pc}]*$
    //       Uses Unicode Properties (supported as of JDK7)
    String expression;

    public FunctionConstraint (String name, ArrayList<Variable> variables, String expression) {
        this.name = name;
        this.variables = variables;
        this.expression = expression;
    }

    @Override
    public Constraint slice (HashMap<String, Integer> variableAssignments) {
        // TODO: Slice function
        // Check that assignment only contains associated variables

        // Get remaining variables

        // Replace used variables with their values

        // Return new FunctionConstraint
        return null;
    }

    @Override
    public Float evaluate (HashMap<String, Integer> variableAssignments) {
        // TODO: Runs J2V8 -- Will need to call to a J2V8 engine (ideally as a JadeX service)
        return 0f;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FunctionConstraint that = (FunctionConstraint) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode () {
        return Objects.hash(super.hashCode(), expression);
    }

    @Override
    public String toString () {
        return "FunctionConstraint{" +
                "name='" + name + '\'' +
                ", variables=" + variables +
                ", expression='" + expression + '\'' +
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
        pretty.append("}");

        return pretty.toString();
    }
}
