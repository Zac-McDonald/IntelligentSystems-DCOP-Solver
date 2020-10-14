package message;


import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.*;
import jadex.micro.annotation.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Agent
@RequiredServices({@RequiredService(name="messageServices", type = IMessageService.class, multiple = true,
                    binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true))})
@ProvidedServices(@ProvidedService(name = "thisService", type= IMessageService.class))
public class MessageAgent implements IMessageService{
    @Agent
    protected IInternalAccess agent; //reference to itself

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    // The connections that can be messaged.
    protected HashMap<IComponentIdentifier, IMessageService> addressBook = new HashMap<>();
    // A record of everyone that is online

    public IInternalAccess getAgent(){
        return agent;
    }

    public void updateAddressBook () {
        //get a container of the IMessageService's provided by agents on the platform
        ITerminableIntermediateFuture<IMessageService> fut = requiredServicesFeature.getRequiredServices("messageServices");

        List<IComponentIdentifier> activeAgents = fut.get().stream().map((it) -> {
            IComponentIdentifier id = it.getAgent().getComponentIdentifier();

            //add each agent to the address book
            if (!addressBook.keySet().contains(id)) {
                //the agent appears in the stream but not on the list of active agents, it was just discovered, add it.
                addressBook.put(id, it);
                System.out.println(agent.getComponentIdentifier().toString() + " Discovered: " + id);
            }
            return id;  // Return id, to create a list of active agents
        }).collect(Collectors.toList());

        //Dropped agent detection
        addressBook.keySet().removeIf(id -> {
            // Remove unreachable agents
            if (!activeAgents.contains(id)) {
                System.out.println(agent.getComponentIdentifier().toString() + " Dropped: " + id);
                return true;
            }
            return false;
        });
    }

    @AgentBody
    public void body (IInternalAccess agent) {
        updateAddressBook();

        // Message all connections
        for (IComponentIdentifier id : addressBook.keySet()) {
            sendMessage(new Data("Debug", "Trace", agent.getComponentIdentifier()), id);
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            // Temporary delay
        }
    }

    @Override
    public Future<Void> message (Data content) {
        if (content.type == "Debug" && content.value == "Trace") {
            String me = getAgent().toString();
            me = me.substring(0, me.indexOf("@"));

            String them = content.source.toString();
            them = them.substring(0, them.indexOf("@"));

            System.out.println(me + " received messaged from " + them + ", content: " + content.value.toString());
        }

        receiveMessage(content);
        return null;
    }

    protected void sendMessage (Data content, IComponentIdentifier id) {
        addressBook.get(id).message(content);
    }

    protected Boolean receiveMessage (Data content) {
        // Handle lowest level messages here, otherwise return false and let higher agents handle them
        return false;
    }
}
