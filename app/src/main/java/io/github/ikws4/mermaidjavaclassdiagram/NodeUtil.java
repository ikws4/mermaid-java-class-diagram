package io.github.ikws4.mermaidjavaclassdiagram;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.type.Type;

class NodeUtil {
  public static String getClassName(ClassOrInterfaceDeclaration node) {
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
      className = NodeUtil.convertGenericType(className);
    }
    return className;
  }
  
  public static String getVisibilityChar(NodeWithAccessModifiers node) {
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

  public static String convertGenericType(Type type) {
    return convertGenericType(type.toString());
  }

  public static String convertGenericType(String type) {
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
