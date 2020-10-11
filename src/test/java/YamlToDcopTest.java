import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

public class YamlToDcopTest {
    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadYAML("yamlToDcopTest.yaml");
        dcop.print();
    }
}