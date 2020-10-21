package message;


import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.*;
import jadex.micro.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Agent
@RequiredServices({@RequiredService(name="messageServices", type = IMessageService.class, multiple = true,
                    binding = @Binding(scope =RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true))})
@ProvidedServices(@ProvidedService(name = "thisService", type= IMessageService.class))
public class MessageAgent implements IMessageService{
    @Agent
    protected IInternalAccess agent; //reference to itself

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    // The connections that can be messaged / record of everyone that is online
    protected HashMap<IComponentIdentifier, IMessageService> addressBook = new HashMap<>();

    // Common for both Host and Solver -- so implemented here
    protected List<IComponentIdentifier> hosts = new ArrayList<>();
    protected List<IComponentIdentifier> solvers = new ArrayList<>();

    // Stores messages whose recipient could not be found
    private HashMap<IComponentIdentifier, IMessageService> pendingAddresses = new HashMap<>();

    public IInternalAccess getAgent(){
        return agent;
    }

    public IComponentIdentifier getId () { return agent.getComponentIdentifier(); }

    public void updateAddressBook () {
        //get a container of the IMessageService's provided by agents on the platform
        ITerminableIntermediateFuture<IMessageService> fut = requiredServicesFeature.getRequiredServices("messageServices");

        List<IComponentIdentifier> activeAgents = fut.get().stream().map((it) -> {
            IComponentIdentifier id = it.getAgent().getComponentIdentifier();
            //add each agent to the address book
            if (!addressBook.containsKey(id)) {
                // The agent appears in the stream but not on the list of active agents, it was just discovered, add it.
                // No longer adds to addressBook until we get a tellType message
                // Instead adds to a pendingAddresses book
                // This ensures that we have a 2-way connection with the agent before adding to addressBook
                pendingAddresses.put(id, it);
                System.out.println(agent.getComponentIdentifier().toString() + " Discovered: " + id);
                agentDiscovered(id);
            }
            return id;  // Return id, to create a list of active agents
        }).collect(Collectors.toList());

        //Dropped agent detection
        addressBook.keySet().removeIf(id -> {
            // Remove unreachable agents
            if (!activeAgents.contains(id)) {
                System.out.println(agent.getComponentIdentifier().toString() + " Dropped: " + id);
                agentDropped(id);
                return true;
            }
            return false;
        });
    }

    @AgentBody
    public void body (IInternalAccess agent) {
        updateAddressBook();
/*
        addressBook.keySet().forEach(id -> {
            sendMessage(new Data("Debug.ignore", "Test message", getId()), id);
        });
*
        // Delay for debugging clarity
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            //
        }
*/
    }

    @Override
    public Future<Void> message (Data content) {
        receiveMessage(content, content.getTypeTree());
        return null;
    }

    protected void sendMessage (Data content, IComponentIdentifier id) {
        // TODO: Remove after, or toggle with, debugging
        // Wrap all messages in a Debug.trace to output them to the console
        content = new Data("Debug.trace", content, getId());

        // Send to agent, regardless of which addressBook they are in
        if (addressBook.containsKey(id)) {
            addressBook.get(id).message(content);
        } else if (pendingAddresses.containsKey(id)) {
            pendingAddresses.get(id).message(content);
        }
    }

    protected Data receiveMessage (Data content, String[] typeTree) {
        // Handle lowest level messages here

        if (typeTree.length == 2) {
            switch (typeTree[0]) {
                case "Debug":
                    if (typeTree[1].equals("trace")) {
                        String me = getAgent().toString();
                        me = me.substring(0, me.indexOf("@"));

                        String them = content.source.toString();
                        them = them.substring(0, them.indexOf("@"));

                        System.out.println(me + " received messaged from " + them + ", content: " + content.value.toString());

                        // Unwrap any remaining content
                        // Case-by-case -- message was enclosed, so need to re-evaluate it
                        if (content.value instanceof Data) {
                            content = (Data)content.value;
                            // Send it for another loop
                            receiveMessage(content, content.getTypeTree());
                            return null;
                        } else {
                            content = null;
                        }
                    }
                    break;
                case "Discover":
                    if (typeTree[1].equals("tellType")) {
                        // If the id is null here, there is a problem -- it should NEVER happen
                        addressBook.put(content.source, pendingAddresses.get(content.source));
                        pendingAddresses.remove(content.source);

                        if (content.value.equals("Host")) {
                            hosts.add(content.source);
                        } else if (content.value.equals("Solver")) {
                            solvers.add(content.source);
                        }
                    }
                    break;
            }
        }

        return content;
    }

    // TODO: Ideally call these async, they might block for a while otherwise
    protected void agentDiscovered (IComponentIdentifier id) {
        // Ask what type of agent they are
        Data content = new Data("Discover.askType", null, getId());
        sendMessage(content, id);
    }

    protected void agentDropped (IComponentIdentifier id) {
        // For now just remove them from our records
        hosts.remove(id);
        solvers.remove(id);
    }
}
