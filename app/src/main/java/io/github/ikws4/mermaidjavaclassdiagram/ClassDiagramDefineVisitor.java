package io.github.ikws4.mermaidjavaclassdiagram;

import java.util.Set;
import java.util.stream.Collectors;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

class ClassDiagramDefineVisitor extends VoidVisitorWithDefaults<String> {
  private Set<String> classNames;
  private int classDepth = 0;
  
  ClassDiagramDefineVisitor(Set<String> classNames) {
    this.classNames = classNames;
  }
  
  @Override
  public void visit(ClassOrInterfaceDeclaration node, String indent) {
    classDepth++;

    String className = node.getNameAsString();
    if (node.getTypeParameters().size() > 0) {
      className += "<";
      for (int i = 0; i < node.getTypeParameters().size(); i++) {
        if (i < node.getTypeParameters().size() - 1) {
          className += node.getTypeParameter(i).getNameAsString() + ", ";
        } else {
          className += node.getTypeParameter(i).getNameAsString();
        }
      }
      className += ">";
      classNames.add(className);
      className = convertGenericType(className);
    } else {
      classNames.add(className);
    }

    
    if (classDepth > 1) {
      indent = "  ";
      println(indent, "");
    }

    for (Type type : node.getExtendedTypes()) {
      String typeName = convertGenericType(type);
      println(indent, className + " --|> " + typeName + " : extends ");
    }

    for (Type type : node.getImplementedTypes()) {
      String typeName = convertGenericType(type);
      println(indent, className + " --|> " + typeName + " : implements ");
    }

    // println(indent, "class " + className + " {");
    println(indent, "class " + className);

    if (node.isInterface()) {
      println(indent, "  <<interface>> " + className);
    } else if (node.isEnumDeclaration()) {
      println(indent, "  <<enum>> " + className);
    } else if (node.isAbstract()) {
      println(indent, "  <<abstract>> " + className);
    }

    super.visit(node, indent + "  " + className + " : ");

    // println(indent, "}");
    classDepth--;
  }

  @Override
  public void visit(FieldDeclaration node, String indent) {
    if (Main.printField) {
      String visibility = getVisibilityChar(node);
      String type = convertGenericType(node.getElementType());
      String varName = node.getVariable(0).getNameAsString();

      println(indent, visibility + type + " " + varName);
    }
    super.visit(node, indent);
  }

  @Override
  public void visit(MethodDeclaration node, String indent) {
    if (Main.printMethod) {
      String visibility = getVisibilityChar(node);
      String methodName = node.getNameAsString();
      String returnType = convertGenericType(node.getType());
      String args = node.getParameters()
        .stream()
        .map(p -> convertGenericType(p.getType()) + " " + p.getNameAsString())
        .collect(Collectors.joining(", "));

      println(indent, visibility + methodName + "(" + args + ") " + returnType);
    }
    super.visit(node, indent);
  }

  @Override
  public void defaultAction(Node node, String indent) {
    for (Node child : node.getChildNodes()) {
      child.accept(this, indent);
    }
  }

  @Override
  public void defaultAction(NodeList node, String indent) {
    for (int i = 0; i < node.size(); i++) {
      node.get(i).accept(this, indent);
    }
  }

  private void println(String indent, String line) {
    System.out.println(indent + line);
  }

  private String getVisibilityChar(NodeWithAccessModifiers node) {
    String visibility = "";
    if (node.isPublic()) {
      visibility = "+";
    } else if (node.isPrivate()) {
      visibility = "-";
    } else if (node.isProtected()) {
      visibility = "#";
    } else {
      visibility = "~";
    }
    return visibility;
  }

  private String convertGenericType(Type type) {
    char[] chars = type.toString().toCharArray();
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
