package org.pnop.sample.waf.cb.general;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

    public static void main(String[] args) {

        // 外部サービス呼び出しに見立てたコールバック関数、BOOL値を受け取り例外をスローするか判定する
        Action<Boolean> action = (throwException) -> {
            if (throwException) {
                logger.info("Action is failed.");
                throw new IOException();
            }
            logger.info("Action is successul.");
            return;
        };

        // サーキットブレーカーの作成。4回失敗でOPENになり、5秒経過するとOPENからHALF_OPEN に遷移する
        var cb = new CircuitBreaker("test", 4, 5);

        // 成功、失敗を交互に繰り返し、最終的に、サーキットブレーカーはOPENになる
        for (int i = 0; i < 8; i++) {
            try {
                cb.invoke(i % 2 == 0, action);
            } catch (Exception e) {
            }
            logger.info(cb.toString());
        }

        // 5秒以上経過させ、HALF_OPEN にする
        logger.info("-------------- Waiting ---------------");
        sleep(Duration.ofSeconds(10));

        // HALF_OPEN 状態で失敗すると、すぐにOPENに遷移する
        for (int i = 0; i < 3; i++) {
            try {
                cb.invoke(true, action);
            } catch (Exception e) {
            }
            logger.info(cb.toString());
        }
        
        // 5秒以上経過させ、HALF_OPEN にする
        logger.info("-------------- Waiting ---------------");
        sleep(Duration.ofSeconds(10));

        // 連続して成功すると、HALF_OPEN から CLOSEDに遷移する
        for (int i = 0; i < 5; i++) {
            try {
                cb.invoke(false, action);
            } catch (Exception e) {
            }
            logger.info(cb.toString());
        }
        logger.info("end");
    }

    private static void sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException e) {
        }
    }
}
