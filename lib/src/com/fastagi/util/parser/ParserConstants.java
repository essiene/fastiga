/* Generated By:JavaCC: Do not edit this line. ParserConstants.java */
package com.fastagi.util.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int CODE = 5;
  /** RegularExpression Id. */
  int SPACE = 6;
  /** RegularExpression Id. */
  int RESULT = 7;
  /** RegularExpression Id. */
  int EQUALS = 8;
  /** RegularExpression Id. */
  int DIGIT = 9;
  /** RegularExpression Id. */
  int SIGN = 10;
  /** RegularExpression Id. */
  int BROPEN = 11;
  /** RegularExpression Id. */
  int BRCLOSE = 12;
  /** RegularExpression Id. */
  int ID = 13;
  /** RegularExpression Id. */
  int DATA = 14;
  /** RegularExpression Id. */
  int LETTER = 15;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\r\\n\"",
    "\"\\t\"",
    "\"200\"",
    "\" \"",
    "\"result\"",
    "\"=\"",
    "<DIGIT>",
    "\"-\"",
    "\"(\"",
    "\")\"",
    "\"endpos\"",
    "<DATA>",
    "<LETTER>",
  };

}