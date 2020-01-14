package io.openems.edge.project.renault.tmhmodbus.types;

import java.util.function.Supplier;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;

/**
 * Represents a supplier of results that can throw an
 * {@link OpenemsNamedException}.
 *
 * <p>
 * This is derived from {@link Supplier}.
 *
 * @param <T> the type of results supplied by this supplier
 *
 * @since 1.8
 */
@FunctionalInterface
public interface OpenemsSupplier<T> {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	T get() throws OpenemsNamedException;

}
