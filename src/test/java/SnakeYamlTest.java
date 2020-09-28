import dcopsolver.dcop.DCOP;
import fileInput.YamlLoader;

public class SnakeYamlTest {
    public static void main(String[] args) throws Exception {
        YamlLoader loader = new YamlLoader();
        DCOP dcop = loader.loadYAML("graph_coloring.yaml");
        System.out.print(dcop.toString());
    }
}
