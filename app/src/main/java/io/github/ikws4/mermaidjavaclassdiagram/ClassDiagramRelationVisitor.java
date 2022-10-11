package io.github.ikws4.mermaidjavaclassdiagram;

import java.util.Set;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

class ClassDiagramRelationVisitor extends BaseVisitor<String> {
  private Set<String> classNames;

  ClassDiagramRelationVisitor(Set<String> classNames) {
    this.classNames = classNames;
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration node, String prefix) {
    String className = node.getNameAsString();

    if (node.isNestedType()) {
      ClassOrInterfaceDeclaration parentNode =
          (ClassOrInterfaceDeclaration) node.getParentNode().get();
      System.out.println(className + " ..> " + parentNode.getNameAsString() + " : inside");
    }

    super.visit(node, prefix + className);
  }

  @Override
  public void visit(FieldDeclaration node, String prefix) {
    String type = node.getElementType().asString();

    if ((type.contains("List") || type.contains("Set")) && type.contains("<")) {
      type = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
      if (classNames.contains(type)) {
        System.out.println(prefix + " --> " + NodeUtil.convertGenericType(type) + " : has many");
      }
    } else {
      if (classNames.contains(type)) {
        System.out.println(prefix + " --> " + NodeUtil.convertGenericType(type) + " : has");
      }
    }

    super.visit(node, prefix);
  }
}
