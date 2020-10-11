package dcopsolver.dcop.builders;

import dcopsolver.dcop.Domain;
import dcopsolver.dcop.Variable;

import java.util.HashMap;

//Variable builder
public class VariableB {
    String name;
    String domain;
    Integer initialValue;

    public VariableB() { }

    //creates variable object
    public Variable build(HashMap<String, Domain> domains) throws Exception {

        //fetches domain by name
        Domain domain = domains.get(this.domain);

        //checks if domain was found and throws exception if not
        if(domain == null)
            throw new Exception("Domain not found: " + this.domain);

        //creates variable
        return new Variable(name, domain, initialValue);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setInitialValue(Integer initialValue) {
        this.initialValue = initialValue;
    }
}
