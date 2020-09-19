package List.Agents;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.*;
import service.testing.ITimeService;

import java.util.List;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
@RequiredServices(
        @RequiredService(name="listservices",
                type= IListService.class,
                multiple=true,
                binding=@Binding(scope=Binding.SCOPE_GLOBAL))
)
public class ListUserAgent {
    /**
     *  The list services are searched and added at agent startup.
     */
    @AgentService
    public void addTimeService(IListService listservice) {
        List<String> list;
        ISubscriptionIntermediateFuture<List<String>> subscription = listservice.subscribe();
        while(subscription.hasNextIntermediateResult()) {
            list = subscription.getNextIntermediateResult();
            for (String str : list){
                System.out.println(str.toString());
            }
        }
    }
    /**
     *  Start a Jadex platform and the ListUserAgent.
     */
    public static void  main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setNetworkName("yourStudentNumber");
        config.setNetworkPass("yourStudentNumber");
        config.addComponent(ListUserAgent.class);
        config.setAwareness(true);
        config.setGui(false);
        Starter.createPlatform(config).get();
    }
}
