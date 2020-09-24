package dcopsolver.dcop;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Constraint {
    String name;
    ArrayList<Variable> variables;

    public ArrayList<String> variable_names () {
        return variables.stream()
                .map(variable -> variable.name)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int arity () {
        return variables.size();
    }

    public ArrayList<Integer> shape () {
        return variables.stream()
                .map(variable -> variable.domain.size())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // TODO: Slice function

    public abstract Float evaluate ();

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constraint that = (Constraint) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(variables, that.variables);
    }

    @Override
    public int hashCode () {
        return Objects.hash(name, variables);
    }

    @Override
    public String toString () {
        return "Constraint{" +
                "name='" + name + '\'' +
                ", variables=" + variables +
                '}';
    }
}
