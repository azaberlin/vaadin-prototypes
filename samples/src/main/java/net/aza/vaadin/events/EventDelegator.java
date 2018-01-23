package net.aza.vaadin.events;


import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.vaadin.spring.events.EventBus;

/**
 * This delegator component publish spring events to the Vaadin Eventbus (and registered listeners).
 * <p/>
 * There was some default provided example that didn't work for me, so I expanded it to the given version.
 * <p/>
 * Just register it at your configuration class as a bean and you can use Vaadin and Spring Events simutaneously .
 * <pre>
&#64;Bean
protected EventDelegator eventDelegator(final ApplicationEventBus bus) {
	return new EventDelegator(bus);
}
</pre>
 *
 * @see https://github.com/peholmst/vaadin4spring/tree/master/samples/eventbus-sample
 * @author azaberlin
 *
 */
public class EventDelegator {

	private EventBus eventbus;

	public EventDelegator(final EventBus bus) {
		this.eventbus = bus;
	}

	@EventListener
	private void delegatePayloadEvents(final PayloadApplicationEvent<?> event) {
		this.eventbus.publish(event.getSource(), event.getPayload());
	}

	@EventListener
	private void delegateApplicationEvents(final ApplicationEvent event) {
		this.eventbus.publish(event.getSource(), event);
	}
}
