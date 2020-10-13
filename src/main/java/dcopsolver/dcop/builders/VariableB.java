package dcopsolver.dcop.builders;

import dcopsolver.dcop.Domain;
import dcopsolver.dcop.Variable;
import dcopsolver.dcop.VariableWithCostFnc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

//Variable builder
public class VariableB {
    String name;
    String domain;
    Integer initialValue;
    String source;
    String expression;

    public VariableB() { }

    //creates variable object
    public Variable build(String dcopDir, HashMap<String, Domain> domains) throws Exception {

        //fetches domain by name
        Domain domain = domains.get(this.domain);

        //checks if domain was found and throws exception if not
        if (domain == null) {
            throw new Exception("Domain not found: " + this.domain);
        }

        // Catch no initial value - default to first value in domain
        if (initialValue == null) {
            initialValue = domain.iterator().next();
        }

        // TODO: Should we add a banned variable name list -- banning Javascript reserved words (i.e. if, function)

        if (expression != null && !expression.isEmpty()) {
            // Check for sources
            if (source == null) {
                source = "";
            } else {
                // Parse source file as a string
                try {
                    StringBuilder sb = new StringBuilder();
                    Files.readAllLines(Paths.get(dcopDir, source)).forEach(sb::append);
                    source = sb.toString();
                } catch (IOException e) {
                    throw new FileNotFoundException("Could not find source file: " + source);
                }
            }

            return new VariableWithCostFnc(name, domain, initialValue, expression, source);
        } else { // TODO: Variable with Dict
            //creates variable
            return new Variable(name, domain, initialValue);
        }
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

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
