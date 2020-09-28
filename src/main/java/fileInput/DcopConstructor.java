package fileInput;

import java.util.HashMap;

import dcopsolver.dcop.builders.ConstraintB;
import dcopsolver.dcop.builders.DCOPB;
import dcopsolver.dcop.builders.DomainB;
import dcopsolver.dcop.builders.VariableB;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.error.YAMLException;

//custom constraint constructor
public class DcopConstructor extends Constructor{

    //hash map of class names to class instances
    private HashMap<String,Class<?>> classMap = new HashMap<>();

    //populates HashMap
    public DcopConstructor(Class<?> theRoot) {
        super(theRoot);
        classMap.put(DCOPB.class.getName(), DCOPB.class);
        classMap.put(DomainB.class.getName(), DomainB.class);
        classMap.put(ConstraintB.class.getName(), ConstraintB.class);
        classMap.put(VariableB.class.getName(), VariableB.class);
    }

    //searches for class
    protected Class<?> getClassForNode(Node node){
        String name = node.getTag().getClassName();
        Class<?> cl = classMap.get(name);
        if(cl == null)
            throw new YAMLException("Class not found: " + name);
        else
            return cl;
    }
}
