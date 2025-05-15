package view;

import controller.event.game.UseCannons;

import java.util.EventListener;

public class EventHandlerClient {


    public EventHandlerClient() {

    }

    private EventListener<UseCannons> listener = data -> {
        System.out.print("ciao");

    };



}
