package dcopsolver.dcop;

import java.util.HashMap;
import java.util.Objects;

public class DCOP {
    String name;
    String description;
    Boolean objectiveIsMin;
    HashMap<String, Domain> domains;
    HashMap<String, Variable> variables;
    HashMap<String, Constraint> constraints;

    public DCOP (String name, String description, Boolean objectiveIsMin) {
        this.name = name;
        this.description = description;
        this.objectiveIsMin = objectiveIsMin;

        this.domains = new HashMap<String, Domain>();
        this.variables = new HashMap<String, Variable>();
        this.constraints = new HashMap<String, Constraint>();
    }

    public DCOP (String name, String description, Boolean objectiveIsMin, HashMap<String, Domain> domains,
                 HashMap<String, Variable> variables, HashMap<String, Constraint> constraints)
    {
        this.name = name;
        this.description = description;
        this.objectiveIsMin = objectiveIsMin;

        this.domains = domains;
        this.variables = variables;
        this.constraints = constraints;
    }

    public void addVariable (Variable variable) {
        if (variables.containsKey(variable.name))
        {
            // Catch not-equal but same name variable addition
            if (!variables.get(variable.name).equals(variable))
            {
                throw new IllegalArgumentException("A different variable already exists with name: " + variable.name);
            }
        } else {
            variables.put(variable.name, variable);
        }
    }

    public void addConstraint (Constraint constraint) {
        //
    }

    public float solutionCost () {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DCOP dcop = (DCOP) o;
        return Objects.equals(name, dcop.name) &&
                Objects.equals(description, dcop.description) &&
                Objects.equals(objectiveIsMin, dcop.objectiveIsMin) &&
                Objects.equals(domains, dcop.domains) &&
                Objects.equals(variables, dcop.variables) &&
                Objects.equals(constraints, dcop.constraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, objectiveIsMin, domains, variables, constraints);
    }

    @Override
    public String toString() {
        return "DCOP{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", objectiveIsMin=" + objectiveIsMin +
                ", domains=" + domains +
                ", variables=" + variables +
                ", constraints=" + constraints +
                '}';
    }
}
