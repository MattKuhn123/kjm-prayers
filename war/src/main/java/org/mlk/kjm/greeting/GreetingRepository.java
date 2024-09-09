package org.mlk.kjm.greeting;

import java.util.ArrayList;
import java.util.List;

public class GreetingRepository {

    private final List<Greeting> greetings;

    public GreetingRepository() {
        greetings = new ArrayList<Greeting>();
    }

    public void addGreeting(Greeting newGreeting) {
        this.greetings.add(newGreeting);
    }

    public List<Greeting> getGreetings() {
        return this.greetings;
    }
}
