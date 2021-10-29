package ru.gx.fin.quik.events;

import ru.gx.fin.gate.quik.model.internal.QuikAllTrade;
import ru.gx.fin.gate.quik.model.internal.QuikAllTradesPackage;
import ru.gx.kafka.events.AbstractOnObjectsLoadedFromIncomeTopicEvent;
import ru.gx.kafka.events.AbstractOnRawDataLoadedFromIncomeTopicEvent;

public class LoadedAllTradesEvent extends AbstractOnRawDataLoadedFromIncomeTopicEvent {
    public LoadedAllTradesEvent(Object source) {
        super(source);
    }
}