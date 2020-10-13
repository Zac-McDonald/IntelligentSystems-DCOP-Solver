package dcopsolver.dcop;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DCOP {
    String name;
    String description;
    Boolean objectiveIsMin;
    HashMap<String, Domain> domains;


    HashMap<String, Variable> variables;
    HashMap<String, Constraint> constraints;
    // TODO: External Javascript sources + how to use them -- here or per function
    //HashMap<String, String> sources;
    // TODO: Consider adding a metainfo HashMap or similar
    //       Example usage: Displaying String variable that has been enumerated

    public DCOP (String name, String description, Boolean objectiveIsMin) {
        this.name = name;
        this.description = description;
        this.objectiveIsMin = objectiveIsMin;

        this.domains = new HashMap<>();
        this.variables = new HashMap<>();
        this.constraints = new HashMap<>();
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

    public String getName () {
        return name;
    }

    public String getDescription () {
        return description;
    }

    public Boolean getObjectiveIsMin () {
        return objectiveIsMin;
    }

    public HashMap<String, Domain> getDomains () {
        return domains;
    }

    public HashMap<String, Variable> getVariables () {
        return variables;
    }

    public HashMap<String, Constraint> getConstraints () {
        return constraints;
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

    public Float solutionCost (HashMap<String, Integer> variableAssignments) {
        // Requires a complete assignment
        if (!variableAssignments.keySet().equals(variables.keySet()))
        {
            throw new IllegalArgumentException("Cannot calculate a solution from an incomplete assignment");
        }
        Float total = 0f;

        for (String vName : variableAssignments.keySet())
        {
            // Add variable costs
            total += variables.get(vName).evaluate(variableAssignments.get(vName));
        }

        for (Constraint c : constraints.values())
        {
            // Add constraint costs
            total += c.evaluate(variableAssignments);
        }

        return total;
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

    public String prettyPrint () {
        StringBuilder pretty = new StringBuilder(
                "DCOP{\n" +
                "\tname='" + name + "' (#" + hashCode() + "),\n" +
                "\tdescription='" + description + "',\n" +
                "\tobjectiveIsMin=" + objectiveIsMin + ",\n"
        );

        pretty.append("\tdomains=[");
        for (Domain d : domains.values())
        {
            pretty.append("\n\t\t").append(d.prettyPrint().replace("\n", "\n\t\t"));
        }
        pretty.append("\n\t],\n");

        pretty.append("\tvariables=[");
        for (Variable v : variables.values())
        {
            pretty.append("\n\t\t").append(v.prettyPrint().replace("\n", "\n\t\t"));
        }
        pretty.append("\n\t],\n");

        pretty.append("\tconstraints=[");
        for (Constraint c : constraints.values())
        {
            pretty.append("\n\t\t").append(c.prettyPrint().replace("\n", "\n\t\t"));
        }
        pretty.append("\n\t]\n}");

        return pretty.toString();
    }

    //prints all dcop information to terminal in easy to read format
    public void print () {

        System.out.println(
            "DCOP \n\n" +
            "Name= " + name + "\n\n" +
            "Description= " + description + "\n\n" +
            "ObjectiveIsMin= " + objectiveIsMin + "\n\n" +
            "Domains= \n"
        );

        //prints domains
        for (Map.Entry<String, Domain> entry: domains.entrySet()) {
            Domain d = entry.getValue();
            System.out.println(
                "\tName= " + d.name + "\n" +
                "\tType= " + d.semanticType + "\n" +
                "\tValues= " + d.values + "\n"
            );
        }

        //prints variables
        System.out.println("Variables= \n");
        for (Map.Entry<String, Variable> entry: variables.entrySet()) {
            Variable v = entry.getValue();
            System.out.println(
                "\tName= " + v.name + "\n" +
                "\tDomain= " + v.domain.name + "\n" +
                "\tInitial Value= " + v.initialValue + "\n"
            );
        }

        //prints constraints
        System.out.println("Constraints= \n");
        for (Map.Entry<String, Constraint> entry: constraints.entrySet()) {
            Constraint c = entry.getValue();
            System.out.println(
                "\tName= " + c.name + "\n" +
                "\tVariables= "
            );

            for (Variable v: c.variables){
                System.out.println("\t\t" + v.name);
            }
            System.out.println();
        }
    }
}
