package io.openems.edge.project.renault.tmhmodbus.types;

import java.nio.ByteBuffer;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordUint32Consumer extends ModbusRecordConsumer<Integer> {

	private final OpenemsConsumer<Integer> function;

	public ModbusRecordUint32Consumer(int offset, String name, OpenemsConsumer<Integer> function) {
		super(offset, name, ModbusType.UINT32, function);
		this.function = function;
	}

	@Override
	public String toString() {
		return "ModbusRecordUint32Consumer [offset=" + this.getOffset() + "]";
	}

	@Override
	protected void callConsumer(ByteBuffer buff) throws OpenemsNamedException {
		this.function.accept(buff.getInt());
	}

}
