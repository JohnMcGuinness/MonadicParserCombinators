package com.github.johnmcguinness.fp.monparse;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface Parser<A> { 

	public static <A> Parser<A> result(A a) {
		return input -> List.of(Tuple.of(a, input));
	}

	public static <A> Parser<A> zero() {
		return input -> List.of();
	}

	public static Parser<Character> item() {
		return input -> {
			
			final CharSeq chars = 
				input == null
					? CharSeq.empty()
					: CharSeq.of(input);
			
			return chars.isEmpty()
					? List.of()
					: List.of(Tuple.of(chars.head(), chars.tail().mkString()));
		};
	}
	
	public default <B> Parser<B> bind(Function<A, Parser<B>> f) {
		
		return input -> 
			parse(input)
				.stream()
				.flatMap(result -> f.apply(result._1).parse(result._2).stream())
				.collect(Collectors.toList());
	}
	
	public default <B> Parser<Tuple2<A, B>> seq(Parser<B> p) {
		return
			this.bind(a -> 
				p.bind(b ->
					result(Tuple.of(a, b))));
	}
	
	public default Parser<A> plus(Parser<A> p) {
		return input ->  
				Stream
					.concat(parse(input).stream(), p.parse(input).stream())
                    .collect(Collectors.toList());
	}
	
	public static Parser<Character> sat(Predicate<Character> p) {
		return item().bind(x -> p.test(x) ? result(x) : zero());
	}
	
	public static Parser<Character> character(char ch) {
		return sat(input -> ch == input);
	}
	
	public static Parser<Character> digit() {
		return sat(input -> '0' <= input && input <= '9');
	}
	
	public static Parser<Character> lower() {
		return sat(input -> 'a' <= input && input <= 'z');
	}
	
	public static Parser<Character> upper() {
		return sat(input -> 'A' <= input && input <= 'Z');
	}
	
	public static Parser<Character> letter() {
		return lower().plus(upper());
	}
	
	public static Parser<Character> alphanum() {
		return letter().plus(digit());
	}
	
	public static Parser<String> word() {
		return many(letter()).bind(chars -> result(CharSeq.ofAll(chars).mkString()));
	}

	public static <A> Parser<List<A>> many(Parser<A> p) {
		
		final Parser<List<A>> many = 
			p.bind(x -> 
				many(p).bind(xs -> {
					return result(io.vavr.collection.List.ofAll(xs).insert(0, x).toJavaList());
				})
			);
		
		return many.plus(result(new ArrayList<>()));
	}

	public static <A> Parser<List<A>> many1(Parser<A> p) {
		
		return
			p.bind(x -> 
				many(p).bind(xs -> {
					return result(io.vavr.collection.List.ofAll(xs).insert(0, x).toJavaList());
				})
			);
	}
	
	public static Parser<Integer> nat() {
		return 
			many1(digit())
				.bind(digits -> result(Integer.parseInt(CharSeq.ofAll(digits).mkString())));
	}
	
	public static Parser<Integer> integer() {
		
		final Function<Integer, Integer> negate 
			= number -> -1 * number;
		
		final Parser<Function<Integer, Integer>> op 
			= character('-')
				.bind(ingored -> result(negate))
				.plus(result(Function.identity()));
		
		return
			op.bind(f -> 
				nat().bind(n -> 
					result(f.apply(n))));
	}

	public static Parser<List<Integer>> ints() {
		
		return character('[').bind(open ->
					integer().bind(n -> 
						many(character(',').bind(comma -> integer().bind(x -> result(x)))).bind(ns -> 
							character(']').bind(close -> 
								result(io.vavr.collection.List.of(n).appendAll(ns).toJavaList())))));
	}

	public default <B> Parser<List<A>> sepby1(Parser<B> sep) {

		return this.bind(x ->
					many(sep.bind(s ->
						this.bind(y -> result(y)))).bind(xs ->
							result(io.vavr.collection.List.of(x).appendAll(xs).toJavaList())));
	}

	public static Parser<List<Integer>> ints_() {
		
		return character('[').bind(open ->
					integer().sepby1(character(',')).bind(ns ->
							character(']').bind(close -> 
								result(ns))));
	}	

	public static <A, B, C> Parser<B> brackets(Parser<A> open, Parser<B> p, Parser<C> close) {
		
		return open.bind(o ->
					p.bind(x -> 
						close.bind(c ->
							result(x)
						)
					)
				);
	}

	public static Parser<List<Integer>> ints__() {
		return brackets(character('['), integer().sepby1(character(',')), character(']'));
	}	

	public default <B> Parser<List<A>> sepby(Parser<B> sep) {
		return this.sepby1(sep).plus(result(List.of()));
	}
	
	public List<Tuple2<A, String>> parse(String input);
}
