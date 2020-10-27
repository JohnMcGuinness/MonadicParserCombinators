package com.github.johnmcguinness.fp.monparse;

import static com.github.johnmcguinness.fp.monparse.Parser.brackets;
import static com.github.johnmcguinness.fp.monparse.Parser.character;
import static com.github.johnmcguinness.fp.monparse.Parser.lower;
import static com.github.johnmcguinness.fp.monparse.Parser.many;

public class App {
	public static void main(String[] args) {
//		System.out.println(many(letter()).parse("aaa"));
//		System.out.println(many1(letter()).parse("aaa"));
//		System.out.println(nat().parse("aaa"));
//		System.out.println(nat().parse("123"));
//		System.out.println(nat().parse("1b3"));
//		System.out.println(lower().plus(upper()).parse("a5"));
//		System.out.println(lower().plus(upper()).parse("A5"));
//		System.out.println(integer().parse("12"));
//		System.out.println(integer().parse("-12"));
//		System.out.println(ints().parse("[1,2,-3,0]"));
//		System.out.println(integer().sepby1(character(' ')).parse("1 2"));
//		System.out.println(ints_().parse("[1,2,-3,0]"));
//		System.out.println(ints__().parse("[1,2,-3,0]"));
//		System.out.println(integer().sepby(character(',')).parse(""));
//		System.out.println(brackets(character('$'), many(lower()), character('%')).parse("$dsdss%"));
		System.out.println(Arithmatic.expr().parse("2-(9+4)"));
	}
}
