package io.openems.edge.project.renault.tmhmodbus.types;

import java.util.function.Consumer;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;

/**
 * Represents an operation that accepts a single input argument, returns no
 * result but can throw an {@link OpenemsNamedException}.
 *
 * <p>
 * This is derived from {@link Consumer}.
 *
 * @param <T> the type of results supplied by this consumer
 *
 * @since 1.8
 */
@FunctionalInterface
public interface OpenemsConsumer<T> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t the input argument
	 * @throws OpenemsNamedException on error
	 */
	void accept(T t) throws OpenemsNamedException;

}
