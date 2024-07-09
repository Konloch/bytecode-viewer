package the.bytecode.club.bytecodeviewer.decompilers.bytecode;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;

/**
 * This is generated code, please do not make any changes to this file. To add more tokens, adjust the
 * .flex file and then regenerate this file using JFlex.
 * <p>
 * Please see {@link org.fife.ui.rsyntaxtextarea.modes.JavaTokenMaker} as this implementation was based on it.
 * <p>
 * NOTE:
 * <ul>
 * 	<li>
 * 		When regenerating, the {@code zzBuffer} will turn into a {@code CharSequence}, set it to a {@code char[]}.
 * 		This will also create errors throughout where {@code zzBuffer} is used, so you will need to make small changes
 * 		to those methods.
 * 	</li>
 * 	<li>
 * 	    There will be a second {@code yyRefill} method with a default {@code return true;}, remove it.
 * 	</li>
 * </ul>
 */
%%

%public
%class JavaBytecodeTokenMaker
%extends AbstractJFlexCTokenMaker
%unicode
%type org.fife.ui.rsyntaxtextarea.Token

%{
    public JavaBytecodeTokenMaker() {

    }

	private void addHyperlinkToken(int start, int end, int tokenType) {
        int so = start + offsetShift;
		addToken(zzBuffer, start, end, tokenType, so, true);
    }

	private void addToken(int tokenType){
	  addToken(zzStartRead, zzMarkedPos - 1, tokenType);
    }

	private void addToken(int start, int end, int tokenType){
	  int so = start + offsetShift;
	  addToken(zzBuffer, start, end, tokenType, so, false);
    }

	@Override
	public void addToken(char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink) {
	  super.addToken(array, start, end, tokenType, startOffset, hyperlink);
	  zzStartRead = zzMarkedPos;
    }

	@Override
    public String[] getLineCommentStartAndEnd(int languageIndex) {
	  return new String[] { "//", null };
    }

	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
    		resetTokenList();
    		this.offsetShift = -text.offset + startOffset;

    		// Start off in the proper state.
    		int state;
    		switch (initialTokenType) {
    			case TokenTypes.COMMENT_MULTILINE:
    				state = MLC;
    				start = text.offset;
    				break;
    			case TokenTypes.COMMENT_DOCUMENTATION:
    				state = DOCCOMMENT;
    				start = text.offset;
    				break;
                case TokenTypes.LITERAL_STRING_DOUBLE_QUOTE:
                    state = TEXT_BLOCK;
                    start = text.offset;
                    break;
    			default:
    				state = YYINITIAL;
    		}

    		s = text;
    		try {
    			yyreset(zzReader);
    			yybegin(state);
    			return yylex();
    		} catch (IOException ioe) {
    			ioe.printStackTrace();
    			return new TokenImpl();
    		}

    }

	/**
    	 * Refills the input buffer.
    	 *
    	 * @return      <code>true</code> if EOF was reached, otherwise
    	 *              <code>false</code>.
    	 */
    	private boolean zzRefill() {
    		return zzCurrentPos>=s.offset+s.count;
    	}


    	/**
    	 * Resets the scanner to read from a new input stream.
    	 * Does not close the old reader.
    	 *
    	 * All internal variables are reset, the old input stream
    	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
    	 * Lexical state is set to <tt>YY_INITIAL</tt>.
    	 *
    	 * @param reader   the new input stream
    	 */
    	public final void yyreset(Reader reader) {
    		// 's' has been updated.
    		zzBuffer = s.array;
    		/*
    		 * We replaced the line below with the two below it because zzRefill
    		 * no longer "refills" the buffer (since the way we do it, it's always
    		 * "full" the first time through, since it points to the segment's
    		 * array).  So, we assign zzEndRead here.
    		 */
    		//zzStartRead = zzEndRead = s.offset;
    		zzStartRead = s.offset;
    		zzEndRead = zzStartRead + s.count - 1;
    		zzCurrentPos = zzMarkedPos = s.offset;
    		zzLexicalState = YYINITIAL;
    		zzReader = reader;
    		zzAtBOL  = true;
    		zzAtEOF  = false;
    	}

%}

Letter							= ([A-Za-z])
LetterOrUnderscore				= ({Letter}|"_")
NonzeroDigit						= ([1-9])
BinaryDigit						= ([0-1])
Digit							= ("0"|{NonzeroDigit})
HexDigit							= ({Digit}|[A-Fa-f])
OctalDigit						= ([0-7])
AnyCharacterButApostropheOrBackSlash	= ([^\\'])
AnyCharacterButDoubleQuoteOrBackSlash	= ([^\\\"\n])
EscapedSourceCharacter				= ("u"{HexDigit}{HexDigit}{HexDigit}{HexDigit})
Escape							= ("\\"(([bstnfr\"'\\])|([0123]{OctalDigit}?{OctalDigit}?)|({OctalDigit}{OctalDigit}?)|{EscapedSourceCharacter}))
NonSeparator						= ([^\t\f\r\n\ \(\)\{\}\[\]\;\,\.\=\>\<\!\~\?\:\+\-\*\/\&\|\^\%\"\']|"#"|"\\")
IdentifierStart                     = ([:jletter:])
IdentifierPart						= ([:jletterdigit:]|("\\"{EscapedSourceCharacter}))

LineTerminator				= \r|\n|\r\n
WhiteSpace				= ([ \t\f])

CharLiteral				= ([\']({AnyCharacterButApostropheOrBackSlash}|{Escape})[\'])
UnclosedCharLiteral			= ([\'][^\'\n]*)
ErrorCharLiteral			= ({UnclosedCharLiteral}[\'])
StringLiteral				= ([\"]({AnyCharacterButDoubleQuoteOrBackSlash}|{Escape})*[\"])
UnclosedStringLiteral		= ([\"]([\\].|[^\\\"])*[^\"]?)
ErrorStringLiteral			= ({UnclosedStringLiteral}[\"])

MLCBegin					= "/*"
MLCEnd					= "*/"
DocCommentBegin			= "/**"
LineCommentBegin			= "//"

DigitOrUnderscore			= ({Digit}|[_])
DigitsAndUnderscoresEnd		= ({DigitOrUnderscore}*{Digit})
IntegerHelper				= (({NonzeroDigit}{DigitsAndUnderscoresEnd}?)|"0")
IntegerLiteral				= ({IntegerHelper}[lL]?)

BinaryDigitOrUnderscore		= ({BinaryDigit}|[_])
BinaryDigitsAndUnderscores	= ({BinaryDigit}({BinaryDigitOrUnderscore}*{BinaryDigit})?)
BinaryLiteral				= ("0"[bB]{BinaryDigitsAndUnderscores})

HexDigitOrUnderscore		= ({HexDigit}|[_])
HexDigitsAndUnderscores		= ({HexDigit}({HexDigitOrUnderscore}*{HexDigit})?)
OctalDigitOrUnderscore		= ({OctalDigit}|[_])
OctalDigitsAndUnderscoresEnd= ({OctalDigitOrUnderscore}*{OctalDigit})
HexHelper					= ("0"(([xX]{HexDigitsAndUnderscores})|({OctalDigitsAndUnderscoresEnd})))
HexLiteral					= ({HexHelper}[lL]?)

FloatHelper1				= ([fFdD]?)
FloatHelper2				= ([eE][+-]?{Digit}+{FloatHelper1})
FloatLiteral1				= ({Digit}+"."({FloatHelper1}|{FloatHelper2}|{Digit}+({FloatHelper1}|{FloatHelper2})))
FloatLiteral2				= ("."{Digit}+({FloatHelper1}|{FloatHelper2}))
FloatLiteral3				= ({Digit}+{FloatHelper2})
FloatLiteral				= ({FloatLiteral1}|{FloatLiteral2}|{FloatLiteral3}|({Digit}+[fFdD]))

ErrorNumberFormat			= (({IntegerLiteral}|{HexLiteral}|{FloatLiteral}){NonSeparator}+)
BooleanLiteral				= ("true"|"false")

Separator					= ([\(\)\{\}\[\]])
Separator2				= ([\;,.])

NonAssignmentOperator		= ("+"|"-"|"<="|"^"|"++"|"<"|"*"|">="|"%"|"--"|">"|"/"|"!="|"?"|">>"|"!"|"&"|"=="|":"|">>"|"~"|"|"|"&&"|">>>")
AssignmentOperator			= ("="|"-="|"*="|"/="|"|="|"&="|"^="|"+="|"%="|"<<="|">>="|">>>=")
Operator					= ({NonAssignmentOperator}|{AssignmentOperator})

CurrentBlockTag				= ("author"|"deprecated"|"exception"|"param"|"return"|"see"|"serial"|"serialData"|"serialField"|"since"|"throws"|"version")
ProposedBlockTag			= ("category"|"example"|"tutorial"|"index"|"exclude"|"todo"|"internal"|"obsolete"|"threadsafety")
BlockTag					= ({CurrentBlockTag}|{ProposedBlockTag})
InlineTag					= ("code"|"docRoot"|"inheritDoc"|"link"|"linkplain"|"literal"|"value")

Identifier				= ({IdentifierStart}{IdentifierPart}*)
ErrorIdentifier			= ({NonSeparator}+)

Annotation				= ("@"{Identifier}?)

URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved			= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter			= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters			= ({URLCharacter}*)
URLEndCharacter			= ([\/\$]|{Letter}|{Digit})
URL						= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)

%state MLC
%state DOCCOMMENT
%state EOL_COMMENT
%state TEXT_BLOCK

%%

<YYINITIAL> {
/* Keywords */
	"_"	 |
	"abstract"|
	"assert" |
	"break"	 |
	"case"	 |
	"catch"	 |
	"class"	 |
	"const"	 |
	"continue" |
	"default" |
	"do"	 |
	"else"	 |
	"enum"	 |
	"exports" |
	"extends" |
	"final"	 |
	"finally" |
	"for"	 |
	"goto"	 |
	"if"	 |
	"implements" |
	"import" |
	"instanceof" |
	"interface" |
	"module" |
	"native" |
	"new"	 |
	"non-sealed" |
	"null"	 |
	"open" |
	"opens" |
	"package" |
	"permits" |
	"private" |
	"protected" |
	"provides" |
	"public" |
	"record" |
	"requires" |
	"sealed" |
	"static" |
	"strictfp" |
	"super"	 |
	"switch" |
	"synchronized" |
	"this"	 |
	"throw"	 |
	"throws" |
	"to" |
	"transient" |
	"transitive" |
	"try"	 |
	"uses" |
	"void"	 |
	"volatile" |
	"while" |
	/* Bytecode instructions */
	"ifeq" |
	"ifne" |
	"iflt" |
    "ifle" |
    "ifgt" |
	"ifge" |
	"ifnonnull" |
	"ifnull" |
	"if_icmplt" |
	"if_icmple" |
	"if_icmpne" |
	"if_icmpge" |
	"if_icmpgt" |
	"if_icmpeq" |
	"return" |
	"areturn" |
	"athrow" |
	"with"  { addToken(TokenTypes.RESERVED_WORD); }

    /* Data types. */
    "boolean" |
    "byte" |
    "char" |
    "double" |
    "float" |
    "int" |
    "long" |
    "short" |
    "var"  { addToken(TokenTypes.DATA_TYPE); }

    /* Booleans. */
    {BooleanLiteral}  { addToken(TokenTypes.LITERAL_BOOLEAN); }

    {LineTerminator}				{ addNullToken(); return firstToken; }

    {Identifier}					{ addToken(TokenTypes.IDENTIFIER); }

    {WhiteSpace}+					{ addToken(TokenTypes.WHITESPACE); }

    /* String/Character literals. */
    \"\"\"						{ start = zzMarkedPos-3; yybegin(TEXT_BLOCK); }
   	{CharLiteral}					{ addToken(TokenTypes.LITERAL_CHAR); }
    {UnclosedCharLiteral}			{ addToken(TokenTypes.ERROR_CHAR); addNullToken(); return firstToken; }
    {ErrorCharLiteral}				{ addToken(TokenTypes.ERROR_CHAR); }
    {StringLiteral}				{ addToken(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE); }
    {UnclosedStringLiteral}			{ addToken(TokenTypes.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }
    {ErrorStringLiteral}			{ addToken(TokenTypes.ERROR_STRING_DOUBLE); }

    /* Comment literals. */
    "/**/"						{ addToken(TokenTypes.COMMENT_MULTILINE); }
    {MLCBegin}					{ start = zzMarkedPos-2; yybegin(MLC); }
    {DocCommentBegin}				{ start = zzMarkedPos-3; yybegin(DOCCOMMENT); }
    {LineCommentBegin}			{ start = zzMarkedPos-2; yybegin(EOL_COMMENT); }

    /* Annotations. */
    {Annotation}					{ addToken(TokenTypes.ANNOTATION); }

    /* Separators. */
    {Separator}					{ addToken(TokenTypes.SEPARATOR); }
    {Separator2}					{ addToken(TokenTypes.IDENTIFIER); }

    /* Operators. */
    {Operator}					{ addToken(TokenTypes.OPERATOR); }

    /* Numbers */
    {IntegerLiteral}				{ addToken(TokenTypes.LITERAL_NUMBER_DECIMAL_INT); }
    {BinaryLiteral}					{ addToken(TokenTypes.LITERAL_NUMBER_DECIMAL_INT); }
    {HexLiteral}					{ addToken(TokenTypes.LITERAL_NUMBER_HEXADECIMAL); }
    {FloatLiteral}					{ addToken(TokenTypes.LITERAL_NUMBER_FLOAT); }
    {ErrorNumberFormat}				{ addToken(TokenTypes.ERROR_NUMBER_FORMAT); }

    {ErrorIdentifier}				{ addToken(TokenTypes.ERROR_IDENTIFIER); }

    /* Ended with a line not in a string or comment. */
    <<EOF>>						{ addNullToken(); return firstToken; }

    /* Catch any other (unhandled) characters and flag them as identifiers. */
    .							{ addToken(TokenTypes.ERROR_IDENTIFIER); }
}

<MLC> {

	[^hwf\n\*]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, TokenTypes.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_MULTILINE); start = zzMarkedPos; }
	[hwf]					{}

	{MLCEnd}					{ yybegin(YYINITIAL); addToken(start,zzStartRead+1, TokenTypes.COMMENT_MULTILINE); }
	\*						{}
	\n |
	<<EOF>>					{ addToken(start,zzStartRead-1, TokenTypes.COMMENT_MULTILINE); return firstToken; }

}


<DOCCOMMENT> {

	[^hwf\@\{\n\<\*]+			{}
	{URL}						{
                                    int temp = zzStartRead;
                                    if (start <= zzStartRead - 1) {
                                        addToken(start,zzStartRead-1, TokenTypes.COMMENT_DOCUMENTATION);
                                    }
                                    addHyperlinkToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_DOCUMENTATION);
                                    start = zzMarkedPos;
                                }
	[hwf]						{}

	"@"{BlockTag}				{
                                    int temp = zzStartRead;
                                    if (start <= zzStartRead - 1) {
                                        addToken(start,zzStartRead-1, TokenTypes.COMMENT_DOCUMENTATION);
                                    }
                                    addToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_KEYWORD);
                                    start = zzMarkedPos;
                                }
	"@"							{}
	"{@"{InlineTag}[^\}]*"}"	{
                                    int temp = zzStartRead;
                                    if (start <= zzStartRead - 1) {
                                        addToken(start,zzStartRead-1, TokenTypes.COMMENT_DOCUMENTATION);
                                    }
                                    addToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_KEYWORD);
                                    start = zzMarkedPos;
                                }
	"{"							{}
	\n							{ addToken(start,zzStartRead-1, TokenTypes.COMMENT_DOCUMENTATION); return firstToken; }
	"<"[/]?({Letter}[^\>]*)?">"	{ int temp=zzStartRead; addToken(start,zzStartRead-1, TokenTypes.COMMENT_DOCUMENTATION); addToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_MARKUP); start = zzMarkedPos; }
	\<							{}
	{MLCEnd}					{ yybegin(YYINITIAL); addToken(start,zzStartRead+1, TokenTypes.COMMENT_DOCUMENTATION); }
	\*							{}
	<<EOF>>						{ yybegin(YYINITIAL); addToken(start,zzEndRead, TokenTypes.COMMENT_DOCUMENTATION); return firstToken; }

}


<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, TokenTypes.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, TokenTypes.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n |
	<<EOF>>					{ addToken(start,zzStartRead-1, TokenTypes.COMMENT_EOL); addNullToken(); return firstToken; }

}

<TEXT_BLOCK> {
	[^\"\\\n]*				{}
	\\.?						{ /* Skip escaped chars, handles case: '\"""'. */ }
	\"\"\"					{ yybegin(YYINITIAL); addToken(start,zzStartRead+2, TokenTypes.LITERAL_STRING_DOUBLE_QUOTE); }
	\"						{}
	\n |
	<<EOF>>					{ addToken(start,zzStartRead-1, TokenTypes.LITERAL_STRING_DOUBLE_QUOTE); return firstToken; }
}