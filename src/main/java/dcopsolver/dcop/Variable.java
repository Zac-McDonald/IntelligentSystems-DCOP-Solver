package dcopsolver.dcop;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Variable {
    String name;
    Domain domain;
    Integer initialValue;

    public Variable (String name, Domain domain, Integer initialValue) {
        this.name = name;
        this.domain = domain;
        this.initialValue = initialValue;

        // Check initial value is in domain
        if (!this.domain.contains(this.initialValue)) {
            throw new NoSuchElementException("Initial value not found in domain");
        }
    }

    public String getName () {
        return name;
    }

    public Domain getDomain () {
        return domain;
    }

    public Integer getInitialValue () {
        return initialValue;
    }

    public Float evaluate (Integer value) {
        return 0f;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name) &&
                Objects.equals(domain, variable.domain) &&
                Objects.equals(initialValue, variable.initialValue);
    }

    @Override
    public int hashCode () {
        return Objects.hash(name, domain, initialValue);
    }

    @Override
    public String toString () {
        return "Variable{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                ", initialValue=" + initialValue +
                '}';
    }

    public String prettyPrint () {
        return "Variable{\n" +
                "\tname='" + name + "' (#" + hashCode() + "),\n" +
                "\tdomain=" + domain.name + " (#" + domain.hashCode() + "),\n" +
                "\tinitialValue=" + initialValue + "\n" +
                '}';
    }
}
