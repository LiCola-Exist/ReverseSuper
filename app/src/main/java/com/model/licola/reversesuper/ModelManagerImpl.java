package com.model.licola.reversesuper;

import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import com.model.licola.annotation.ReverseSuper;

/**
 * Created by LiCola on 2018/3/15.
 * 使用示例，主要针对已经存在的类，rebuild后就可生成对应的接口类。
 * 避免需要手动编写，针对项目重构，抽象等，加快开发。
 */

@ReverseSuper
public class ModelManagerImpl implements ModelManager  {

  @Override

  public String reverseMethod(String input){
    return "被注解反向的方法";
  }

  @Override
  public void reversMethod(@IntRange(from = 0,to = 10) Integer integer){
  }

  @Override
  @StringRes
  public int reversMethod(){
    return android.R.string.ok;
  }


}
