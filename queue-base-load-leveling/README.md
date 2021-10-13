

## 手動での実行方法




## Docker Compose 利用しての実行方法

## Function の Docker Image

docker build ./ -t waf-sample/consumer-function

## ストレージエミュレータの設定と起動

[ローカルでの Azure Storage の開発に Azurite エミュレーターを使用する | Microsoft Docs](https://docs.microsoft.com/ja-jp/azure/storage/common/storage-use-azurite?tabs=visual-studio)

```
docker run -d -p 10000:10000 -p 10001:10001 mcr.microsoft.com/azure-storage/azurite
```