package com.github.johnmcguinness.fp.monparse;

import io.vavr.Tuple2;
import java.util.List;
import java.util.function.Function;

public interface Parser<A> extends Function<String, List<Tuple2<A, String>>> { }
