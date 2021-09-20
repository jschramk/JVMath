package com.jschramk.JVMath.experimental;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {

  private int type;
  private List<Node> children = new ArrayList<>();
  private Object value;

  public Node(int type) {
    this.type = type;
  }

  public Node(int type, Node... children) {
    this(type);
    this.children = Arrays.asList(children);
  }

  public static Node variable(String name) {
    Node n = new Node(NodeInfo.TYPE_VARIABLE);
    n.setStringValue(name);
    return n;
  }

  public static Node literal(double value) {
    Node n = new Node(NodeInfo.TYPE_LITERAL);
    n.setDoubleValue(value);
    return n;
  }

  public static Node addition(Node... children) {
    return new Node(NodeInfo.TYPE_ADDITION, children);
  }

  public static Node subtraction(Node... children) {
    return new Node(NodeInfo.TYPE_SUBTRACTION, children);
  }

  public static Node multiplication(Node... children) {
    return new Node(NodeInfo.TYPE_MULTIPLICATION, children);
  }

  public static Node division(Node... children) {
    return new Node(NodeInfo.TYPE_DIVISION, children);
  }

  public static Node exponent(Node... children) {
    return new Node(NodeInfo.TYPE_EXPONENT, children);
  }

  public static Node negation(Node child) {
    return new Node(NodeInfo.TYPE_NEGATION, child);
  }

  protected void setDoubleValue(double value) {
    this.value = value;
  }

  public double getDoubleValue() {
    return (double) value;
  }

  public int getType() {
    return type;
  }

  protected void setStringValue(String value) {
    this.value = value;
  }

  public String getStringValue() {
    return (String) value;
  }

  public int childCount() {
    return children == null ? 0 : children.size();
  }

  public Node getChild(int i) {
    return children.get(i);
  }

  public void addChild(Node node) {
    children.add(node);
  }

  public void addChild(int i, Node node) {
    children.add(i, node);
  }

  public void setChild(int i, Node node) {
    children.set(i, node);
  }

  public void removeChild(int i) {
    children.remove(i);
  }

  // get the basic JSON object for this node, can add to it in overridden methods
  protected JsonObject baseToJson() {

    JsonObject object = new JsonObject();

    object.addProperty("type", type);

    if (value != null) {

      if (value instanceof String) {
        object.addProperty("value", (String) value);
      } else if (value instanceof Double) {
        object.addProperty("value", (double) value);
      }

    }

    if (childCount() > 0) {

      JsonArray childArray = new JsonArray();

      for (int i = 0; i < childCount(); i++) {

        childArray.add(getChild(i).toJson());

      }

      object.add("children", childArray);

    }

    return object;

  }

  // default JSON object contains only type and children if any
  public JsonObject toJson() {
    return baseToJson();
  }

  public static Node fromJson(JsonObject object) {
    return baseFromJson(object);
  }

  protected static Node baseFromJson(JsonObject object) {

    int type = object.get("type").getAsInt();

    Node node = new Node(type);



    if (object.has("children")) {

      JsonArray children = object.get("children").getAsJsonArray();

      for (int i = 0; i < children.size(); i++) {

        node.addChild(fromJson(children.get(i).getAsJsonObject()));

      }

    }

    return node;

  }

}
