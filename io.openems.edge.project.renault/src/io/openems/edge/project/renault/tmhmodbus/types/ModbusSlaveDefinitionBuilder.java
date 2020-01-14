package io.openems.edge.project.renault.tmhmodbus.types;

import java.util.TreeMap;

import io.openems.common.channel.AccessMode;
import io.openems.edge.common.channel.ChannelId;
import io.openems.edge.common.modbusslave.ModbusRecord;
import io.openems.edge.common.modbusslave.ModbusRecordChannel;
import io.openems.edge.common.modbusslave.ModbusRecordFloat32;
import io.openems.edge.common.modbusslave.ModbusRecordFloat32Reserved;
import io.openems.edge.common.modbusslave.ModbusRecordFloat64;
import io.openems.edge.common.modbusslave.ModbusRecordFloat64Reserved;
import io.openems.edge.common.modbusslave.ModbusRecordString16;
import io.openems.edge.common.modbusslave.ModbusRecordString16Reserved;
import io.openems.edge.common.modbusslave.ModbusRecordUint16;
import io.openems.edge.common.modbusslave.ModbusRecordUint16Reserved;
import io.openems.edge.common.modbusslave.ModbusRecordUint32;
import io.openems.edge.common.modbusslave.ModbusType;

public class ModbusSlaveDefinitionBuilder {

	private final TreeMap<Integer, ModbusRecord> records = new TreeMap<Integer, ModbusRecord>();

	// next offset in the 30000 range
	private int nextOffset30000 = 30_000;

	private ModbusSlaveDefinitionBuilder() {
	}

	public ModbusSlaveDefinitionBuilder channel(int offset, ChannelId channelId, ModbusType type) {
		this.add(new ModbusRecordChannel(offset, type, channelId, AccessMode.READ_WRITE));
		return this;
	}

	public ModbusSlaveDefinitionBuilder uint16(int offset, String name, short value) {
		this.add(new ModbusRecordUint16(offset, name, value));
		return this;
	}

	public ModbusSlaveDefinitionBuilder uint16Supplier(int offset, String name, OpenemsSupplier<Short> function) {
		this.add(new ModbusRecordUint16ReadOnly(offset, name, function));
		return this;
	}

	public ModbusSlaveDefinitionBuilder uint32(int offset, String name, int value) {
		this.add(new ModbusRecordUint32(offset, name, value));
		return this;
	}

	public ModbusSlaveDefinitionBuilder uint16Reserved(int offset) {
		this.add(new ModbusRecordUint16Reserved(offset));
		return this;
	}

	public ModbusSlaveDefinitionBuilder float32(int offset, String name, float value) {
		this.add(new ModbusRecordFloat32(offset, name, value));
		return this;
	}

	public ModbusSlaveDefinitionBuilder float32Reserved(int offset) {
		this.add(new ModbusRecordFloat32Reserved(offset));
		return this;
	}

	public ModbusSlaveDefinitionBuilder float64(int offset, String name, double value) {
		this.add(new ModbusRecordFloat64(offset, name, value));
		return this;
	}

	public ModbusSlaveDefinitionBuilder float64Reserved(int offset) {
		this.add(new ModbusRecordFloat64Reserved(offset));
		return this;
	}

	public ModbusSlaveDefinitionBuilder string16(int offset, String name, String value) {
		this.add(new ModbusRecordString16(offset, name, value));
		return this;
	}

	public ModbusSlaveDefinitionBuilder string16Reserved(int offset) {
		this.add(new ModbusRecordString16Reserved(offset));
		return this;
	}

	private void add(ModbusRecord record) throws IllegalArgumentException {
		Integer nextOffset = null;
		if (record.getOffset() >= 30_000) {
			nextOffset = this.nextOffset30000;
			this.nextOffset30000 += record.getType().getWords();
		}
		if (nextOffset == null) {
			throw new IllegalArgumentException(
					"Offset [" + record.getOffset() + "] is not in range for Record [" + record + "]");
		}
		if (record.getOffset() != nextOffset) {
			throw new IllegalArgumentException("Expected offset [" + nextOffset + "] but got [" + record.getOffset()
					+ "] for Record [" + record + "]");
		}
		// TODO validate that this 'offset' is not already used (e.g. by float32)
		this.records.put(record.getOffset(), record);
	}

	public TreeMap<Integer, ModbusRecord> build() {
		return this.records;
	}

	public static ModbusSlaveDefinitionBuilder of() {
		return new ModbusSlaveDefinitionBuilder();
	}

}
