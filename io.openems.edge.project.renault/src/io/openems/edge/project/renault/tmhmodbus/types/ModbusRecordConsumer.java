package io.openems.edge.project.renault.tmhmodbus.types;

import java.nio.ByteBuffer;

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
 * Wraps a writable {@link ModbusRecord} with a {@link OpenemsConsumer}
 * function.
 *
 * @param <T> the type of the function
 */
public abstract class ModbusRecordConsumer<T> extends ModbusRecord {

	private final Logger log = LoggerFactory.getLogger(ModbusRecordConsumer.class);
	private final String name;

	/**
	 * this is used to buffer calls to writeValue(). Once the buffer is full, the
	 * value is actually forwarded to the channel.
	 */
	private final Byte[] writeValueBuffer;

	public ModbusRecordConsumer(int offset, String name, ModbusType type, OpenemsConsumer<T> function) {
		super(offset, type);
		this.name = name;

		// initialize buffer
		int byteLength = 0;
		switch (this.getType()) {
		case FLOAT32:
			byteLength = ModbusRecordFloat32.BYTE_LENGTH;
			break;
		case FLOAT64:
			byteLength = ModbusRecordFloat64.BYTE_LENGTH;
			break;
		case STRING16:
			byteLength = ModbusRecordString16.BYTE_LENGTH;
			break;
		case UINT16:
			byteLength = ModbusRecordUint16.BYTE_LENGTH;
			break;
		case UINT32:
			byteLength = ModbusRecordUint32.BYTE_LENGTH;
			break;
		}
		this.writeValueBuffer = new Byte[byteLength];
	}

	/**
	 * This implementation is Write-Only: always return UNDEFINED value.
	 */
	@Override
	public byte[] getValue(OpenemsComponent component) {
		switch (this.getType()) {
		case FLOAT32:
			return ModbusRecordFloat32.UNDEFINED_VALUE;
		case FLOAT64:
			return ModbusRecordFloat64.UNDEFINED_VALUE;
		case STRING16:
			return ModbusRecordString16.UNDEFINED_VALUE;
		case UINT16:
			return ModbusRecordUint16.UNDEFINED_VALUE;
		case UINT32:
			return ModbusRecordUint32.UNDEFINED_VALUE;
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
		return AccessMode.WRITE_ONLY;
	}

	protected abstract void callConsumer(ByteBuffer buff) throws OpenemsNamedException;

	@Override
	public void writeValue(OpenemsComponent component, int index, byte byte1, byte byte2) {
		this.writeValueBuffer[index * 2] = byte1;
		this.writeValueBuffer[index * 2 + 1] = byte2;
		// is the buffer full?
		for (int i = 0; i < this.writeValueBuffer.length; i++) {
			if (this.writeValueBuffer[i] == null) {
				return; // no, it is not full
			}
		}

		// yes, it is full -> Prepare ByteBuffer
		ByteBuffer buff = ByteBuffer.allocate(this.writeValueBuffer.length);
		for (int i = 0; i < this.writeValueBuffer.length; i++) {
			buff.put(this.writeValueBuffer[i]);
		}
		buff.rewind();

		// clear buffer
		for (int i = 0; i < this.writeValueBuffer.length; i++) {
			this.writeValueBuffer[i] = null;
		}

		// Forward Value to Consumer
		try {
			this.callConsumer(buff);
		} catch (OpenemsNamedException e) {
			this.log.error("Consumer failed for ByteBuffer [" + buff.toString() + "]: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
