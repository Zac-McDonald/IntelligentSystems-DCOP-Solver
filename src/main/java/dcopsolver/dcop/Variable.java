package dcopsolver.dcop;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Variable {
    String name;
    Domain domain;
    Integer initialValue;   // Index of initial value in domain

    public Variable (String name, Domain domain, Integer initialValue) {
        this.name = name;
        this.domain = domain;
        this.initialValue = domain.indexOf(initialValue);

        // Check initial value is in domain
        if (this.initialValue == -1) {
            throw new NoSuchElementException("Initial value not found in domain");
        }
    }

    public Variable (String name, Domain domain, String initialValue) {
        this.name = name;
        this.domain = domain;
        this.initialValue = domain.indexOf(initialValue);

        // Check initial value is in domain
        if (this.initialValue == -1) {
            throw new NoSuchElementException("Initial value not found in domain");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name) &&
                Objects.equals(domain, variable.domain) &&
                Objects.equals(initialValue, variable.initialValue);
        // TODO: Should initialValue contribute to equality?
        // Should we instead merge (add initialValue) variables on name conflict
        // initialValue contributes to equality to be more-specific
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, domain, initialValue);
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                ", initialValue=" + initialValue +
                '}';
    }
}
