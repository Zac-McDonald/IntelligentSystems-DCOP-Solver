package messagequeue;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.*;

/**
 *  Example queue user that registers at the queue with a topic and
 *  publishes a number of topics before terminating.
 */
@Agent
@RequiredServices(@RequiredService(name="mq",
		type= IMessageQueueService.class,
		binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
@Arguments(@Argument(name="topic", clazz=String.class, defaultvalue="\"default_topic\""))
public class UserAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	
	/** The message queue. */
	//@AgentServiceSearch
	@AgentService
	protected IMessageQueueService mq;
	
	/** The topic argument. */
	@AgentArgument
	protected String topic;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		final ISubscriptionIntermediateFuture<Event> fut = mq.subscribe(topic);
		fut.addResultListener(new IntermediateDefaultResultListener<Event>()
		{
			public void intermediateResultAvailable(Event event)
			{
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
		config.setNetworkName("yourStudentNumber");
		config.setNetworkPass("yourStudentNumber");
		config.setGui(true);
		config.addComponent(UserAgent.class);
		Starter.createPlatform(config).get();
	}

}
