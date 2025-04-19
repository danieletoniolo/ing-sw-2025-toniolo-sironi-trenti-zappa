package network.messages;

import java.io.Serializable;

public class SingleArgMessage<T> extends Message {
    private final T arg1;

    public SingleArgMessage(MessageType type, T arg1) {
        super(type);
        this.arg1 = arg1;
    }

    public T get1() {
        return arg1;
    }
}
