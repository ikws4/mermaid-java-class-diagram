package io.github.ikws4.mermaidjavaclassdiagram;

import java.util.Set;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

class ClassDiagramRelationVisitor extends VoidVisitorWithDefaults<String> {
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
      // className = parentNode.getNameAsString() + className;
    }

    super.visit(node, prefix + className);
  }

  @Override
  public void visit(FieldDeclaration node, String prefix) {
    String type = node.getElementType().asString();

    if ((type.contains("List") || type.contains("Set")) && type.contains("<")) {
      type = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
      if (classNames.contains(type)) {
        println(prefix, " --> " + convertGenericType(type) + " : has many");
      }
    } else {
      if (classNames.contains(type)) {
        println(prefix, " --> " + convertGenericType(type) + " : has");
      }
    }

    super.visit(node, prefix);
  }

  @Override
  public void defaultAction(Node node, String prefix) {
    for (Node child : node.getChildNodes()) {
      child.accept(this, prefix);
    }
  }

  @Override
  public void defaultAction(NodeList node, String prefix) {
    for (int i = 0; i < node.size(); i++) {
      node.get(i).accept(this, prefix);
    }
  }

  private void println(String prefix, String line) {
    System.out.println(prefix + line);
  }

  // private String withoutGenerics(String type) {
  //   if (type.contains("<")) {
  //     return type.substring(0, type.indexOf("<"));
  //   } else {
  //     return type;
  //   }
  // }

  private String convertGenericType(String type) {
    char[] chars = type.toCharArray();
    boolean found = false;
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == '<') {
        if (found) {
          builder.append("<𝅵");
        } else {
          found = true;
          builder.append('~');
        }
      } else {
        builder.append(chars[i]);
      }
    }
    for (int i = builder.length() - 1; i >= 0; i--) {
      if (builder.charAt(i) == '>') {
        builder.setCharAt(i, '~');
        break;
      }
    }
    return builder.toString();
  }
}
