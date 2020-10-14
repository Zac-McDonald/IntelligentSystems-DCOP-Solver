package dcopsolver.dcop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Constraint {
    String name;
    ArrayList<Variable> variables;

    public Constraint () {
        // JavaBeans compliance
    }

    public ArrayList<String> variable_names () {
        return variables.stream()
                .map(variable -> variable.name)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getName () {
        return name;
    }

    public ArrayList<Variable> getVariables () {
        return variables;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setVariables (ArrayList<Variable> variables) {
        this.variables = variables;
    }

    public int arity () {
        return variables.size();
    }

    public ArrayList<Integer> shape () {
        return variables.stream()
                .map(variable -> variable.domain.size())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Uses partial assignment
    public abstract Constraint slice (HashMap<String, Integer> variableAssignments);

    // Needs complete assignment
    public abstract Float evaluate (HashMap<String, Integer> variableAssignments);

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

    public String prettyPrint () {
        StringBuilder pretty = new StringBuilder(
                "Constraint{\n" +
                "\tname='" + name + "' (#" + hashCode() + "),\n"
        );

        pretty.append("\tvariables=[\n");
        for (Variable v : variables)
        {
            pretty.append("\t\t").append(v.name).append(" (#").append(v.hashCode()).append(")\n");
        }
        pretty.append("\t]\n}");

        return pretty.toString();
    }
}
