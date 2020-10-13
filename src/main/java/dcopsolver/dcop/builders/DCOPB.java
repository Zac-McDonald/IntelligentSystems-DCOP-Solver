package dcopsolver.dcop.builders;

import dcopsolver.dcop.*;

import java.util.HashMap;
import java.util.List;

//DCOP builder
public class DCOPB {
    String name;
    String description;
    Boolean objectiveIsMin;
    List<DomainB> domainsB;
    List<VariableB> variablesB;
    List<ConstraintB> constraintsB;

    public DCOPB() { }

    //converts builder to real DCOP object
    public DCOP build(String dcopDir) throws Exception {

        //creates domain hashMap
        HashMap<String, Domain> domains = new HashMap<>();
        for (DomainB domainB : domainsB) {
            domains.put(domainB.name, domainB.build(dcopDir));
        }

        //creates variables hashMap
        HashMap<String, Variable> variables = new HashMap<>();
        for (VariableB variableB : variablesB) {
            variables.put(variableB.name, variableB.build(dcopDir, domains));
        }

        //creates constraints hashMap
        HashMap<String, Constraint> constraints = new HashMap<>();
        for (ConstraintB constraintB : constraintsB) {
            constraints.put(constraintB.name, constraintB.build(dcopDir, variables));
        }

        //creates the DCOP object

        return new DCOP(name, description, objectiveIsMin, domains, variables, constraints);
    }

    public void setName(String name){
        this.name=name;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public void setObjectiveIsMin(Boolean objectiveIsMin) {
        this.objectiveIsMin = objectiveIsMin;
    }

    public void setDomains(List<DomainB> domains) {
        this.domainsB = domains;
    }

    public void setVariables(List<VariableB> variables) {
        this.variablesB = variables;
    }

    public void setConstraints(List<ConstraintB> constraints) {
        this.constraintsB = constraints;
    }

}
