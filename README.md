# Androidアプリ「駅めいど」 サンプル版

## メイドさんと一緒に楽しく学ぶAndroid開発入門セミナー 〜Web API編〜

このアプリは日本Androidの会 学生部と秋葉原支部 共催の「**メイドさんと一緒に楽しく学ぶAndroid開発入門セミナー 〜Web API編〜**」のためのサンプルアプリです。

## Usage

ソースコードに`APIKEY.java`が入っていませんので、そのままではビルドできません。下記ファイルにAPIキーを入れて`app/src/main/java/com/jagsa/ekimaid/example`の中に保存してください。

```java
package com.jagsa.ekimaid.example;

public class APIKEY {
    static String ekispert = "<駅すぱあと Web API のキー>";
}
```

尚、実際にはAPIキーの保管は暗号化などを検討してください。このアプリはあくまでサンプルです。

## Thanks

このアプリでは株式会社ヴァル研究所が無償で提供している「[駅すぱあと Webサービス](https://ekiworld.net/service/sier/webservice/index.html)」を利用させていただいております。

---

Copyright 2016 古川新 \<old.river.new@gmail.com\>

