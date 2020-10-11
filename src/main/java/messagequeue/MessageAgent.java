package messagequeue;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.*;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


import java.util.*;

/**
 *  combines together the user and the queue object.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IMessageQueueService.class))
@RequiredServices(@RequiredService(name="mq",
        type= IMessageQueueService.class,
        binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
@Arguments(@Argument(name="topic", clazz=String.class, defaultvalue="\"default_topic\""))
public class MessageAgent implements IMessageQueueService {
    //-------- attributes --------

    /** The agent. */
    @Agent
    protected IInternalAccess agent;

    /** The map of subscribers. */
    protected Map<String, List<SubscriptionIntermediateFuture<Event>>> subscribers;

    /** The message queue. */
    //@AgentServiceSearch
    @AgentService
    protected IMessageQueueService mq;

    /** The topic argument. */
    @AgentArgument
    protected String topic;

    //-------- service features --------

    @AgentCreated
    public void agentCreated()
    {
        this.subscribers = new HashMap<String, List<SubscriptionIntermediateFuture<Event>>>();
    }

    /**
     *  Subscribe to a specific topic. New events that fit to the topic
     *  are forwarded to all subscribers as intermediate results.
     *  A subscribe can unsubscribe by terminating the future.
     *  @param topic The topic.
     *  @return The events.
     */

    @Override
    public ISubscriptionIntermediateFuture<Event> subscribe(String topic)
    {
        //first we get the subs
        final SubscriptionIntermediateFuture<Event>	ret	= (SubscriptionIntermediateFuture<Event>) SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);

        List<SubscriptionIntermediateFuture<Event>> subs = subscribers.get(topic);
        if(subs==null)
        {
            subs = new ArrayList<SubscriptionIntermediateFuture<Event>>();
            subscribers.put(topic, subs);
        }
        subs.add(ret);

        return ret;
    }

    /**
     *  Publish a new event to the queue.
     *  @param topic The topic.
     *  @param event The event to publish.
     */
    @Override
    public IFuture<Void> publish(String topic, Event event)
    {
//		System.out.println("pub: "+topic+" "+event);
        List<SubscriptionIntermediateFuture<Event>> subs = subscribers.get(topic);
        if(subs!=null)
        {
            for(Iterator<SubscriptionIntermediateFuture<Event>> it = subs.iterator(); it.hasNext(); )
            {
                SubscriptionIntermediateFuture<Event> sub = it.next();
                if(!sub.addIntermediateResultIfUndone(event))
                {
                    System.out.println("Removed: "+sub);
                    it.remove();
                }
            }
            if(subs.isEmpty())
                subscribers.remove(topic);
        }

        return IFuture.DONE;
    }

    //-------- Agent Body --------

    @AgentBody
    public void body()
    {
        //get a future from the message queue agent subscription
        final ISubscriptionIntermediateFuture<Event> fut = mq.subscribe(topic);
        fut.addResultListener(new IntermediateDefaultResultListener<Event>()
        {
            public void intermediateResultAvailable(Event event)//when the result is available print a recieved message
            {
                //when receiving a message
                System.out.println("Received: "+agent.getComponentIdentifier()+" "+event);
            }

            public void exceptionOccurred(Exception exception)
            {
                System.out.println("Ex: "+exception);
            }
        });

        IComponentStep<Void> step = new IComponentStep<Void>()
        {
            final int[] cnt = new int[1];
            public IFuture<Void> execute(IInternalAccess ia)
            {
                //sending a message
                mq.publish(topic, new Event("some type", cnt[0]++, agent.getComponentIdentifier()));
                if(cnt[0]<10)
                {
                    agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, this);
                }
                else
                {
                    fut.terminate();
                }
                return IFuture.DONE;
            }
        };
        agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, step);
    }

    public static void main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefault();
        config.setGui(true);
        config.addComponent(MessageAgent.class);
        Starter.createPlatform(config).get();
    }

}
