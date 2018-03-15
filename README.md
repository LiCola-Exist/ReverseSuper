# ReverseSuper
注解处理器，开发辅助，加快重构。快速生成接口。

# 项目背景
在重构项目时，发现以前有大量类，硬编码直接使用，没有面向接口编程，不利于抽象和单元测试。
面对大量的类，如何快速的提取出抽象方法以及它们的返回值注解/参数注解。

# 项目原理
Java提供注解处理器，在项目编译前期，注解器有机会处理代码。利用这个处理器，我们可以实现对现有类的扫描，然后根据扫描得到的信息，利用工具生成java文件。

# 项目灵感
源自大名鼎鼎的[butterknife](https://github.com/JakeWharton/butterknife/tree/master/butterknife-compiler)对注解处理器的应用。

# 使用帮助
参考app项目下的ModelManagerImpl类。
具体配置参考app的配置，主要由annotation+compiler库实现。
