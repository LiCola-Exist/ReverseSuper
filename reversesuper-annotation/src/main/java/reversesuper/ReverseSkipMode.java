package reversesuper;

/**
 * 注解忽略模式
 *
 * @author LiCola
 * @date 2019/4/4
 */
public enum ReverseSkipMode {

  /**
   * 全部模式，针对抽象类和接口全部忽略
   */
  All,

  /**
   * Extend模式，针对抽象类忽略
   */
  Extend,

  /**
   * Impl模式，针对接口忽略
   */
  Impl,
}
