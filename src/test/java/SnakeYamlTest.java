import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

public class SnakeYamlTest {
    public static void main(String[] args){
        try{
            FileInputStream input = new FileInputStream("YamlTest.yaml");
            Yaml yaml = new Yaml();
            List<String> list = (List<String>) yaml.load(input);
            System.out.println(list);


        }
        catch(Exception e){System.out.println("File Import Failed");}
    }
}
