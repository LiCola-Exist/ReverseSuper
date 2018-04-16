package com.licola.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.licola.annotation.ReverseSuper;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic.Kind;

/**
 * Created by LiCola on 2017/6/21.
 *
 * 注解生成代码：生成目录在项目build包下，和目标类同级
 *
 * 简单示例说明：{@link <a href="http://blog.stablekernel.com/the-10-step-guide-to-annotation-processing-in-android-studio">}
 * 引入{@link <a href="https://github.com/square/javapoet">}
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ReverseSuperProcessor extends AbstractProcessor {

  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    filer = processingEnv.getFiler();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
      types.add(annotation.getCanonicalName());
    }
    return types;
  }

  private Set<Class<? extends Annotation>> getSupportedAnnotations() {
    Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
    annotations.add(ReverseSuper.class);
    return annotations;
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

    List<TypeSpecPackage> specs = new ArrayList<>();

    Set<? extends Element> setModel = roundEnvironment
        .getElementsAnnotatedWith(ReverseSuper.class);//得到被注解目标类们

    //遍历被注解的目标类 获取信息
    for (Element elementItem : setModel) {
      //获取包名
      String packName = MoreElements
          .asPackage(MoreElements.asType(elementItem).getEnclosingElement())
          .getQualifiedName()
          .toString();
      //构造接口
      TypeSpec typeSpec = buildByElement(elementItem, packName);
      specs.add(new TypeSpecPackage(packName, typeSpec));
    }

    //依次写反向接口
    for (TypeSpecPackage item : specs) {
      writeToJavaFile(item.packageName, item.typeSpec);
    }

    return true;
  }

  /**
   * 根据传入的元素 构造接口类
   */
  private TypeSpec buildByElement(Element element, String packName) {

    String interfaceName;

    String superName = element.getAnnotation(ReverseSuper.class).superName();//获取注解指定的接口名称
    if (CheckUtils.isEmpty(superName)) {
      //没有指定命名 检查后缀 裁剪目标类名
      interfaceName = getSuperNameByTargetClass(element, packName);
    } else {
      //指定命名 直接使用值
      interfaceName = superName;
    }

    TypeSpec.Builder classSpecBuild =
        TypeSpec.interfaceBuilder(interfaceName).addModifiers(Modifier.PUBLIC);

    //得到类内部全部定义元素（包括 域、静态方法、对象方法等）
    List<? extends Element> elementMethods = MoreElements.asType(element).getEnclosedElements();

    for (Element elementItem : elementMethods) {
      if (elementItem.getKind() != ElementKind.METHOD) {
        //跳过非方法
        continue;
      }

      ExecutableElement executableElement = MoreElements.asExecutable(elementItem);
      Set<Modifier> modifiers = executableElement.getModifiers();

      if (modifiers.contains(Modifier.STATIC)) {
        //修饰符中 包含static 即静态方法 跳过
        continue;
      }

      if (modifiers.contains(Modifier.PRIVATE)) {
        //私有方法 跳过
        continue;
      }

      //添加接口抽象方法
      classSpecBuild.addMethod(buildInterfaceMethod(executableElement));
    }

    return classSpecBuild.build();

  }

  private String getSuperNameByTargetClass(Element element, String packName) {
    String targetClassName = element.getSimpleName().toString();//获取当前标记类名
    String suffix = element.getAnnotation(ReverseSuper.class).suffixName();
    int suffixLength = suffix.length();
    int targetClassNameLength = targetClassName.length();

    if (targetClassNameLength == suffixLength) {
      error(element, "ReverseSuper 命名不符合规范 %s.%s类名称太短无法得到有效名称", packName, targetClassName);
      throw new IllegalArgumentException(targetClassName);
    }

    String targetClassSuffix = targetClassName.substring(targetClassNameLength - suffixLength);
    if (!targetClassSuffix.equals(suffix)) {
      error(element, "ReverseSuper 命名不符合规范 %s.%s类应当以%s结尾", packName, targetClassName, suffix);
      throw new IllegalArgumentException(targetClassName);
    }

    return targetClassName.substring(0, targetClassNameLength - suffixLength);
  }

  private MethodSpec buildInterfaceMethod(ExecutableElement executableElement) {

    MethodSpec.Builder methodSpecBuild =
        MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

    //添加方法返回值的 注解
    for (AnnotationMirror item : executableElement.getAnnotationMirrors()) {
      TypeElement typeElement = MoreElements.asType(item.getAnnotationType().asElement());
      //跳过实现类的Overrider重写注解
      if (Override.class.getSimpleName().equals(typeElement.getSimpleName().toString())) {
        continue;
      }
      methodSpecBuild.addAnnotation(AnnotationSpec.get(item));
    }

    //检查是否 返回值为void的方法
    if (executableElement.getReturnType().getKind() == TypeKind.VOID) {
      methodSpecBuild.returns(void.class);
    } else {
      methodSpecBuild.returns(ClassName.get(executableElement.getReturnType()));
    }

    //添加方法参数 注解
    for (VariableElement variableElement : executableElement.getParameters()) {
      methodSpecBuild.addParameter(getParameterSpaceByVariable(variableElement));
    }

    return methodSpecBuild.build();
  }


  /**
   * 根据变量元素 构建参数 包括修饰符和参数注解
   */
  private ParameterSpec getParameterSpaceByVariable(VariableElement element) {
    TypeName type = TypeName.get(element.asType());
    String name = element.getSimpleName().toString();

    ParameterSpec.Builder builder =
        ParameterSpec.builder(type, name).addModifiers(element.getModifiers());

    List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

    //复制参数的注解
    for (AnnotationMirror itemAnnotation : annotationMirrors) {
      builder.addAnnotation(AnnotationSpec.get(itemAnnotation));
    }

    return builder.build();
  }

  private void error(Element element, String message, Object... args) {
    printMessage(Kind.ERROR, element, message, args);
  }

  private void printMessage(Kind kind, Element element, String message, Object[] args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }

    processingEnv.getMessager().printMessage(kind, message, element);
  }

  private void writeToJavaFile(String packageName, TypeSpec typeSpec) {
    if (typeSpec == null) {
      return;
    }
    JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
