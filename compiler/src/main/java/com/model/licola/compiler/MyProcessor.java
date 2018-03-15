package com.model.licola.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.model.licola.annotation.ReverseSuper;
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
 * 简单示例说明：{@see <a href="http://blog.stablekernel.com/the-10-step-guide-to-annotation-processing-in-android-studio">}
 * 引入{@link <a href="https://github.com/square/javapoet">}
 *
 */
@AutoService(Processor.class) @SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MyProcessor extends AbstractProcessor {

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
        .getElementsAnnotatedWith(ReverseSuper.class);//得到被注解类

    for (Element elementItem :
        setModel) {
      String packName =
          MoreElements.asPackage(MoreElements.asType(elementItem).getEnclosingElement())
              .getQualifiedName()
              .toString();
      TypeSpec typeSpec = buildByElement(elementItem);
      specs.add(new TypeSpecPackage(packName, typeSpec));
    }

    for (TypeSpecPackage item : specs) {
      writeToJavaFile(item.packageName, item.typeSpec);
    }

    return true;
  }

  /**
   * 根据传入的元素 构造
   */
  private TypeSpec buildByElement(Element element) {

    String interfaceName;

    String assignName = element.getAnnotation(ReverseSuper.class).reverseSuperName();//获取注解指定的接口名称
    if (CheckUtils.isEmpty(assignName)) {
      //非指定命名 检查后缀
      String targetClassName = element.getSimpleName().toString();//获取当前标记类名
      String suffix = element.getAnnotation(ReverseSuper.class).defaultSuffixName();
      if (targetClassName.contains(suffix)) {
        interfaceName = targetClassName.replace(suffix, "");//去掉后缀
      } else {
        error(element, "标记的命名不符合规范 应当以%s结尾 或者直接指定生成的接口名", suffix);
        throw new IllegalArgumentException(targetClassName);
      }
    } else {
      //指定命名 直接使用值
      interfaceName = assignName;
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
      classSpecBuild.addMethod(buildInterface(executableElement));
    }

    return classSpecBuild.build();

  }

  private MethodSpec buildInterface(ExecutableElement executableElement) {

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
      variableElement.getSimpleName();

      ParameterSpec parameterSpec = getParameterSpaceByVariable(variableElement);

      methodSpecBuild.addParameter(parameterSpec);
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
