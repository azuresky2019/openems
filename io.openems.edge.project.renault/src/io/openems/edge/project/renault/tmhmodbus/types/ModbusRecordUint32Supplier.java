package io.openems.edge.project.renault.tmhmodbus.types;

import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordUint32Supplier extends ModbusRecordSupplier<Integer> {

	public ModbusRecordUint32Supplier(int offset, String name, OpenemsSupplier<Integer> function) {
		super(offset, name, ModbusType.UINT32, function);
	}

	@Override
	public String toString() {
		return "ModbusRecordUint32Supplier [offset=" + this.getOffset() + "]";
	}

}
