package dcopsolver.dcop;

import java.util.HashSet;
import java.util.Objects;

public class VariableWithCostFnc extends Variable {
    // TODO: Expression validation
    String expression;      // Maps the variable values (integer) to a floating point cost

    public VariableWithCostFnc (String name, Domain domain, Integer initialValue, String expression) {
        super(name, domain, initialValue);
        this.expression = expression;
    }

    @Override
    public Float evaluate (Integer value) {
        // TODO: Call J2V8 Service
        return 0f;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VariableWithCostFnc that = (VariableWithCostFnc) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode () {
        return Objects.hash(super.hashCode(), expression);
    }

    @Override
    public String toString () {
        return "VariableWithCostFnc{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                ", initialValue=" + initialValue +
                ", expression='" + expression + '\'' +
                '}';
    }

    @Override
    public String prettyPrint () {
        return "VariableWithCostFnc{\n" +
                "\tname='" + name + "' (#" + hashCode() + "),\n" +
                "\tdomain=" + domain.name + " (#" + domain.hashCode() + "),\n" +
                "\tinitialValue=" + initialValue + ",\n" +
                "\texpression='" + expression + "'\n" +
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
        VariableWithCostFnc fncVar1 = new VariableWithCostFnc("Var", d, 0, "value * 2");
        VariableWithCostFnc fncVar2 = new VariableWithCostFnc("Var", d, 0, "value * 2");

        System.out.println("reg == fnc -> " + regularVar.equals(fncVar1));
        System.out.println("fnc == reg -> " + fncVar1.equals(regularVar));
        System.out.println("fnc == fnc -> " + fncVar1.equals(fncVar2));
    }
}
