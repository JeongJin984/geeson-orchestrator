package saga.common.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Event<T> {
    private final T payload;
    @Setter
    private BaseEvent baseEvent;

    public Event(T payload) {
        this.payload = payload;
        this.baseEvent = null;
    }
}
