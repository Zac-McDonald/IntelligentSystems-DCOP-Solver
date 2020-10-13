package dcopsolver.dcop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class VariableWithCostFnc extends Variable {
    String expression;      // Maps the variable values (integer) to a floating point cost
    String source;

    public VariableWithCostFnc (String name, Domain domain, Integer initialValue, String expression, String source) {
        super(name, domain, initialValue);
        this.expression = expression;
        this.source = source;

        // Validate expression
        if (!JavascriptEngine.getInstance().validFloatExpression(name + "=" + initialValue + ";" + expression, source)) {
            throw new IllegalArgumentException("Expression \"" + expression + "\" does not return float.");
        }
    }

    public String getExpression () {
        return expression;
    }

    public String getSource () {
        return source;
    }

    @Override
    public Float evaluate (Integer value) {
        // Evaluate expression using J2V8
        return JavascriptEngine.getInstance().evaluateFloatExpression(name + "=" + value + ";" + expression, source);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VariableWithCostFnc that = (VariableWithCostFnc) o;
        return Objects.equals(expression, that.expression) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode () {
        return Objects.hash(super.hashCode(), expression, source);
    }

    @Override
    public String toString () {
        return "VariableWithCostFnc{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                ", initialValue=" + initialValue +
                ", expression='" + expression + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    @Override
    public String prettyPrint () {
        return "VariableWithCostFnc{\n" +
                "\tname='" + name + "' (#" + hashCode() + "),\n" +
                "\tdomain=" + domain.name + " (#" + domain.hashCode() + "),\n" +
                "\tinitialValue=" + initialValue + ",\n" +
                "\texpression='" + expression + "'\n" +
                "\tsource='" + source + "'\n" +
                '}';
    }

    // Checking that equality works as expected
    // TODO: Consider adding JUnit
    public static void main (String[] args)
    {
        HashSet<Integer> dValues = new HashSet<Integer>();
        dValues.add(0); dValues.add(1); dValues.add(2); dValues.add(3);
        Domain d = new Domain("Test domain", "Integers", dValues);

        Variable regularVar = new Variable("Var", d, 0);
        VariableWithCostFnc fncVar1 = new VariableWithCostFnc("Var", d, 0, "value * 2", "");
        VariableWithCostFnc fncVar2 = new VariableWithCostFnc("Var", d, 0, "value * 2", "");

        System.out.println("reg == fnc -> " + regularVar.equals(fncVar1));
        System.out.println("fnc == reg -> " + fncVar1.equals(regularVar));
        System.out.println("fnc == fnc -> " + fncVar1.equals(fncVar2));
    }
}
