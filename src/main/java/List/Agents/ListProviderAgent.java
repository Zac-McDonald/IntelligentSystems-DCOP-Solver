package List.Agents;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.*;

/**
 *  The time provider periodically sends out time values to all subscribers.
 *  For simplicity, the agent implements the time service itself.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type= IListService.class))
public class ListProviderAgent  implements IListService {
    //-------- attributes --------

    /** The list created on startup. */
    protected List<String> list = getList();

    /** The subscriptions to be informed about the time. */
    protected Set<SubscriptionIntermediateFuture<List<String>>> subscriptions
            = new LinkedHashSet<SubscriptionIntermediateFuture<List<String>>>();

    //-------- IListService interface --------

    /**
     *  List property.
     */
    public List<String>   list() {
        return list;
    }

    /**
     *  Subscribe to the list service.
     *  Every couple of seconds, the list will be
     *  sent to the subscriber.
     */
    public ISubscriptionIntermediateFuture<List<String>> subscribe() {
        // Add a subscription to the set of subscriptions.
        SubscriptionIntermediateFuture<List<String>> ret = new SubscriptionIntermediateFuture<List<String>>();
        subscriptions.add(ret);
        ret.setTerminationCommand(new TerminationCommand() {
            /**
             *  The termination command allows to be informed, when the subscription ends,
             *  e.g. due to a communication error or when the service user explicitly
             *  cancels the subscription.
             */
            public void terminated(Exception reason) {
                System.out.println("removed subscriber due to: "+reason);
                subscriptions.remove(ret);
            }
        });
        return ret;
    }

    //-------- agent life cycle --------

    /**
     *  Due to annotation, called once after agent is initialized.
     *  The internal access parameter is optional and is injected automatically.
     */
    @AgentBody
    public void body(IInternalAccess ia) {
        // The execution feature provides methods for controlling the execution of the agent.
        IExecutionFeature   exe = ia.getComponentFeature(IExecutionFeature.class);
        System.out.println("Hello World, I am the list holder!");
        // Execute a step every 5000 milliseconds, start from next full 5000 milliseconds
        exe.repeatStep(5000-System.currentTimeMillis()%5000, 5000, ia1 -> {
            // Notify all subscribers
            for(SubscriptionIntermediateFuture<List<String>> subscriber: subscriptions) {
                // Add the stored list as intermediate result.
                // The if-undone part is used to ignore errors,
                // when subscription was cancelled in the mean time.
                subscriber.addIntermediateResultIfUndone(list);
            }
            return IFuture.DONE;
        });
    }


    //-------- helper functions --------

    private List<String> getList() {
        List<String> ret = new ArrayList<String>();
        ret.add("string one");
        ret.add("string two");
        ret.add("string three");
        return ret;
    }

    /**
     *  Start a Jadex platform and add just the TimeProviderAgent.
     */
    public static void  main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.addComponent(ListProviderAgent.class);
        config.setNetworkName("yourStudentNumber");
        config.setNetworkPass("yourStudentNumber");
        config.setAwareness(true);
        Starter.createPlatform(config).get();
    }
}