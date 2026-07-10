# 问墨输入法（Android）

问墨是一款完全离线的中文输入法。本仓库是 Android 平台外壳与跨平台 Rust 输入引擎的起点。

## 隐私约束

- 不声明网络权限。
- 不声明录音权限。
- 不接入遥测、广告、远程配置或在线词库。
- 输入内容不写日志；密码字段不学习。

运行 `sh scripts/check-privacy.sh` 可以检查 Android Manifest 的关键权限。

## 当前 MVP

- 可注册的 Android `InputMethodService`
- 始终显示的收起按钮
- 拼音组合串与 12 万余条离线简繁词语索引
- 简体/繁体切换
- 独立的数字小键盘和常用符号面板
- 输入法启用和切换引导页
- 独立、零依赖的 Rust 引擎原型

## 构建

使用 Android Studio 打开此目录，或在 Gradle Wrapper 可用后运行 `./gradlew assembleDebug`。
Rust 引擎测试：`cargo test --manifest-path engine/Cargo.toml`。

注音解析、词频语言模型、Rust JNI 接入和密码字段策略将在后续里程碑完成。

## 捐助

如果这个项目对你有用的话，请我喝罐可乐吧。
<br>
<img width=30% height=30% src="请我喝可乐.jpg" alt="qrcode">
<br>

## 开源许可证

问墨自行编写的代码和文档采用 [Apache License 2.0](LICENSE)。该许可证允许使用、修改、分发和商业使用，并包含明确的专利授权条款。

第三方词库、语料、成语典故内容及字体不自动适用 Apache License 2.0，具体要求见 [DATA-LICENSES.md](DATA-LICENSES.md)。“问墨”名称和产品标识不因源代码许可而自动获得商标授权。
