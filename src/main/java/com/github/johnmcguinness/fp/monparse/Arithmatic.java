package com.github.johnmcguinness.fp.monparse;

import static com.github.johnmcguinness.fp.monparse.Parser.brackets;
import static com.github.johnmcguinness.fp.monparse.Parser.character;
import static com.github.johnmcguinness.fp.monparse.Parser.lazy;
import static com.github.johnmcguinness.fp.monparse.Parser.nat;
import static com.github.johnmcguinness.fp.monparse.Parser.result;
import java.util.function.Function;

public final class Arithmatic {
	
	private static final Parser<Function<Integer, Function<Integer, Integer>>> additionop
			= character('+').bind(ignored -> result(x -> y -> x + y));

	private static final Parser<Function<Integer, Function<Integer, Integer>>> subtractionop
			= character('-').bind(ignored -> result(x -> y -> x - y));

	public static Parser<Integer> expr() {

//		This implementation makes no progress because the first
//		thing it does is to call itself.
//
//		return 
//			expr().bind(x ->
//				addop().bind(f -> 
//					factor().bind(y -> 
//						result(f.apply(x).apply(y)))))
//				.plus(factor());

//		This implentation works. It is the same as chainl1 where p is factor() and op is addop().
//
//		return
//			factor().bind(initial ->
//				many(addop().bind(f -> 
//						factor().bind(y -> 
//							result(Tuple.of(f, y))))).bind(fys -> {
//								final List<Tuple2<Function<Integer, Function<Integer, Integer>>, Integer>> list = List.ofAll(fys);
//								return result(List.ofAll(fys).foldLeft(initial, (acc, fy) -> fy._1.apply(acc).apply(fy._2)));
//							})
//			);

		return factor().chainl1(addop());
	}
	
	public static Parser<Function<Integer, Function<Integer, Integer>>> addop() {
		return additionop.plus(subtractionop);
	}

	public static Parser<Integer> factor() {

		//Because Java is not a lazy language then brackets needs to
		//be wrapped in a structure that will evaluate it lazily;
		return nat().plus(lazy(() -> brackets(character('('), expr(), character(')'))));
	}
}
