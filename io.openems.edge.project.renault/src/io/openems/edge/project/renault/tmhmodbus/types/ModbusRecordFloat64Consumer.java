package io.openems.edge.project.renault.tmhmodbus.types;

import java.nio.ByteBuffer;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordFloat64Consumer extends ModbusRecordConsumer<Double> {

	private final OpenemsConsumer<Double> function;

	public ModbusRecordFloat64Consumer(int offset, String name, OpenemsConsumer<Double> function) {
		super(offset, name, ModbusType.FLOAT64, function);
		this.function = function;
	}

	@Override
	public String toString() {
		return "ModbusRecordFloat64Consumer [offset=" + this.getOffset() + "]";
	}

	@Override
	protected void callConsumer(ByteBuffer buff) throws OpenemsNamedException {
		this.function.accept(buff.getDouble());
	}

}
