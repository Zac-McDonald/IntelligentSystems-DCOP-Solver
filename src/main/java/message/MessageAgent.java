package message;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.*;
import jadex.micro.annotation.*;

import java.util.*;

@Agent
@RequiredServices(@RequiredService(name="messageServices", type = IMessageService.class,multiple = true,
        binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true)))
@ProvidedServices(@ProvidedService(type= IMessageService.class))
@Arguments(@Argument(name="dcop", clazz= Object.class, defaultvalue="\"HashMap<String, Integer> map = new HashMap<String, Integer>()\""))
public class MessageAgent implements IMessageService{
    @Agent
    protected IInternalAccess agent; //reference to itself
    @ServiceComponent
    IMessageService requiredServicesFeature;
    /** The subscriptions that will get messaged. */
    protected HashMap<IComponentIdentifier, SubscriptionIntermediateFuture<Data>> subscriptions
            = new HashMap<IComponentIdentifier, SubscriptionIntermediateFuture<Data>>();
    @AgentArgument
    private Object dcop;

    public ISubscriptionIntermediateFuture<Data> subscribe(IComponentIdentifier agentID) {
        // Add a subscription to the set of subscriptions.
        SubscriptionIntermediateFuture<Data> ret = new SubscriptionIntermediateFuture<>();
        subscriptions.put(agentID,ret);
        ret.setTerminationCommand(new TerminationCommand() {
            /**
             *  The termination command allows to be informed, when the subscription ends,
             *  e.g. due to a communication error or when the service user explicitly
             *  cancels the subscription.
             */
            public void terminated(Exception reason) {
                System.out.println("removed " + agentID.toString() + " due to: " +reason);
                subscriptions.remove(agentID);
            }
        });
        return ret;
    }

    public IInternalAccess getAgent(){
        return agent;
    }


    @AgentBody
    public void body (IInternalAccess agent) {
        //set up a subscription to all(???) message service providers
        ISubscriptionIntermediateFuture<Data> fut = requiredServicesFeature.subscribe(agent.getComponentIdentifier());
        fut.addResultListener(new IntermediateDefaultResultListener<Data>()
        {
            public void intermediateResultAvailable(Data content)//when the result is available print a recieved message
            {
                System.out.println("Received: "+agent.getComponentIdentifier()+" "+content);
            }

            public void exceptionOccurred(Exception exception)
            {
                System.out.println("Ex: "+exception);
            }
        });

        Integer x=10;
        //the solving loop
        while (x > 0){
            IComponentIdentifier target = getRandomTargetAgent();
            //get the message content
            Data content = getMessageContent();
            //send the message
            sendMessage(content, target);
            x--;
        }
    }

    //TODO Replace this with a function that returns meaningful message content
    private Data getMessageContent() {
        return new Data("test",dcop, agent.getComponentIdentifier());
    }

    //TODO Replace this function with one that actually selects a target
    public IComponentIdentifier getRandomTargetAgent(){
        for (IComponentIdentifier id:subscriptions.keySet()) {
            return id;
        }
        return null;
    }

    /** These methods make hard calls (they wait) for a return future from the requested agents
     * Should ideally be replaced with a listener. **/
    public void sendMessage(Data content, IComponentIdentifier target){
        subscriptions.get(target).addIntermediateResult(content);
    }

}
