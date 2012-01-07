package net.homelinux.md401.magus;

public interface FunctionThrows<F, T, E extends Throwable> {

	  T apply(F from) throws E;
}
