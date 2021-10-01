package org.pnop.sample.waf.cb.general;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreaker {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

    private CircuitBreakerStateStore stateStore;

    private long openToHalfOpenWaitTimeInSecond;

    private String name;

    /**
     * @param name
     * @param openToHalfOpenWaitTimeInSecond
     */
    public CircuitBreaker(String name, long openToHalfOpenWaitTimeInSecond) {
        this.name = name;
        this.openToHalfOpenWaitTimeInSecond = openToHalfOpenWaitTimeInSecond;
        this.stateStore = new CircuitBreakerStateStore();
    }

    public void invoke(Action action) throws Exception {
        logger.info("invoked");

        if (isOpen()) {
            logger.info("open");
            whenCircuitIsOpen(action);
        }

        logger.info("close");
        try {
            action.run();
        } catch (Exception e) {
            trackException(e);
            throw e;
        }
    }

    private void trackException(Throwable t) {
        stateStore.trip(t);
    }

    private boolean isOpen() {
        return !stateStore.isClosed();
    }

    private boolean isClosed() {
        return stateStore.isClosed();
    }

    private void whenCircuitIsOpen(Action action) throws Exception {
        var th = stateStore.getLastStateChangeDate().plusSeconds(openToHalfOpenWaitTimeInSecond);

        //  Open のタイムアウト期間が経過したかチェックする
        if (th.isBefore(LocalDateTime.now())) {

            // 経過していれば HalfOpen に遷移し、コールバックする
            // 成功したら Open に遷移する
            try {
                synchronized (this) {
                    this.stateStore.halfOpen();
                    action.run();
                    stateStore.reset();
                }

            } catch (Exception e) {
                trackException(e);
                throw new CircuitBreakerOpenException(
                        "The circuit was tripped while half-open. Refer to the inner exception for the cause of the trip.",
                        e);
            }
        }

        throw new CircuitBreakerOpenException(
                "The circuit is still open. Refer to the inner exception for the cause of the circuit trip.",
                this.stateStore.getLastException());
    }

    @Override
    public String toString() {
        return String.format("name : %s , state %s\n", name, stateStore.getState()) + String.format(
                "lastException : %s , lastTime", stateStore.getLastException(), stateStore.getLastStateChangeDate());
    }
}
