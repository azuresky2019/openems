package io.openems.edge.project.renault.tmhmodbus.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.channel.AccessMode;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusRecord;
import io.openems.edge.common.modbusslave.ModbusRecordFloat32;
import io.openems.edge.common.modbusslave.ModbusRecordFloat64;
import io.openems.edge.common.modbusslave.ModbusRecordString16;
import io.openems.edge.common.modbusslave.ModbusRecordUint16;
import io.openems.edge.common.modbusslave.ModbusRecordUint32;
import io.openems.edge.common.modbusslave.ModbusType;

/**
 * Wraps a writable {@link ModbusRecord} with a {@link OpenemsSupplier}
 * function.
 *
 * @param <T> the type of the function
 */
public abstract class ModbusRecordSupplier<T> extends ModbusRecord {

	private final Logger log = LoggerFactory.getLogger(ModbusRecordSupplier.class);
	private final String name;
	private final OpenemsSupplier<T> function;

	public ModbusRecordSupplier(int offset, String name, ModbusType type, OpenemsSupplier<T> function) {
		super(offset, type);
		this.name = name;
		this.function = function;
	}

	@Override
	public byte[] getValue(OpenemsComponent component) {
		Object value;
		try {
			value = this.function.get();
		} catch (OpenemsNamedException e) {
			this.log.warn("Unable to get value for " + this.toString() + ": " + e.getMessage());
			return new byte[0];
		}
		switch (this.getType()) {
		case FLOAT32:
			if (value != null) {
				return ModbusRecordFloat32.toByteArray(value);
			} else {
				return ModbusRecordFloat32.UNDEFINED_VALUE;
			}
		case FLOAT64:
			if (value != null) {
				return ModbusRecordFloat64.toByteArray(value);
			} else {
				return ModbusRecordFloat64.UNDEFINED_VALUE;
			}
		case STRING16:
			if (value != null) {
				return ModbusRecordString16.toByteArray(value);
			} else {
				return ModbusRecordString16.UNDEFINED_VALUE;
			}
		case UINT16:
			if (value != null) {
				return ModbusRecordUint16.toByteArray(value);
			} else {
				return ModbusRecordUint16.UNDEFINED_VALUE;
			}
		case UINT32:
			if (value != null) {
				return ModbusRecordUint32.toByteArray(value);
			} else {
				return ModbusRecordUint32.UNDEFINED_VALUE;
			}
		}
		assert true;
		return new byte[0];
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getValueDescription() {
		return "";
	}

	@Override
	public AccessMode getAccessMode() {
		return AccessMode.READ_ONLY;
	}

	@Override
	public void writeValue(OpenemsComponent component, int index, byte byte1, byte byte2) {
		this.log.warn("Writing to Read-Only Modbus Record is not allowed! [" + this.getOffset() + ", " + this.getType()
				+ "]");
	}
}
