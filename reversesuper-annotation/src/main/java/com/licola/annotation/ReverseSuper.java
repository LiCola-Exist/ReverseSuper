package com.licola.annotation;

/**
 * Created by LiCola on 2018/3/15.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 反向生成父类接口类注释
 * 被标记的类在编译时，会在build目录下的同级包 生成接口类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ReverseSuper {

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

  /**
   * 被标注类的名称后缀 默认是命名是 Impl
   * 可以根据实际修改，必须是被标记类的后缀。
   * 默认规则 如：AccountMangerImpl->AccountManager
   */
  String suffixName() default Suffix;
}
