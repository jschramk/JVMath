# Overview
JVMath is a modular and expandable computer algebra system (CAS) for Java.

# Documentation

## Table of Contents
1. [JVMath Language](#jvmath-language)

## JVMath Language
JVMath uses a set of rules to operate on expressions and equations. The rules are defined in ```.rules``` source files. These are compiled to JSON files which are included in the JVMath JAR and read at runtime. Rules are applied in the order they are defined in the source files. Below is an example of a rule for solving an equation.
```
@FLIP
solve (y = x) {
    x = y : Flip the equation so ${#x} is on the left side
} where {
    x: is f(#target)
    y: not f(#target)
}
```
Each rule starts with an optional ID, which is denoted by @ followed by the ID. The naming convention for IDs is generally all caps and underscores. Besides being helpful for debugging, IDs allow rules to be referenced as follow-ups of other rules, even when they are not defined directly after the rule that references them. This feature allows different rules to apply to different starting configurations, then pass a common intermediate configuration on to a follow-up rule.

Each rule has an action, which is currently either ```simplify``` or ```solve```. Rules are categorized by actions, so all ```solve``` rules are grouped together, for example.

After the action, the input expression or equation is defined. When rules are applied to a given expresson, the first rule with an input expression that matches the given expression will be applied. If this rule references a follow up rule, then the referenced rule will be applied next. It is considered an error if the referenced rule's input expression does not match the output expression from the previous rule. When a rule is applied, the math engine attempts to apply it as many times as it can be applied in sequence before moving on.

Following the input expression, each step for the application of the rule is defined in order, each on a separate line. If a follow-up rule is defined, the ID of the referenced rule (including the @) must be written as the last step of the rule. Each step has an optional description, which is separated from the step definition by a ```:```. Descriptions may contain text between ```${``` and ```}```, which denotes that this text should be rendered inline when presented to the user. Within these brackets, variable names preceded by ```#``` will be replaced with the variable name that the input variable maps to in the given expression. For example, if the given expression for the rule above is ```a = b``` and the user requests to solve the equation for ```b```, the step description presented to the user will read ```Flip the equation so b is on the left side```, and ```b``` will be highlighted (if the platform supports this feature).
