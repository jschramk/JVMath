/*
BSD License

Copyright (c) 2013, Tom Everett
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of Tom Everett nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/* BSD License
Copyright (c) 2013, Tom Everett
All rights reserved.
*/

grammar jvmathArithmetic;

equation   : SPACE? sum SPACE? comparator SPACE? sum SPACE? ;

sum : product  (SPACE? (PLUS | MINUS) SPACE? product)*   ;

product       : signedFactor (SPACE? (TIMES | DIV | SPACE)? SPACE? factor)*
              |  signedFactor (SPACE? (TIMES | DIV) SPACE? signedFactor)* ;

signedFactor     : signedAtom (SPACE? POW SPACE? signedAtom)* ;

factor     : atom (SPACE? POW SPACE? signedAtom)*  ;

signedAtom : MINUS SPACE? signedAtom | atom   ;

atom       : literal | variable | LPAREN SPACE? sum SPACE? RPAREN   ;

literal : SCIENTIFIC_NUMBER   ;

variable   : VARIABLE   ;

comparator      : EQ | GT | LT;

VARIABLE   : VALID_ID_START VALID_ID_CHAR*   ;

fragment VALID_ID_START: ('a' .. 'z') | ('A' .. 'Z') | '_'   ;

fragment VALID_ID_CHAR: VALID_ID_START | ('0' .. '9')   ;

SCIENTIFIC_NUMBER: NUMBER (E SIGN? NUMBER)?   ;

fragment NUMBER: ('0' .. '9') + ('.' ('0' .. '9') +)?   ;

fragment E: 'E' | 'e'   ;

fragment SIGN: ('+' | '-')   ;

SPACE: ' '+   ;

LPAREN   : '('   ;

RPAREN   : ')'   ;

PLUS     : '+'   ;

MINUS: '\u2212' | '\u002d' ;

TIMES: '\u22c5' | '\u00d7' | '\u002a' ;

DIV: '\u00f7' | '\u002f' ;

GT       : '>'   ;

LT       : '<'   ;

EQ       : '='   ;

POINT    : '.'   ;

POW      : '^'   ;

//WHITESPACE       : [\r\n\t] + -> skip   ;