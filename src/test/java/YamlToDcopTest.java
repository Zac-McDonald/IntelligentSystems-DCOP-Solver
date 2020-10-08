import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

public class YamlToDcopTest {
    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadYAML("./yaml/yamlToDcopTest.yaml");
        //dcop.print();
        System.out.println(dcop.prettyPrint());
    }
}
