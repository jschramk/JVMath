# Overview
JVMath is a computer algebra system (CAS) for Java. It includes a parser and support for simplifying expressions and solving equations.

# Documentation

## Expression Representation
Expressions are internally represented as trees. Evaluation takes place starting from the outermost leaves and ends at the root. Each node in the tree has a certain type, and each type has a relative priority to the other types, allowing for expression trees to be converted to strings without loss of operation hierarchy. Nodes that have a lower priority than their parent must be wrapped by parentheses when concerting the tree to a string.

## Expression Storage
Expressions can either be stored in string or JSON form. Storing them in string form is much more space efficient, but relies on the client to have access to the JVMath parser to convert expressions into trees. Storing them as JSON objects requires more space, but allows the use of clients without direct access to the full functionality of JVMath, such as a web browser, to more easily construct expession trees.

## Operand Types
Each node falls under a specific type from the enumeration below. Nodes with type "Other" require additional information to be fully defined and will be covered in another section.

Code|Type
-|-
0|Literal
1|Variable
2|Addition
3|Subtraction
4|Multiplication
5|Division
6|Exponent
7|Negation
8|Function
9|Other

## JSON Representation
JSON representation of expressions is relatively straightforward. Each tree node is represented as a JSON object with fields for its type, its value (if applicable), and its children (if applicable). Nodes that have a value field, such as variables, generally do not have children. Similarly, nodes with children generally do not have a value. Nodes that represent operations act more as containers for other nodes, while nodes that represent variables or literals act as leaves in the tree. In some cases, a value field may accompany a node with children, such as with named functions where the value field contains the function name and the children field contains its arguments.

## Example
Below is an example of an expression in string form and its corresponding JSON representation. Notice how the order of operations in the expressions is conserved in the JSON hierarchy.
```
(y + 1) * x^2
```
```
{
  "type": 4,
  "children": [
    {
      "type": 2,
      "children": [
        {
          "type": 1,
          "value": "y"
        },
        {
          "type": 0,
          "value": 1
        }
      ]
    },
    {
      "type": 6,
      "children": [
        {
          "type": 1,
          "value": "x"
        },
        {
          "type": 0,
          "value": 2
        }
      ]
    }
  ]
}
```


