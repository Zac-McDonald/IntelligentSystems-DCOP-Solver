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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Agent
@RequiredServices({@RequiredService(name="messageServices", type = IMessageService.class, multiple = true,
        binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true))})
@ProvidedServices(@ProvidedService(name = "thisService", type= IMessageService.class))
@Arguments(@Argument(name="dcop", clazz= Object.class, defaultvalue="\"HashMap<String, Integer> map = new HashMap<String, Integer>()\""))
public class MessageAgent implements IMessageService{
    @Agent
    protected IInternalAccess agent; //reference to itself

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    // The subscriptions that will get messaged.
    protected HashMap<IComponentIdentifier, IMessageService> addressBook = new HashMap<>();
    // A record of who is online

    @AgentArgument
    private Object dcop;

    public IInternalAccess getAgent(){
        return agent;
    }

    public void updateAddressBookStream () {
        //get a container of the IMessageService's provided by agents on the platform
        ITerminableIntermediateFuture<IMessageService> fut = requiredServicesFeature.getRequiredServices("messageServices");

        List<IComponentIdentifier> activeAgents = fut.get().stream().map((it) -> {
            IComponentIdentifier id = it.getAgent().getComponentIdentifier();
            //add each agent to the address book
            if (!addressBook.keySet().contains(id)) {
                //the agent appears in the stream but not on the list of active agents, add it.
                addressBook.put(id, it);
                System.out.println(agent.getComponentIdentifier().toString() + "Discovered:" + id);
            }
            return id;
        }).collect(Collectors.toList());

        //Dropped agent detection
        addressBook.keySet().removeIf(x -> {
            if (!activeAgents.contains(x)) {
                System.out.println("\tDropped " + x);
                return true;
            }
            return false;
        });
    }

    @AgentBody
    public void body (IInternalAccess agent) {
        updateAddressBookStream();

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                // Do fuck all
            }

            // Message all subscribers
            for (IComponentIdentifier id : addressBook.keySet()) {
                Data content = getMessageContent();
                addressBook.get(id).message(content);
            }

            updateAddressBookStream();
        }
    }

    //TODO Replace this with a function that returns meaningful message content
    private Data getMessageContent() {
        return new Data("test",dcop, agent.getComponentIdentifier());
    }

    @Override
    public Future<Void> message(Data content) {
        String me = getAgent().toString();
        me = me.substring(0, me.indexOf("@"));

        String them = content.source.toString();
        them = them.substring(0, them.indexOf("@"));

        System.out.println(me + " messaged from " + them);
        return null;
    }


}
