package org.mlk.kjm.greeting;

import java.util.Map;
import java.util.Optional;

public class GreetingRequest {
    private final String greetingsToKey = "greetingsTo";
    private final Optional<String> greetingsTo;

    public GreetingRequest(Map<String, Optional<String>> postBody) {
        this.greetingsTo = postBody.get(greetingsToKey);
    }

    public Optional<String> getGreetingsTo() {
        return greetingsTo;
    }
}
