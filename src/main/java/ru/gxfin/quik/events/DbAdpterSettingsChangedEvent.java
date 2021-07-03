package ru.gxfin.quik.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Событие об изменнении параметра системы
 * @since 1.0
 */
public class DbAdpterSettingsChangedEvent extends ApplicationEvent {
    /**
     * Имя параметра, который изменился
     */
    @Getter
    private final String settingName;

    public DbAdpterSettingsChangedEvent(Object source, String settingName) {
        super(source);
        this.settingName = settingName;
    }
}
