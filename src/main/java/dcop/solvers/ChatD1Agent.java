package dcop.solvers;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


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
@ProvidedServices(@ProvidedService(type= IChatService.class, implementation=@Implementation(ChatServiceD1.class)))
public class ChatD1Agent {
    /** The agent. */
    @Agent
    protected IInternalAccess agent;

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

        //make an event
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("matthew", 1);
        map.put("zac", 2);
        map.put("Gus", 3);
        Event event = new Event("whisper",map, agent.getComponentIdentifier());

        //message all agents
//        messageAll(agent, fut, event);

        //target is a reference to a specific agent.
        //currently returning have dummy function for getting a target.
        IInternalAccess target = getRandomTargetAgent(fut);
        messageSingle(agent, fut, event, target);

//        messageGroup(agent, fut, event, targets);

    }

    //TODO Replace this function with one that actually selects a target
    public IInternalAccess getRandomTargetAgent(ITerminableIntermediateFuture<IChatService> fut){
        Iterator<IChatService> it = fut.get().iterator();
        IChatService i = it.next();
        return i.getAgent();
    }

    public void messageAll(IInternalAccess agent, ITerminableIntermediateFuture<IChatService> fut, Event event){
        //the agents no longer send events to themselves
        Iterator<IChatService> it = fut.get().iterator();
        while(it.hasNext()){
            IChatService i = it.next();
            if (!i.getAgent().equals(agent)){
                //as long as the agent doesnt match itself it sends the event
                i.message(agent.getComponentIdentifier().getName(), event);
            }
        }
    }

    public void messageSingle(IInternalAccess agent, ITerminableIntermediateFuture<IChatService> fut, Event event, IInternalAccess target){
        //the agents no longer send events to themselves
        Iterator<IChatService> it = fut.get().iterator();
        while (it.hasNext()){
            IChatService i = it.next();
            if (i.getAgent().equals(target)){
                //loops through the futures and calls a message on the target
                i.message(agent.getComponentIdentifier().getName(), event);
            }
        }
    }

    public void messageGroup(IInternalAccess agent, ITerminableIntermediateFuture<IChatService> fut, Event event, IInternalAccess[] targets){
        //the agents no longer send events to themselves
        Iterator<IChatService> it = fut.get().iterator();
        while (it.hasNext()){
            IChatService i = it.next();
            for (IInternalAccess a :targets) {
                if (i.getAgent().equals(a)){
                    //loops through the futures and calls messages for the targets in the group
                    i.message(agent.getComponentIdentifier().getName(), event);
                }
            }
        }
    }

    /**
     * A simple platform to start the ChatD1Agent.
     * @param args Not used
     */
    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getMinimal();
        config.addComponent(ChatD1Agent.class);
        config.addComponent(ChatD1Agent.class);
        config.setGui(true);
        Starter.createPlatform(config).get();
    }

}