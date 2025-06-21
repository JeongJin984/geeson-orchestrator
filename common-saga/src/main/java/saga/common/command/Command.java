package saga.common.command;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Command<T> {
    private final T payload;
    @Setter
    private BaseCommand baseCommand;

    public Command(T payload) {
        this.payload = payload;
        this.baseCommand = null;
    }
}
