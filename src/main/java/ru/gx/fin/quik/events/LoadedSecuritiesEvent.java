package ru.gx.fin.quik.events;

import ru.gx.events.AbstractDataEvent;

public class LoadedSecuritiesEvent extends AbstractDataEvent {
    public LoadedSecuritiesEvent(Object source) {
        super(source);
    }
}
