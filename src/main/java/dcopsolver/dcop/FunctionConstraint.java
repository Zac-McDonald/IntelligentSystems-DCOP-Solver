package dcopsolver.dcop;

import java.util.ArrayList;
import java.util.Objects;

public class FunctionConstraint extends Constraint {
    String expression;

    public FunctionConstraint (String name, ArrayList<Variable> variables, String expression) {
        this.name = name;
        this.variables = variables;
        this.expression = expression;
    }

    @Override
    public Float evaluate () {
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
                "expression='" + expression + '\'' +
                ", name='" + name + '\'' +
                ", variables=" + variables +
                '}';
    }
}
