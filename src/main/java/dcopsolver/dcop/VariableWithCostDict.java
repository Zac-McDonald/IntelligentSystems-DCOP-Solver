package dcopsolver.dcop;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;

public class VariableWithCostDict extends Variable {
    HashMap<Integer, Float> costs;      // Maps integer values (from the domain) to floating point costs
    Float defaultCost;

    public VariableWithCostDict (String name, Domain domain, Integer initialValue,
                                 HashMap<Integer, Float> costs, Float defaultCost) {
        super(name, domain, initialValue);
        this.costs = costs;
        this.defaultCost = defaultCost;

        // Check that all mapped values are in the domain
        for (Integer key : this.costs.keySet()) {
            if (!this.domain.contains(key)) {
                throw new NoSuchElementException("Mapped value (" + key + ") not found in domain");
            }
        }
    }

    public HashMap<Integer, Float> getCosts () {
        return costs;
    }

    public Float getDefaultCost () {
        return defaultCost;
    }

    @Override
    public Float evaluate (Integer value) {
        return costs.getOrDefault(value, defaultCost);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VariableWithCostDict that = (VariableWithCostDict) o;
        return Objects.equals(costs, that.costs) &&
                Objects.equals(defaultCost, that.defaultCost);
    }

    @Override
    public int hashCode () {
        return Objects.hash(super.hashCode(), costs, defaultCost);
    }

    @Override
    public String toString () {
        return "VariableWithCostDict{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                ", initialValue=" + initialValue +
                ", defaultCost=" + defaultCost +
                ", costs=" + costs +
                '}';
    }

    @Override
    public String prettyPrint () {
        return "VariableWithCostFnc{\n" +
                "\tname='" + name + "' (#" + hashCode() + "),\n" +
                "\tdomain=" + domain.name + " (#" + domain.hashCode() + "),\n" +
                "\tinitialValue=" + initialValue + ",\n" +
                "\tdefaultCost=" + defaultCost + ",\n" +
                "\tcosts=" + costs + "\n" +
                '}';
    }
}
