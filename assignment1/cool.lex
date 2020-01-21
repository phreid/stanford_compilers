/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }

    private int comment_level;
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

   
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
    case COMMENT:
      yybegin(YYINITIAL);
      return new Symbol(TokenConstants.ERROR, "EOF in comment");
    case STRING:
      yybegin(YYINITIAL);
      return new Symbol(TokenConstants.ERROR, "EOF in string constant");
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup

DIGIT = [0-9]

CLASS = [Cc][Ll][Aa][Ss][Ss]
ELSE = [Ee][Ll][Ss][Ee]
IF = [Ii][Ff]
FI = [Ff][Ii]
IN = [Ii][Nn]
INHERITS = [Ii][Nn][Hh][Ee][Rr][Ii][Tt][Ss]
ISVOID = [Ii][Ss][Vv][Oo][Ii][Dd]
LET = [Ll][Ee][Tt]
LOOP = [Ll][Oo][Oo][Pp]
POOL = [Pp][Oo][Oo][Ll]
THEN = [Tt][Hh][Ee][Nn]
WHILE = [Ww][Hh][Ii][Ll][Ee]
CASE = [Cc][Aa][Ss][Ee]
ESAC = [Ee][Ss][Aa][Cc]
NEW = [Nn][Ee][Ww]
OF = [Oo][Ff]
NOT = [Nn][Oo][Tt]

TRUE = t[Rr][Uu][Ee]
FALSE = f[Aa][Ll][Ss][Ee]

WHITESPACE = [\ \t]

%state STRING
%state COMMENT

%%

<YYINITIAL>"*"      {return new Symbol(TokenConstants.MULT);}
<YYINITIAL>"."      {return new Symbol(TokenConstants.DOT);}
<YYINITIAL>"("      {return new Symbol(TokenConstants.LPAREN);}
<YYINITIAL>";"      {return new Symbol(TokenConstants.SEMI);}
<YYINITIAL>"-"      {return new Symbol(TokenConstants.MINUS);}
<YYINITIAL>")"      {return new Symbol(TokenConstants.RPAREN);}
<YYINITIAL>"<"      {return new Symbol(TokenConstants.LT);}
<YYINITIAL>","      {return new Symbol(TokenConstants.COMMA);}
<YYINITIAL>"/"      {return new Symbol(TokenConstants.DIV);}
<YYINITIAL>"+"      {return new Symbol(TokenConstants.PLUS);}
<YYINITIAL>"<-"     {return new Symbol(TokenConstants.ASSIGN);}
<YYINITIAL>"<="     {return new Symbol(TokenConstants.LE);}
<YYINITIAL>"="      {return new Symbol(TokenConstants.EQ);}
<YYINITIAL>":"      {return new Symbol(TokenConstants.COLON);}
<YYINITIAL>"~"      {return new Symbol(TokenConstants.NEG);}
<YYINITIAL>"{"      {return new Symbol(TokenConstants.LBRACE);}
<YYINITIAL>"}"      {return new Symbol(TokenConstants.RBRACE);}
<YYINITIAL>"@"      {return new Symbol(TokenConstants.AT);}

<YYINITIAL>{CLASS}    {return new Symbol(TokenConstants.CLASS);}
<YYINITIAL>{NOT}      {return new Symbol(TokenConstants.NOT);}
<YYINITIAL>{OF}       {return new Symbol(TokenConstants.OF);}
<YYINITIAL>{NEW}      {return new Symbol(TokenConstants.NEW);}
<YYINITIAL>{ESAC}     {return new Symbol(TokenConstants.ESAC);}
<YYINITIAL>{CASE}     {return new Symbol(TokenConstants.CASE);}
<YYINITIAL>{WHILE}    {return new Symbol(TokenConstants.WHILE);}
<YYINITIAL>{THEN}     {return new Symbol(TokenConstants.THEN);}
<YYINITIAL>{POOL}     {return new Symbol(TokenConstants.POOL);}
<YYINITIAL>{LET}      {return new Symbol(TokenConstants.LET);}
<YYINITIAL>{LOOP}     {return new Symbol(TokenConstants.LOOP);}
<YYINITIAL>{ISVOID}   {return new Symbol(TokenConstants.ISVOID);}
<YYINITIAL>{INHERITS} {return new Symbol(TokenConstants.INHERITS);}
<YYINITIAL>{IN}       {return new Symbol(TokenConstants.IN);}
<YYINITIAL>{FI}       {return new Symbol(TokenConstants.FI);}
<YYINITIAL>{ELSE}     {return new Symbol(TokenConstants.ELSE);}
<YYINITIAL>{IF}       {return new Symbol(TokenConstants.IF);}

<YYINITIAL>{TRUE}     {return new Symbol(TokenConstants.BOOL_CONST, true);}
<YYINITIAL>{FALSE}    {return new Symbol(TokenConstants.BOOL_CONST, false);}

<YYINITIAL>{DIGIT}+
{
  AbstractSymbol entry = AbstractTable.inttable.addString(yytext());
  return new Symbol(TokenConstants.INT_CONST, entry);
}

<YYINITIAL>"\""
{
  string_buf.setLength(0);
  yybegin(STRING);
}

<STRING>"\""
{
  yybegin(YYINITIAL);
  if (string_buf.length() > MAX_STR_CONST) {
    return new Symbol(TokenConstants.ERROR, "String constant too long");
  }

  AbstractSymbol entry = AbstractTable
    .stringtable
    .addString(string_buf.toString());
  return new Symbol(TokenConstants.STR_CONST, entry);
}

<STRING>[^\n\r\"\\]+  {string_buf.append(yytext());}
<STRING>\\t           {string_buf.append('\t');}
<STRING>\\b           {string_buf.append('\b');}
<STRING>\\n           {string_buf.append('\n');}
<STRING>\\f           {string_buf.append('\f');}

<STRING>\0
{
return new Symbol(TokenConstants.ERROR, "String contains null character");
}           

<STRING>\n
{
  curr_lineno++;
  yybegin(YYINITIAL);
  return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
}

<YYINITIAL>[A-Z][A-Za-z0-9_]*
{
  AbstractSymbol entry = AbstractTable.idtable.addString(yytext());
  return new Symbol(TokenConstants.TYPEID, entry);
}

<YYINITIAL>[a-z][A-Za-z0-9_]*
{
  AbstractSymbol entry = AbstractTable.idtable.addString(yytext());
  return new Symbol(TokenConstants.OBJECTID, entry);
}

<YYINITIAL>--.*      {}
<YYINITIAL>\(\*      {yybegin(COMMENT);}
<YYINITIAL>\*\)      {return new Symbol(TokenConstants.ERROR, "Unmatched *)");}

<COMMENT>\(\*        {comment_level++;}
<COMMENT>\n          {curr_lineno++;}
<COMMENT>.           {}

<COMMENT>\*\)
{
  if (comment_level > 0) {
    comment_level--;
  } else {
    yybegin(YYINITIAL);
  }
}

<YYINITIAL>[\n\r]  {curr_lineno++;}

{WHITESPACE}       {}

.                  { System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }
