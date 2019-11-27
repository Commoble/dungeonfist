package com.github.commoble.dungeonfist.util.functions;

@FunctionalInterface
public interface IOFunction<T, R, E extends Throwable>
{
	R apply(T t) throws E;
}
