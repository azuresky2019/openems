package io.openems.edge.project.renault.tmhmodbus.types;

import java.nio.ByteBuffer;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordFloat32Consumer extends ModbusRecordConsumer<Float> {

	private final OpenemsConsumer<Float> function;

	public ModbusRecordFloat32Consumer(int offset, String name, OpenemsConsumer<Float> function) {
		super(offset, name, ModbusType.FLOAT32, function);
		this.function = function;
	}

	@Override
	public String toString() {
		return "ModbusRecordFloat32Consumer [offset=" + this.getOffset() + "]";
	}

	@Override
	protected void callConsumer(ByteBuffer buff) throws OpenemsNamedException {
		this.function.accept(buff.getFloat());
	}

}
