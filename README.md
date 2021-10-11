# Microsoft Azure Well-Architected Framework Samples for Java

Microsoft Azure Well-Architected Framework に基づいたクラウドデザインパターンの実勢 Java編のサンプル集です。

セミナーの内容に基づいて以下の実行可能なサンプルを提供しています。

| フォルダ   | サンプル内容 |
|---|---|
| [retry](./retry/README.md) | リトライ（再試行）パターン |
| [circuitbreaker](./circuitbreaker/README.md) |  サーキットブレーカー パターン|
| healthendpoint |  正常性エンドポイントの監視パターン |
| queue | キュー ベースの負荷平準化パターン |


## 前提条件

実行するにあたって以下の前提条件があります。サンプルの実行方法は、個々の `README.md` に書かれています。IDE上やコマンドラインから任意の方法で実行可能です。

- Java 11 以降の JDK
- Azure Storge Emulator (予定)
- ...
  
## 利用する外部サービス

任意のステータスコードを返す Web サービスを、「外部サービス」として見立てて利用しているサンプルがあります。この Web サービスでは、リクエストに応じたHTTP ステータスコードを返却したり、遅延したレスポンスを返却することができます。

* [httpbin.org](http://httpbin.org/)

Docker Image が公開されていますので、ローカルにテストで利用することもできます。

## 確認環境

**要書換え**

このサンプルは以下の環境で確認しました。

- Ubuntu20.04 on Windows11 WSL2
- OpenJDK Runtime Environment Microsoft-22268 (build 11.0.11+9)
- Apache Maven 3.8.1
- Eclipse IDE for Java Developers (2021-3)
- Visual Studio Code 1.60.0 


## 免責

## ライセンス

TODO

