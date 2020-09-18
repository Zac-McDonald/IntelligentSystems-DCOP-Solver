package chat.testing.chatv2;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.*;
import chat.testing.IChatService;

/**
 * This agent is just a deceleration of what services are required and offered.
 */
@Agent
@RequiredServices({
        @RequiredService(name="clockservice", type= IClockService.class,
                binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
        @RequiredService(name="chatservices", type = IChatService.class, multiple = true,
                binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true))})
@ProvidedServices(@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatServiceD2.class)))
public class ChatD2Agent {
    /**
     * Agent requires no code, just required and provided services.
      * @param args Not used
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getMinimal();
        config.addComponent(ChatD2Agent.class);
        config.addComponent(ChatD2Agent.class);
        config.setGui(true);
        Starter.createPlatform(config).get();
    }
}