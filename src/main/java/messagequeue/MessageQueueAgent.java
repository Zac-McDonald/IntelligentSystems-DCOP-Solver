package messagequeue;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
//import jadex.bridge.service.annotation.OnInit; replaced with @AgentCreated
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.*;

/**
 *  This agent represents the central message queue. It offers a 
 *  message queue service that is used by the clients.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IMessageQueueService.class))
public class MessageQueueAgent implements IMessageQueueService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The map of subscribers. */
	protected Map<String, List<SubscriptionIntermediateFuture<Event>>> subscribers;
	
	//-------- methods --------

	/**
	 *  Called on agent creation.
	 */
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
	public ISubscriptionIntermediateFuture<Event> subscribe(String topic)
	{
		//first we get the subs
		final SubscriptionIntermediateFuture<Event>	ret	= (SubscriptionIntermediateFuture<Event>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);

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
	public IFuture<Void> publish(String topic, Event event)
	{
		System.out.println("pub: "+topic+" "+event);
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

	public static void main(String[] args) {
		PlatformConfiguration config = PlatformConfiguration.getDefault();
		config.setGui(true);
		config.addComponent(MessageQueueAgent.class);
		Starter.createPlatform(config).get();
	}

}


