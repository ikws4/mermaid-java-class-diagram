package io.github.ikws4.mermaidjavaclassdiagram;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.Type;

class ClassDiagramExtendAndImplVisitor extends BaseVisitor<Void> {
  @Override
  public void visit(ClassOrInterfaceDeclaration node, Void arg) {
    String className = NodeUtil.getClassName(node);

    for (Type type : node.getExtendedTypes()) {
      String typeName = NodeUtil.convertGenericType(type);
      System.out.println(className + " --|> " + typeName + " : extends ");
    }

    for (Type type : node.getImplementedTypes()) {
      String typeName = NodeUtil.convertGenericType(type);
      System.out.println(className + " --|> " + typeName + " : implements ");
    }
    
    super.visit(node, arg);
  }
}
