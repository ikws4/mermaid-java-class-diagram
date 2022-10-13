package io.github.ikws4.mermaidjavaclassdiagram;

import java.util.Set;
import java.util.stream.Collectors;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

class ClassDiagramDefineVisitor extends BaseVisitor<String> {
  private Set<String> classNames;
  private int classDepth = 0;
  private boolean insideInterface = false;

  ClassDiagramDefineVisitor(Set<String> classNames) {
    this.classNames = classNames;
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration node, String indent) {
    classDepth++;

    String className = NodeUtil.getClassName(node);
    classNames.add(className);

    if (classDepth > 1) {
      indent = "  ";
      System.out.println(indent);
    }

    System.out.println(indent + "class " + className);

    if (node.isInterface()) {
      insideInterface = true;
      System.out.println(indent + "  <<interface>> " + className);
    } else if (node.isEnumDeclaration()) {
      System.out.println(indent + "  <<enum>> " + className);
    } else if (node.isAbstract()) {
      System.out.println(indent + "  <<abstract>> " + className);
    }

    super.visit(node, indent + "  " + className + " : ");

    classDepth--;
    insideInterface = false;
  }

  @Override
  public void visit(FieldDeclaration node, String indent) {
    if (Main.printField) {
      String visibility = NodeUtil.getVisibilityChar(node);
      if (insideInterface && visibility == "~") visibility = "+";

      if ((Main.onlyPrintPublic && visibility == "+") || !Main.onlyPrintPublic) {
        if (visibility == "+") {
          String type = NodeUtil.convertGenericType(node.getElementType());
          String varName = node.getVariable(0).getNameAsString();

          System.out.println(indent + visibility + type + " " + varName);
        }
      }
    }
    super.visit(node, indent);
  }

  @Override
  public void visit(MethodDeclaration node, String indent) {
    if (Main.printMethod) {
      String visibility = NodeUtil.getVisibilityChar(node);
      if (insideInterface && visibility == "~") visibility = "+";
      if ((Main.onlyPrintPublic && visibility == "+") || !Main.onlyPrintPublic) {
        String methodName = node.getNameAsString();
        String returnType = NodeUtil.convertGenericType(node.getType());
        String args = node.getParameters()
          .stream()
          .map(p -> NodeUtil.convertGenericType(p.getType()) + " " + p.getNameAsString())
          .collect(Collectors.joining(", "));

        System.out.println(indent + visibility + methodName + "(" + args + ") " + returnType);
      }
    }
    super.visit(node, indent);
  }
}
