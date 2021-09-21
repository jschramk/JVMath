grammar ruleSet;

// parser codeRules

parse: r_rule+ EOF;

r_rule: r_definition r_filter?;

r_definition: id? ACTION NESTED_PARENS r_target_specifier? r_action_content;

r_filter: 'where' NESTED_BRACKETS;

r_target_specifier: 'for' NESTED_PARENS;

r_action_content: NESTED_BRACKETS;

id: ID;

// lexer codeRules

//REQUIREMENT_SPEC : 'is' | 'not' ;

//FILTER: 'where' WS? '{' FILTER_LINE* '}';

//FILTER_LINE: WS? VARIABLE WS? ':' WS? REQUIREMENT_SPEC WS? 'f(' WS? VARIABLE WS? ')' WS?;

ACTION: 'solve' | 'simplify' ;

ID: '@' VARIABLE;

VARIABLE   : [a-zA-Z_][a-zA-Z_0-9]*;

NESTED_PARENS:  '(' ( ~[()] | NESTED_PARENS )* ')';

NESTED_BRACKETS: '{' ( ~[{}] | NESTED_BRACKETS )* '}';

WS: [ \t\r\n]+ -> skip;

COMMENT: '/*' .*? '*/' -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;






