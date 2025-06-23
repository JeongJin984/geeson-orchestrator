package saga.common.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Event<T> {
    private T payload;
    @Setter
    private BaseEvent baseEvent;

    public Event(T payload) {
        this.payload = payload;
        this.baseEvent = null;
    }
}
