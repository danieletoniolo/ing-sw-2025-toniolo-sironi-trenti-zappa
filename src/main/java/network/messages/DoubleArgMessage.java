package network.messages;

public class DoubleArgMessage<T1, T2> extends Message {
    private final T1 arg1;
    private final T2 arg2;

    public DoubleArgMessage(MessageType type, T1 arg1, T2 arg2) {
        super(type);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public T1 getArg1() {
        return arg1;
    }

    public T2 getArg2() {
        return arg2;
    }
}
