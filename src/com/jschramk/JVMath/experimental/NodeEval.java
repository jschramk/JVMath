package com.jschramk.JVMath.experimental;

public class NodeEval {

  public static Node evaluate(Node n) {

    return null;

  }

  // evaluates a binary operation of a given type.
  // associativity is handled by the caller.
  private static Node binary(int type, Node n1, Node n2) {

    switch (type) {

      case NodeInfo.TYPE_ADDITION: {
        return Node.literal(n1.getDoubleValue() + n2.getDoubleValue());
      }

      case NodeInfo.TYPE_SUBTRACTION: {
        return Node.literal(n1.getDoubleValue() - n2.getDoubleValue());
      }

      case NodeInfo.TYPE_MULTIPLICATION: {
        return Node.literal(n1.getDoubleValue() * n2.getDoubleValue());
      }

      case NodeInfo.TYPE_DIVISION: {
        return Node.literal(n1.getDoubleValue() / n2.getDoubleValue());
      }

      case NodeInfo.TYPE_EXPONENT: {
        return Node.literal(Math.pow(n1.getDoubleValue(), n2.getDoubleValue()));
      }

    }

    throw new RuntimeException("Unable to evaluate binary operation of type " + type);

  }

  // evaluates a unary operation of a given type
  private static Node unary(int type, Node n) {

    switch (type) {

      case NodeInfo.TYPE_NEGATION: {
        return Node.literal(-n.getDoubleValue());
      }

    }

    throw new RuntimeException("Unable to evaluate unary operation of type " + type);

  }

  // evaluates a function with a given name
  private static Node function(String name, Node... args) {

    switch (name) {

    }

    throw new RuntimeException("Unable to evaluate function \"" + name + "\"");

  }

}
