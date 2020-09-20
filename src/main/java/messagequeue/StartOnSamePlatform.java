package messagequeue;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import service.testing.TimeProviderAgent;
import service.testing.TimeUserAgent;

public class StartOnSamePlatform {
    /**
     * This program will start the MessageQueueAgent and then a user on the
     * same JadeX platform. You can start each agent type on their own platform
     * using the main functions in each agent.
     * @param args not used
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setNetworkName("yourStudentNumber");
        config.setNetworkPass("yourStudentNumber");
        config.setGui(true);
        config.addComponent(MessageQueueAgent.class);
        config.addComponent(UserAgent.class);
        config.addComponent(UserAgent.class);
        Starter.createPlatform(config).get();
    }
}
