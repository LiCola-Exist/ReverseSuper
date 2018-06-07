package reversesuper.compiler;

import com.squareup.javapoet.TypeSpec;
import java.io.File;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

/**
 * Created by LiCola on 2017/6/21.
 */

public class TypeSpecPackage {

  private PackageElement packageElement;
  private TypeSpec typeSpec;
  private Filer buildFiler;
  private File srcFile;

  public static TypeSpecPackage createBySrc(PackageElement packageElement, TypeSpec typeSpec, File srcFile){
    return new TypeSpecPackage(packageElement,typeSpec,null,srcFile);
  }

  public static TypeSpecPackage createByBuild(PackageElement packageElement, TypeSpec typeSpec, Filer buildFiler){
    return new TypeSpecPackage(packageElement,typeSpec,buildFiler,null);
  }

  public TypeSpecPackage(PackageElement packageElement, TypeSpec typeSpec,
      Filer buildFiler, File srcFile) {
    this.packageElement = packageElement;
    this.typeSpec = typeSpec;
    this.buildFiler = buildFiler;
    this.srcFile = srcFile;
  }

  public PackageElement getPackageElement() {
    return packageElement;
  }

  public void setPackageElement(PackageElement packageElement) {
    this.packageElement = packageElement;
  }

  public TypeSpec getTypeSpec() {
    return typeSpec;
  }

  public void setTypeSpec(TypeSpec typeSpec) {
    this.typeSpec = typeSpec;
  }

  public Filer getBuildFiler() {
    return buildFiler;
  }

  public void setBuildFiler(Filer buildFiler) {
    this.buildFiler = buildFiler;
  }

  public File getSrcFile() {
    return srcFile;
  }

  public void setSrcFile(File srcFile) {
    this.srcFile = srcFile;
  }
}
