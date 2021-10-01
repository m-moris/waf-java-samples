package org.pnop.sample.waf.cb.general;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CircuitBreakerStateStore {

    private ConcurrentLinkedDeque<Throwable> stack = new ConcurrentLinkedDeque<>();

    private CircuitBreakerStateEnum prevState = CircuitBreakerStateEnum.CLOSED;
    private CircuitBreakerStateEnum state = CircuitBreakerStateEnum.CLOSED;

    private LocalDateTime lastStateChangedDate;

    private int successfulHalfOpenCount = 0;

    public CircuitBreakerStateEnum getState() {
        return state;
    }

    public CircuitBreakerStateEnum getPrevState() {
        return prevState;
    }

    public LocalDateTime getLastStateChangeDate() {
        return lastStateChangedDate;
    }

    public LocalDateTime setLastStateChangeDate(LocalDateTime date) {
        lastStateChangedDate = date;
        return lastStateChangedDate;
    }

    public Throwable getLastException() {
        return stack.isEmpty() ? null : stack.peek();
    }

    public void trip(Throwable t) {
        changeState(CircuitBreakerStateEnum.OPEN);
        stack.push(t);
    }

    public int getFailureCount() {
        return stack.size();
    }

    public void setFailure(Throwable t) {
        stack.push(t);
    }

    public void reset() {
        changeState(CircuitBreakerStateEnum.CLOSED);
        stack.clear();
        successfulHalfOpenCount = 0;
    }

    public void close() {
        changeState(CircuitBreakerStateEnum.CLOSED);
    }

    public void halfOpen() {
        changeState(CircuitBreakerStateEnum.HALF_OPEN);
    }

    public void open() {
        changeState(CircuitBreakerStateEnum.OPEN);
    }
    
    public int successfullHalfOpen() {
        return ++successfulHalfOpenCount;
    }

    public boolean isOpen() {
        return state == CircuitBreakerStateEnum.OPEN;
    }

    public boolean isClosed() {
        return state == CircuitBreakerStateEnum.CLOSED;
    }

    private void changeState(CircuitBreakerStateEnum state) {
        this.prevState = this.state;
        this.state = state;
        this.lastStateChangedDate = LocalDateTime.now();
    }

}
