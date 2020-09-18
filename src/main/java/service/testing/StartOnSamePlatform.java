package service.testing;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

public class StartOnSamePlatform {
    /**
     * This program will start the TimeProvider agent and then two users on the
     * same JadeX platform. You can start each agent type on their own platform
     * using the main functions in each agent.
     * @param args not used
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setGui(true);
        config.addComponent(TimeProviderAgent.class);
        config.addComponent(TimeUserAgent.class);
        config.addComponent(TimeUserAgent.class);
        Starter.createPlatform(config).get();
    }
}
