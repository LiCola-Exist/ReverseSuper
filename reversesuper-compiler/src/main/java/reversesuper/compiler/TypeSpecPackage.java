package reversesuper.compiler;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by LiCola on 2017/6/21.
 */

public class TypeSpecPackage {

  private String packageName;
  private TypeSpec typeSpec;

  public TypeSpecPackage(String packageName, TypeSpec typeSpec) {
    this.packageName = packageName;
    this.typeSpec = typeSpec;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public TypeSpec getTypeSpec() {
    return typeSpec;
  }

  public void setTypeSpec(TypeSpec typeSpec) {
    this.typeSpec = typeSpec;
  }
}
