package saga.common.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Command<T> {
    private T payload;
    @Setter
    private BaseCommand baseCommand;

    public Command(T payload) {
        this.payload = payload;
        this.baseCommand = null;
    }
}
