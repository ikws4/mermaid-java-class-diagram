package io.github.ikws4.mermaidjavaclassdiagram;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

class BaseVisitor<T> extends VoidVisitorWithDefaults<T> {
  @Override
  public void defaultAction(Node node, T arg) {
    for (Node child : node.getChildNodes()) {
      child.accept(this, arg);
    }
  }

  @Override
  public void defaultAction(NodeList node, T arg) {
    for (int i = 0; i < node.size(); i++) {
      node.get(i).accept(this, arg);
    }
  }
}
