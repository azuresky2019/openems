package io.openems.edge.project.renault.tmhmodbus.types;

import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordUint16ReadOnly extends ModbusRecordReadOnly<Short> {

	public ModbusRecordUint16ReadOnly(int offset, String name, OpenemsSupplier<Short> function) {
		super(offset, name, ModbusType.UINT16, function);
	}

	@Override
	public String toString() {
		return "ModbusRecordUint16ReadOnly [offset=" + this.getOffset() + "]";
	}

}
