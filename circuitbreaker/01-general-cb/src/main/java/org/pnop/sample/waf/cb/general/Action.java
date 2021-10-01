package org.pnop.sample.waf.cb.general;

@FunctionalInterface
public interface Action {
    void run() throws Exception;
}
