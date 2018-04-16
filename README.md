# [ReverseSuper](https://github.com/LiCola/ReverseSuper)

[ ![Download](https://user-gold-cdn.xitu.io/2018/4/16/162cd11d46d81f57) ](https://bintray.com/licola/maven/ReverseSuper-annotation/_latestVersion) 
[ ![Download](https://user-gold-cdn.xitu.io/2018/4/16/162cd11d539ebe64) ](https://bintray.com/licola/maven/ReverseSuper-compiler/_latestVersion)

# 引用

```java
    implementation 'com.licola:reversesuper-annotation:1.0.0'//注解库
    annotationProcessor 'com.licola:reversesuper-compiler:1.0.0'//代码生成工具库
```

# 使用
```java
/**
 * Created by LiCola on 2018/3/15.
 * 使用示例，主要针对已经存在的类，rebuild后就可生成对应的接口类。
 * 避免需要手动编写，针对项目重构，抽象等，加快开发。
 * 其中AccountManager接口类是动态生成，它抽象目标类的public方法
 */
@ReverseSuper
public class AccountManagerImpl implements AccountManager{
    @Override
    public String reverseMethod(String input) {
      return "被反向生成抽象方法的 目标方法";
    }
    
    private String value = "不会被处理非方法信息-变量";

    private void privateMethod() {
    //不会被反向生成的私有方法
    }
}
```
当```ReverseSuper```注解在目标类上，点击Build-Rebuild，就会动态生成对应的接口类。

# 项目背景
在项目重构时，当面的硬编码实现的api类，需要把具体类的方法抽象出来，新建接口类加入抽象方法，然后才能加入单元测试，或实现装饰者模式等处理。但是如果面对大量的api类，手动是很繁琐而且非常不自动化的。
面对大量的类，如何快速的提取出抽象方法以及它们的返回值注解/参数注解。
关于命名Reverse反向，一般的编码是从接口（上层）->实现类（下层）。但是重构时面对实现类（下层），反而需要更层的接口类，所以就是反向。

# 项目原理
Java提供注解处理器，在项目编译前期，注解器有机会处理代码。利用这个处理器，我们可以实现对现有类的扫描，然后根据扫描得到的信息，利用工具生成java文件。

# 说明
下面说明一些细节问题
## 关于命名
参照阿里巴巴Java开发手册-编程规约-命名风格。
接口和实现类的命名规则：
- 【强制】对于 Service 和 DAO 类，基于 SOA 的理念，暴露出来的服务一定是接口，内部
的实现类用 Impl 的后缀与接口区别。
正例： CacheServiceImpl 实现 CacheService 接口
- 【推荐】如果是形容能力的接口名称，取对应的形容词做接口名 （ 通常是–able 的形式）。
正例： AbstractTranslator 实现 Translatable。

这里有两套规则，对应的我的在注解中有两个可选项。其中默认实现【强制】的Impl命名风格。
```java
 /**
   * 被标注类的名称后缀 默认是命名是 Impl
   * 可以根据实际修改，必须是被标记类的后缀。
   * 默认规则 如：AccountMangerImpl->AccountManager
   */
  String suffixName() default Suffix;

  /**
   * 默认的接口实现类后缀
   */
  String Suffix = "Impl";
  
  /**
   * 指定 生成接口类的名称
   * 默认为空：裁剪约定后缀的标记类的 {@link #suffixName}
   * 非空输入：生成指定名称的接口 忽略后缀检查
   * 如：AbstractTranslator->Translatable
   */
  String superName() default "";
```

## 关于生成的java文件
当rebuild项目时，会在app-build-generated-sourceapt文件下，生成目标类的同名包以及动态生成的接口类。最终在apk打包过程中源码.java文件和apt下的.java文件会合并打包。在我们继承接口时即```implements AccountManager```会认为是导入同名包的下的代码，不会有import语句。

## 辅助重构-最终弃用

重构是一个渐进的过程，从最初的实现类反向生成接口类。刚开始可能没有大面积的重构或者抽象能力不足，接口类随时会修改。动态的反向可以带来便利。只要修改实现类方法参数/返回值以及它们的注解，rebuild就会马上生成接口。一次修改（否则就要修改接口类和实现类的方法，两次修改）。

当重构完成或者更高层抽象分离出来，我们的接口类最终确定，就不需要build项目时动态生成反向接口类，每次build动态生成反而可能拖慢了项目的编译时间。这时就可以从动态文件包app-build-generated-sourceapt中复制出接口类放到适合的包。从而弃用```@ReverseSuper```注解。完成它辅助重构的使命。

# 项目灵感
源自大名鼎鼎的[butterknife](https://github.com/JakeWharton/butterknife/tree/master/butterknife-compiler)对注解处理器的应用。



