package io.openems.edge.project.renault.tmhmodbus.types;

import java.nio.ByteBuffer;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusRecordUint16Consumer extends ModbusRecordConsumer<Short> {

	private final OpenemsConsumer<Short> function;

	public ModbusRecordUint16Consumer(int offset, String name, OpenemsConsumer<Short> function) {
		super(offset, name, ModbusType.UINT16, function);
		this.function = function;
	}

	@Override
	public String toString() {
		return "ModbusRecordUint16Consumer [offset=" + this.getOffset() + "]";
	}

	@Override
	protected void callConsumer(ByteBuffer buff) throws OpenemsNamedException {
		this.function.accept(buff.getShort());
	}

}
