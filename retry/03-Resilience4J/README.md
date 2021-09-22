# Resilience4j のリトライサンプル

## 前提

- Java 11 以降

## ビルド

以下のコマンドでビルドします。

```sh
mvn clean pakcage
```

## 実行

以下のコマンドで実行します。

```sh
mvn exec:java 
```

## 依存ライブラリ

```xml
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-retry</artifactId>
            <version>1.7.1</version>
        </dependency>
```


## 参考リンク

* [resilience4j/resilience4j: Resilience4j is a fault tolerance library designed for Java8 and functional programming](https://github.com/resilience4j/resilience4j)
* [Resilience4j Retry](https://resilience4j.readme.io/docs/retry)