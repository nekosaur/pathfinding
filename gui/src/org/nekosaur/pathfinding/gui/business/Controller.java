package org.nekosaur.pathfinding.gui.business;

import com.google.common.eventbus.EventBus;
import org.nekosaur.pathfinding.gui.business.events.EditMapLoadEvent;

import javax.annotation.PostConstruct;

/**
 * @author nekosaur
 */
public class Controller {

    private final EventBus eventBus = new EventBus();

    @PostConstruct
    public void init() {

        System.out.println("ASD");



    }

    public void registerEvents(Object o) {
        eventBus.register(o);
    }

    public void postEvent(Object e) {
        eventBus.post(e);
    }
}
