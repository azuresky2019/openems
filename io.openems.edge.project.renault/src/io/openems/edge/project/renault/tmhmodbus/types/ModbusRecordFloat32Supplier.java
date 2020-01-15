package io.openems.edge.project.renault.tmhmodbus.types;

import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordFloat32Supplier extends ModbusRecordSupplier<Float> {

	public ModbusRecordFloat32Supplier(int offset, String name, OpenemsSupplier<Float> function) {
		super(offset, name, ModbusType.FLOAT32, function);
	}

	@Override
	public String toString() {
		return "ModbusRecordFloat32Supplier [offset=" + this.getOffset() + "]";
	}

}
