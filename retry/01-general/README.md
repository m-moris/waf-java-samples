# 独自に実装した場合のリトライサンプル

リトライロジックを理解するため実装したサンプルですので、ライブラリやフレームワークを利用したリトライをお勧めします。

## 前提

- Java 11 以降

## ビルド

```sh
mvn pakcage
```

## 実行

以下のコマンドで実行でききます。

```sh
mvn exec:java 
```

## サンプル説明

本サンプルはリトライの内部ロジックを理解しやすいように、ライブラやフレームワークを使わずにリトライを実装した例です。呼び出す外部サースとして任意のステータスコードを返却するWEBサービスを利用しています。

4つのパターンをを確認できます。

1. 外部サービスが200を返すパターン。リトライせずに終了します。
2. 外部サービスが500を返すパターン。500はリトライするステータスコードのため、リトライを試行します。
3. 外部サービスが429を返すパターン。上記と同様です。
4. 外部サービスが既定時間ないにレスポンスを返さないため、`HttpTimeoutException`がスローされる例。リトライ対象の例外であるため、リトライを試行します。

### 結果

実行結果をパターン別にまとめます。

外部サービスが200を返すパターン

```log
2021-09-21 20:14:32:068 INFO com.example.retry.App - main start
2021-09-21 20:14:32:072 INFO com.example.retry.App - *** success ***
2021-09-21 20:14:32:632 INFO com.example.retry.RetrySample - retry count : 0
2021-09-21 20:14:33:752 INFO com.example.retry.RetrySample - response : 200
```

外部サービスが500を返すパターン

```log
2021-09-21 20:14:33:758 INFO com.example.retry.App - *** internal server errror ***
2021-09-21 20:14:33:762 INFO com.example.retry.RetrySample - retry count : 0
2021-09-21 20:14:34:438 INFO com.example.retry.RetrySample - response : 500
2021-09-21 20:14:34:443 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:14:37:458 INFO com.example.retry.RetrySample - retry count : 1
2021-09-21 20:14:37:653 INFO com.example.retry.RetrySample - response : 500
2021-09-21 20:14:37:667 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:14:40:678 INFO com.example.retry.RetrySample - retry count : 2
2021-09-21 20:14:40:878 INFO com.example.retry.RetrySample - response : 500
2021-09-21 20:14:40:893 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:14:43:902 INFO com.example.retry.RetrySample - retry count : 3
2021-09-21 20:14:44:089 INFO com.example.retry.RetrySample - response : 500
2021-09-21 20:14:44:095 ERROR com.example.retry.RetrySample - Number of retries exceeded
```

外部サービスが429を返すパターン。500と同様


```log
2021-09-21 20:14:44:100 INFO com.example.retry.App - *** too many request ***
2021-09-21 20:14:44:105 INFO com.example.retry.RetrySample - retry count : 0
2021-09-21 20:14:44:680 INFO com.example.retry.RetrySample - response : 429
2021-09-21 20:14:44:686 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:14:47:691 INFO com.example.retry.RetrySample - retry count : 1
2021-09-21 20:14:47:876 INFO com.example.retry.RetrySample - response : 429
2021-09-21 20:14:47:879 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:14:50:887 INFO com.example.retry.RetrySample - retry count : 2
2021-09-21 20:14:51:078 INFO com.example.retry.RetrySample - response : 429
2021-09-21 20:14:51:089 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:14:54:106 INFO com.example.retry.RetrySample - retry count : 3
2021-09-21 20:14:54:288 INFO com.example.retry.RetrySample - response : 429
2021-09-21 20:14:54:293 ERROR com.example.retry.RetrySample - Number of retries exceeded
```

`HttpTimeoutException` がスローされるパターン。

```log
2021-09-21 20:14:54:296 INFO com.example.retry.App - *** timeout ***
2021-09-21 20:14:54:300 INFO com.example.retry.RetrySample - retry count : 0
2021-09-21 20:14:59:312 WARN com.example.retry.RetrySample - HttpTimeoutException
2021-09-21 20:14:59:327 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:15:02:343 INFO com.example.retry.RetrySample - retry count : 1
2021-09-21 20:15:07:368 WARN com.example.retry.RetrySample - HttpTimeoutException
2021-09-21 20:15:07:375 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:15:10:393 INFO com.example.retry.RetrySample - retry count : 2
2021-09-21 20:15:15:402 WARN com.example.retry.RetrySample - HttpTimeoutException
2021-09-21 20:15:15:407 INFO com.example.retry.RetrySample - waiting....
2021-09-21 20:15:18:420 INFO com.example.retry.RetrySample - retry count : 3
2021-09-21 20:15:23:439 WARN com.example.retry.RetrySample - HttpTimeoutException
2021-09-21 20:15:23:445 ERROR com.example.retry.RetrySample - Number of retries exceeded
2021-09-21 20:15:23:448 INFO com.example.retry.App - main end
```

### サンプル説明詳細

リトライ回数は定数として宣言されています。変更するとリトライ回数が変化します。

```java
    private static final int MAX_RETRY_COUNT = 3;
```

ステータスコードによるリトライの判定。 200番台は成功、500、503、429 はリトライ対象、それ以外は失敗と判定します。

```java
                if (code >= 200 && code <= 299) {
                    return true;
                }
                // リトライすべきステータスコードかチェック
                if ((code == 500 || code == 503 || code == 429) == false) {
                    return false;
                }
```

リトライする例外の判定。`HttpTimeoutException` をリトライ対象にしています。それ以外は失敗と判定します。

```java
            } catch (HttpTimeoutException e) {
                // タイムアウトの例外はリトライ対象
                logger.warn("HttpTimeoutException");
            } catch (IOException | InterruptedException e) {
                // それ以外の例外は失敗
                logger.error("Exception", e);
                return false;
            }
```
リトライ間隔は一定です。`retryCount * RETRY_INTERVAL` に変更すると、段階的に間隔が延びていきます。

```java
    private static final int RETRY_INTERVAL  = 3;

    ...

            try {
                logger.info("waiting....");
                TimeUnit.SECONDS.sleep(RETRY_INTERVAL);
            } catch (InterruptedException e) {
                return false;
            }
```

以上


