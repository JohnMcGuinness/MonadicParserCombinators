package com.github.johnmcguinness.fp.monparse;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import java.util.List;

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
			if(input == null || input.isEmpty()) {
				return List.of();
			}
			else {
				final CharSeq chars = CharSeq.of(input);
				return List.of(Tuple.of(chars.head(), chars.tail().mkString()));
			}
		};
	}
	
	public List<Tuple2<A, String>> parse(String input);
}
