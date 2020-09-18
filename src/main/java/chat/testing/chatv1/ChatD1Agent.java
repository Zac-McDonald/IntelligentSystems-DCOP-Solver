package chat.testing.chatv1;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.*;
import chat.testing.IChatService;

/**
 * A simple agent looks for all other IChatService providing components
 * and says "Hey" to them.
 */
@Agent
@RequiredServices({
        @RequiredService(name="clockservice", type= IClockService.class,
            binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
        @RequiredService(name="chatservices", type = IChatService.class, multiple = true,
            binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true))})
@ProvidedServices(@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatServiceD1.class)))
public class ChatD1Agent {
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    /**
     * Sends a hello message to each agent providing the IChatService (which
     * includes this agent).
     * @param agent A reference to the agent (component) that is deployed.
     */
    @AgentBody
    public void body (IInternalAccess agent) {
        ITerminableIntermediateFuture<IChatService> fut = requiredServicesFeature
                .getRequiredServices("chatservices");
        fut.get()
                .forEach((it) -> // -- Java8 Lambda function usage, see: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
                it.message(agent.getComponentIdentifier().getName(), "Hey uze"));
    }

    /**
     * A simple platform to start the ChatD1Agent.
     * @param args Not used
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getMinimal();
        config.addComponent(ChatD1Agent.class);
        config.setGui(true);
        Starter.createPlatform(config).get();
    }

}