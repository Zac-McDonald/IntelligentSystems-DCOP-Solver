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
    // TODO: Consider adding a metainfo HashMap or similar
    //       Example usage: Displaying String variable that has been enumerated

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
            // Throw error if we already have a different variable with the same name.
            if (!variables.get(variable.name).equals(variable))
            {
                throw new IllegalArgumentException("A different variable already exists with name: " + variable.name);
            }
        } else {
            variables.put(variable.name, variable);
            addDomain(variable.domain);
        }
    }

    public void addDomain (Domain domain) {
        if (domains.containsKey(domain.name))
        {
            // Throw error if we already have a different domain with the same name.
            if (!domains.get(domain.name).equals(domain))
            {
                throw new IllegalArgumentException("A different domain already exists with name: " + domain.name);
            }
        } else {
            domains.put(domain.name, domain);
        }
    }

    public void addConstraint (Constraint constraint) {
        if (constraints.containsKey(constraint.name))
        {
            // Throw error if we already have a different constraint with the same name.
            if (!constraints.get(constraint.name).equals(constraint))
            {
                throw new IllegalArgumentException("A different constraint already exists with name: " + constraint.name);
            }
        } else {
            constraints.put(constraint.name, constraint);
            for (Variable v : constraint.variables) {
                addVariable(v);
            }
        }
    }

    public Float solutionCost () {
        // TODO: Relies on a partial solution representation
        return 0f;
    }

    @Override
    public boolean equals (Object o) {
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
    public int hashCode () {
        return Objects.hash(name, description, objectiveIsMin, domains, variables, constraints);
    }

    @Override
    public String toString () {
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
