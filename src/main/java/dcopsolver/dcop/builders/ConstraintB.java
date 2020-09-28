package dcopsolver.dcop.builders;

import dcopsolver.dcop.Constraint;
import dcopsolver.dcop.FunctionConstraint;
import dcopsolver.dcop.Variable;

import java.util.ArrayList;
import java.util.HashMap;

//constraint builder
public class ConstraintB {
    String name;
    String type;
    String expression;

    public ConstraintB(){}

    //converts builder to actual constraint
    public Constraint build(HashMap<String, Variable> v) throws Exception {
        Constraint c = null;

        //for functional constraint
        if(this.type.equals("functional")) {
            //TODO: search for needed variables
            //gives all variables to list
            ArrayList<Variable> variables = new ArrayList<>(v.values());

            c = new FunctionConstraint(this.name, variables, this.expression);
        }

        //if constraint can't be found, throw exception
        if (c == null)
            throw new Exception("Constraint type not found: " + type);
        else
            return c;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) { this.type = type; }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
