# SampleGithubClient
## 概要
- githubレポジトリをインクリメンタルサーチで検索可能なGithub client
## 機能
- Github repositoryの検索
- parameterの追加(sort,order)
## 使用言語
- Kotlin
## preview
- https://gyazo.com/76557c595da19628ad65e6d57615e2d8
- https://gyazo.com/c402d22e21dec76a9800adf07d32a38d
## 工夫した点
- scoreの部分を数字だけでなく星の数によって表示することでわかりやすくした
- オプションメニューの選択がしやすいようにした(UI・選択しただけで現在検索しているものがsort/orderされるように工夫した)
## future works
- 結果表示の部分をfragmentに切り出したほうが他画面を追加する時に実装しやすい
- queryの方のオプションメニューは今回追加しなかったので、その部分もUIの方で追加できるようにしたい
