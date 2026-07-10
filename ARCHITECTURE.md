# 问墨跨平台架构

## 边界

系统外壳保持原生：Android `InputMethodService`、iOS `UIInputViewController`、Windows TSF、macOS InputMethodKit。拼音、注音、候选排序、简繁转换和只读词库统一由 Rust 引擎实现。

平台 UI 只能依赖 `InputEngine` 接口，不能直接依赖词库格式。Android 当前的 `LocalInputEngine` 是用于打通系统生命周期的临时实现，接入 Rust 后由 JNI 适配器替换。

## 数据原则

正式词库使用可复现的数据流水线生成，原始数据必须记录来源、许可证和版本。运行时词库只读；用户学习数据使用独立、本机私有且可清除的存储。

## 隐私原则

网络和录音不是“关闭的功能”，而是不进入制品的能力。各平台构建必须检查权限、entitlement、依赖及链接符号。任何隐私检查失败都应阻止发布。
