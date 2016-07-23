# Watson Developer Cloud Tools
## What is this tools
IBM BlueMix で利用可能な Watson API の開発のための Tool です．Dialog Service のルールエディタを提供しています．

## Installation
Eclipse Plugin として開発しています．
以下のリンク先から jar ファイルをダウンロードして，Eclipse の dropins ディレクトリにコピーしてください．Eclipse を再起動すればインストールは完了です．

released jar file(Preview version) : [watson.dialog.tools_1.0.0.201607231834.jar](http://takahiro-joukou.github.io/WatsonDeveloperCloudTools/documents/jars/watson.dialog.tools_1.0.0.201607231834.jar "download plugin jars")

## How to use it
ファイルメニューから [新規] > [その他] を選び，新規ファイル作成ウィザードから [Watson] > [Watson Dialog Service] で新規ファイルを作成します．
または既存のファイルを，Watson Dialog Service Editor で開いてください．
GUI で，Dialog サービスのルールを定義した xml が編集できます．

![Dialog Edit Page](http://takahiro-joukou.github.io/WatsonDeveloperCloudTools/documents/images/dialog_edit_page.jpg)

編集したルールファイルを実行するには，[ウィンドウ] > [設定] から，BlueMix 上の Watson の認証情報をに設定してください．

![Dialog Preference](http://takahiro-joukou.github.io/WatsonDeveloperCloudTools/documents/images/dialog_preference.jpg)

[Create]ボタンをクリックすると Dialog のインスタンスを作成することができます．[New Conversation]ボタンをクリックすると，Dialog サービスを実行できます．
実行した履歴は，[Show History]で表示できます．

![Dialog Run Page](http://takahiro-joukou.github.io/WatsonDeveloperCloudTools/documents/images/dialog_run_page.jpg)

## Disclaimer
ツールは，プレビュー版として提供しています．BugFix や機能拡張は順次，対応します．
