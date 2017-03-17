# Androidアプリ「駅めいど」 サンプル版

## メイドさんと一緒に楽しく学ぶAndroid開発入門セミナー 〜Web API編〜

このアプリは日本Androidの会 学生部と秋葉原支部 共催の「**メイドさんと一緒に楽しく学ぶAndroid開発入門セミナー 〜Web API編〜**」のためのサンプルアプリです。

当日の資料は[こちら](https://docs.google.com/presentation/d/1hsmdsuS6nvzacyOLXdzfsV_7ohI0Bmm9eA9SqqYhB7k/edit?usp=sharing)になります。

利用させていただいているAPIの仕様については、公式資料をご参照ください。

- [駅すぱあとAPI - station/light](http://docs.ekispert.com/v1/api/station/light.html)
- [駅すぱあとAPI - station](http://docs.ekispert.com/v1/api/station.html)
- [Google Place API - places/web-service/search](https://developers.google.com/places/web-service/search)

## Usage

ソースコードに`APIKEY.java`が入っていませんので、そのままではビルドできません。下記ファイルにAPIキーを入れて`app/src/main/java/com/jagsa/ekimaid/example`の中に保存してください。

```java
package com.jagsa.ekimaid.example;

public class APIKEY {
    static String ekispert = "<駅すぱあと Web API のキー>";
    static String googlePlace = "<Placeを許可したGoogle APIキー>";
}
```

尚、実際にはAPIキーの保管は暗号化などを検討してください。このアプリはあくまでサンプルです。

## Thanks

このアプリでは株式会社ヴァル研究所が無償で提供している「[駅すぱあと Webサービス](https://ekiworld.net/service/sier/webservice/index.html)」を利用させていただいております。

---

Copyright 2016 古川新 \<old.river.new@gmail.com\>

