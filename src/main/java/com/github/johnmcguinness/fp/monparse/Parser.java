package com.github.johnmcguinness.fp.monparse;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
		return input -> {
			final List<Tuple2<A, String>> ps = this.parse(input);
			ps.addAll(p.parse(input));
			return ps;
		};
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
		
		final Parser<String> neWord = 
			letter().bind(x -> 
				word().bind(xs -> 
					result(x + xs)));
		
		return neWord.plus(result(""));
	}

	public List<Tuple2<A, String>> parse(String input);
}
