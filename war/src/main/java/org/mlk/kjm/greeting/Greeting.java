package org.mlk.kjm.greeting;

import java.util.Optional;

public class Greeting {
    private final Optional<String> greetingTo;

    public Greeting(Optional<String> greetingTo) {
        this.greetingTo = greetingTo;
    }

    public Optional<String> getGreetingTo() {
        return greetingTo;
    }
}
