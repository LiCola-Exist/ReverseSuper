package com.licola.compiler;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by LiCola on 2017/6/21.
 */

public class TypeSpecPackage {

  public String packageName;
  public TypeSpec typeSpec;

  public TypeSpecPackage(String packageName, TypeSpec typeSpec) {
    this.packageName = packageName;
    this.typeSpec = typeSpec;
  }
}
