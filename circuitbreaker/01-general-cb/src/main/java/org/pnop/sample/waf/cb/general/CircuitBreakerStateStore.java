package org.pnop.sample.waf.cb.general;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CircuitBreakerStateStore {

    private ConcurrentLinkedDeque<Throwable> stack = new ConcurrentLinkedDeque<>();

    private CircuitBreakerStateEnum state = CircuitBreakerStateEnum.Closed;

    private LocalDateTime lastStateChangedDate;

    public CircuitBreakerStateEnum getState() {
        return state;
    }

    public Throwable getLastException() {
        return stack.isEmpty() ? null : stack.peek();
    }

    public LocalDateTime setLastStateChangeDate(LocalDateTime date) {
        lastStateChangedDate = date;
        return lastStateChangedDate;
    }
    
    public LocalDateTime getLastStateChangeDate() {
        return lastStateChangedDate;
    }

    public void trip(Throwable t) {
        changeState(CircuitBreakerStateEnum.Open);
        stack.push(t);
    }

    public void reset() {
        changeState(CircuitBreakerStateEnum.Closed);
        stack.clear();
    }

    public void halfOpen() {
        changeState(CircuitBreakerStateEnum.HalfOpen);
    }

    public boolean isClosed() {
        return state == CircuitBreakerStateEnum.Closed;
    }

    private void changeState(CircuitBreakerStateEnum state) {
        this.state = state;
        this.lastStateChangedDate = LocalDateTime.now();
    }

}
