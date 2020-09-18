package chat.testing;


import jadex.commons.future.Future;

/**
 * A chatting interface used by version one and two of the project.
 */
public interface IChatService {
    Future<Void> message (String sender, String text);
}
