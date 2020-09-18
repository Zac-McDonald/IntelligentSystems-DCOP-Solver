package dcopsolver;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

public class RunAgents {
    /**
     * A basic example of a simple Java program that starts a minimal JadeX
     * platform with a very simple Agent that prints hello world.
     * @param args Input string is ignored.
     */
    public static void main(String[] args) {
        PlatformConfiguration conf = PlatformConfiguration.getMinimal();
        conf.addComponent(SimpleAgent.class);
        conf.setGui(true);
        Starter.createPlatform(conf);
    }
}
