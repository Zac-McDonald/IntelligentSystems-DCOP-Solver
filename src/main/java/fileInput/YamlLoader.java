package fileInput;

import dcopsolver.dcop.*;
import dcopsolver.dcop.builders.ConstraintB;
import dcopsolver.dcop.builders.DCOPB;
import dcopsolver.dcop.builders.DomainB;
import dcopsolver.dcop.builders.VariableB;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;

public class YamlLoader {

    public YamlLoader(){}
    public DCOP loadYAML(String fileName) throws Exception {

        //creates new constructor
        DcopConstructor dcopCstr = new DcopConstructor(DCOPB.class);

        //creates link between yaml file headings and classes for DCOP
        TypeDescription dcopDesc = new TypeDescription(DCOPB.class);
        dcopDesc.addPropertyParameters("domains", DomainB.class);
        dcopDesc.addPropertyParameters("variables", VariableB.class);
        dcopDesc.addPropertyParameters("constraints", ConstraintB.class);


        //add descriptions to constructors
        dcopCstr.addTypeDescription(dcopDesc);

        //reads in and populates dcop builder
        Yaml yaml = new Yaml(dcopCstr);
        FileInputStream input = new FileInputStream(fileName);
        DCOPB dcopB = yaml.load(input);

        return dcopB.build(new File(fileName).getParent());
    }
}
