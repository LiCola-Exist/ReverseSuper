package com.model.licola.reversesuper;

import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import java.io.FileNotFoundException;
import java.util.zip.ZipException;
import reversesuper.ReverseExtend;
import reversesuper.ReverseImpl;
import reversesuper.ReverseSkip;
import reversesuper.ReverseSkipMode;

/**
 * 极端情况下，一个目标类被两个注解标记，会同时生成抽象类和接口 针对某些public方法可以针对的标记忽略
 *
 * @author LiCola
 * @date 2018/10/15
 */
@ReverseImpl(interfaceName = "SuperInterface")
@ReverseExtend(superName = "SuperClass")
public class ReverseSuper extends SuperClass implements SuperInterface, Runnable {

  /**
   * 被反向生成抽象方法的 目标方法
   *
   * @param input 输入值
   * @return 固定返回
   */
  @Override
  public final String reverseMethod(String input) {
    return "被反向生成抽象方法的 目标方法";
  }

  /**
   * 被反向生成抽象方法的 目标方法-带参数注解
   *
   * @param integer 带注解的输入范围
   * @return 固定返回值
   */
  @Override
  public synchronized String reversMethod(@IntRange(from = 0, to = 10) Integer integer) {
    //展示 方法参数注解 反向生成的能力
    return "被反向生成抽象方法的 目标方法-带参数注解";
  }

  @Override
  @StringRes
  public int reversMethod() {
    //展示 返回值注解 反向生成的能力
    return android.R.string.ok;
  }

  @Override
  public void reversMethod(String input) throws FileNotFoundException, ZipException {
    throw new FileNotFoundException("");
  }

  /**
   * 实现Runnable接口的方法，但是反向类并不需要生成该方法，可以打上 ReverseSkip注解 表示跳过该方法
   */
  @ReverseSkip
  @Override
  public void run() {

  }

  @Override
  @ReverseSkip(mode = ReverseSkipMode.Extend)
  public void onlyReversImplMethod() {

  }

  @Override
  @ReverseSkip(mode = ReverseSkipMode.Impl)
  public void onlyReversExtendMethod() {

  }

  /**
   * 私有方法不会反向生成
   */
  private void privateMethod() {
    //不会被反向生成的私有方法
  }

  /**
   * 默认访问修饰的方法 不会被反向
   */
  int getView() {
    return 0;
  }


}
