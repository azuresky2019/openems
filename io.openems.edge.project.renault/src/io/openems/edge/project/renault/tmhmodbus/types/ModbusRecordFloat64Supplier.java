package io.openems.edge.project.renault.tmhmodbus.types;

import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordFloat64Supplier extends ModbusRecordSupplier<Double> {

	public ModbusRecordFloat64Supplier(int offset, String name, OpenemsSupplier<Double> function) {
		super(offset, name, ModbusType.FLOAT64, function);
	}

	@Override
	public String toString() {
		return "ModbusRecordFloat64Supplier [offset=" + this.getOffset() + "]";
	}

}
