package message;

import chat.testing.IChatService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.*;
import jadex.micro.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Agent
@RequiredServices({@RequiredService(name="messageServices", type = IMessageService.class, multiple = true,
        binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true))})
@ProvidedServices(@ProvidedService(type= IMessageService.class))
@Arguments(@Argument(name="dcop", clazz= Object.class, defaultvalue="\"HashMap<String, Integer> map = new HashMap<String, Integer>()\""))
public class MessageAgent implements IMessageService{
    @Agent
    protected IInternalAccess agent; //reference to itself

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    // The subscriptions that will get messaged.
    protected HashMap<String, IMessageService> addressBook;
    protected HashMap<IComponentIdentifier, SubscriptionIntermediateFuture<Data>> subscriptions
            = new HashMap<IComponentIdentifier, SubscriptionIntermediateFuture<Data>>();

    @AgentArgument
    private Object dcop;

    public IInternalAccess getAgent(){
        return agent;
    }

    public void updateAddressBookListener () {
        ITerminableIntermediateFuture<IMessageService> services = requiredServicesFeature.getRequiredServices("messageServices");

        final Future<IMessageService> ret = new Future();
        services.addIntermediateResultListener(new IntermediateDefaultResultListener<IMessageService>() {
            //@Override
            public void intermediateResultAvailable (IMessageService iMessageService) {
                addressBook.put(iMessageService.getAgent().getComponentIdentifier().toString(), iMessageService);
            }

            public void exceptionOccurred(Exception exception)
            {
                System.out.println("Ex: "+exception);
            }
        });
    }

    public void updateAddressBookStream () {
        ITerminableIntermediateFuture<IMessageService> fut = requiredServicesFeature.getRequiredServices("messageServices");
        fut.get().forEach((it) -> {
            String me = getAgent().toString();
            me = me.substring(0, me.indexOf("@"));

            String them = it.getAgent().toString();
            them = them.substring(0, them.indexOf("@"));

            // TODO: Doesn't update old hosts, can't simple do "addressBook.get(them) != it" because 'it' is different
            if (!addressBook.containsKey(them)) {
                System.out.println(me + " - discovered -> " + them);
                addressBook.put(them, it);
            }
        });
    }

    @AgentBody
    public void body (IInternalAccess agent) {
        addressBook = new HashMap<>();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            // Do fuck all
        }

        //updateAddressBookListener();
        updateAddressBookStream();

/*

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
*/
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                // Do fuck all
            }

            String me = getAgent().toString();
            me = me.substring(0, me.indexOf("@"));

            System.out.println(me + ": " + addressBook);

            // Message all subscribers
            for (IComponentIdentifier id : subscriptions.keySet()) {
                Data content = getMessageContent();
                sendMessage(content, id);
            }

            updateAddressBookStream();
        }

        /*
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
        */
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

    // These methods make hard calls (they wait) for a return future from the requested agents
    // Should ideally be replaced with a listener.
    public void sendMessage(Data content, IComponentIdentifier target){
        subscriptions.get(target).addIntermediateResult(content);
    }

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

}
