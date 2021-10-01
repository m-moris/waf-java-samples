package org.pnop.sample.waf.cb.general;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreaker {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

    private CircuitBreakerStateStore stateStore;

    private long openToHalfOpenWaitTimeInSecond;

    private String name;

    private int failureThreshold = 1;
    private int successThreshold = 1;

    /**
     */
    public CircuitBreaker(
        String name,
        int failureThreshold,
        long openToHalfOpenWaitTimeInSecond) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.successThreshold = failureThreshold / 2 + 1;

        this.openToHalfOpenWaitTimeInSecond = openToHalfOpenWaitTimeInSecond;
        this.stateStore = new CircuitBreakerStateStore();
    }

    public <T> void invoke(T arg, Action<T> action) throws Exception {

        // OPEN なら サービスを呼び出さずに復帰する
        if (isOpen()) {
            logger.info("Action is not invoked.");
            whenCircuitIsOpen(arg, action);
        }

        // HALF_OPTN もしくは CLOSED ならサービス呼び出しを試みる
        // 実装によっては HALF_OPEN 時に、サービス呼び出しを絞る可能性もある
        try {
            action.run(arg);
            transitionToClose();
        } catch (Exception e) {
            trackException(e);
            throw e;
        }
    }

    private void trackException(Throwable t) {
        stateStore.setFailure(t);
        if (stateStore.isClosed()) {
            if (stateStore.getFailureCount() >= failureThreshold) {
                stateStore.open();
            }
        } else {
            stateStore.open();
        }
    }

    private void transitionToClose() {
        if(isClosed()) {
            stateStore.close();
        }
        else if (isHalfOpen()) {
            if (stateStore.successfullHalfOpen() >= this.successThreshold) {
                stateStore.reset();
            } else {
                stateStore.halfOpen();
            }
        }
    }

    private boolean isOpen() {
        return stateStore.isOpen();
    }

    private boolean isClosed() {
        return stateStore.isClosed();
    }

    private boolean isHalfOpen() {
        return !isOpen() && !isClosed();
    }

    private <T> void whenCircuitIsOpen(T arg, Action<T> action) throws Exception {

        var time = stateStore.getLastStateChangeDate().plusSeconds(openToHalfOpenWaitTimeInSecond);

        // Open のタイムアウト期間が経過したかチェックする
        if (time.isBefore(LocalDateTime.now())) {

            // 経過していれば HalfOpen に遷移し、コールバックする
            // 成功したら Open に遷移する
            try {
                synchronized (this) {
                    stateStore.halfOpen();
                    action.run(arg);
                    transitionToClose();
                }

            } catch (Exception e) {
                trackException(e);
                throw new CircuitBreakerOpenException(
                    "The circuit was tripped while half-open. Refer to the inner exception for the cause of the trip.",
                    e);
            }
        } else {
            stateStore.open(); // tracking status
        }

        throw new CircuitBreakerOpenException(
            "The circuit is still open. Refer to the inner exception for the cause of the circuit trip.",
            this.stateStore.getLastException());
    }

    DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:MM:SS");

    @Override
    public String toString() {
        return String.format("name = %s, state = %s -> %s, last exception = %s, failure count = %s , last changed = %s",
            name,
            stateStore.getPrevState(),
            stateStore.getState(),
            stateStore.getLastException(),
            stateStore.getFailureCount(),
            stateStore.getLastStateChangeDate() == null ? null : stateStore.getLastStateChangeDate().format(f));

    }
}
