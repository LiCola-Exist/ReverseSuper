package reversesuper.generator;


import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by LiCola on 2018/6/6.
 */
public class ReverseGenerator {

  public static void main(Class[] classList, String outPath) throws IOException {

    if (CheckUtils.isEmpty(classList)) {
      throw new IllegalArgumentException("请输入需要生成的目标类");
    }

    List<TypeSpecPackage> specList = new ArrayList<>(classList.length);

    for (Class targetClass : classList) {
      String packageName=targetClass.getPackage().getName();
      specList.add(new TypeSpecPackage(packageName,buildSuperElement(targetClass)));
    }

//    for (TypeSpecPackage item : specList) {
//      JavaFile javaFile = JavaFile.builder(item.getPackageName(), item.getTypeSpec())
//          .build();
//      javaFile.writeTo(new File(outPath));
//    }

//    MethodSpec main = MethodSpec.methodBuilder("main")
//        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//        .returns(void.class)
//        .addParameter(String[].class, "args")
//        .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
//        .build();
//
//    TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
//        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//        .addMethod(main)
//        .build();
//
//    JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
//        .build();
//
//    javaFile.writeTo(System.out);

  }

  private static TypeSpec buildSuperElement(Class targetClass) {

    Annotation[] annotations = targetClass.getDeclaredAnnotations();

    if (CheckUtils.isEmpty(annotations)){
      throw new IllegalArgumentException(String.format(Locale.CHINA,"%s类缺少@SuperseImlp/@SuperExtend注解",targetClass));
    }

    return null;
  }
}
