package com.model.licola.reversesuper;

import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import reversesuper.ReverseImpl;

/**
 * Created by LiCola on 2018/3/15.
 * 使用示例，主要针对已经存在的类，rebuild后就可生成对应的接口类。
 * 避免需要手动编写，针对项目重构，抽象等，加快开发。
 * 其中AccountManager接口类是动态生成，它抽象目标类的public方法
 */
@ReverseImpl
public class AccountManagerImpl implements AccountManager {

  /**
   * 被反向生成抽象方法的 目标方法
   *
   * @param input 输入值
   * @return 固定返回
   */
  @Override
  public String reverseMethod(String input) {
    return "被反向生成抽象方法的 目标方法";
  }

  /**
   * 被反向生成抽象方法的 目标方法-带参数注解
   *
   * @param integer 带注解的输入范围
   * @return 固定返回值
   */
  @Override
  public String reversMethod(@IntRange(from = 0, to = 10) Integer integer) {
    //展示 方法参数注解 反向生成的能力
    return "被反向生成抽象方法的 目标方法-带参数注解";
  }

  @Override
  @StringRes
  public int reversMethod() {
    //展示 返回值注解 反向生成的能力
    return android.R.string.ok;
  }

  private String value = "不会被处理非方法信息 变量";

  private void privateMethod() {
    //不会被反向生成的私有方法
  }

}
