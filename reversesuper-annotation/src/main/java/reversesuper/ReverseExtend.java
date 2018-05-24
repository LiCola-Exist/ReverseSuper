package reversesuper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LiCola on 2018/5/24.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ReverseExtend {


  /**
   * 生成抽象类名的前缀 默认是Abstract
   * 默认规则：Adapter(标记类)->AbstractAdapter(生成的抽象类)
   * 也可以传入参数指定生成的抽象类名的前缀
   * 如：传入Base,Adapter(标记类)->BaseAdapter(生成的抽象类)
   */
  String superPrefix() default "Abstract";

  /**
   * 指定 生成的抽象类名称
   * 默认：默认该字段不作用，通过{@link #superName()}直接拼接标记类和前缀生成抽象类
   * 非空输入：指定生成的抽象类名称
   * 如：传入BaseAdapter, MyAdapter(标记类)->BaseAdapter(生成的抽象类)
   */
  String superName() default "";


}
