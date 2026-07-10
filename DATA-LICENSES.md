# 词库及数据许可证

仓库根目录的 Apache License 2.0 适用于问墨自行编写的源代码和项目文档，但不自动授予第三方词典、语料、成语释义、典故原文、例句、字体或其他数据内容的使用权。

任何数据进入 `data/source` 前，必须登记：

- 数据集名称和版本
- 原始来源及获取日期
- 原作者或权利人
- 原始许可证全文或永久链接
- 是否允许修改、再分发和商业使用
- 必须保留的署名及 NOTICE 内容
- 数据清洗和派生过程

来源不明、许可证缺失、禁止商业使用或禁止再分发的数据，不得进入发布词库。最终安装包应随附所有适用的第三方许可证和署名。

## CC-CEDICT

- 内容：简体词形、繁体词形、汉语拼音及英文释义
- 项目：https://cc-cedict.org/
- 本项目获取日期：2026-07-10
- 许可证：Creative Commons Attribution-ShareAlike 4.0 International
- 用途：生成问墨的离线拼音到简繁词形索引
- 派生数据：`app/src/main/assets/cedict_pinyin.tsv`

CC-CEDICT 及其派生词库数据不适用问墨代码的 Apache License 2.0，而继续依据 CC BY-SA 4.0 提供。问墨未将英文释义收入输入索引。

## Unicode Unihan

- 内容：汉字属性；当前使用 `kGradeLevel` 将基础教育常用字优先展示
- 项目：https://www.unicode.org/reports/tr38/
- 本项目获取日期：2026-07-10
- 许可证：Unicode License v3
- 许可证副本：`data/source/UNICODE-LICENSE.txt`

Unihan 数据只参与可复现的索引编译流程。其许可证及版权声明必须随数据和衍生制品保留。
