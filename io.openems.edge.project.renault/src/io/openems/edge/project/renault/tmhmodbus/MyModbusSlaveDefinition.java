package io.openems.edge.project.renault.tmhmodbus;

import java.util.TreeMap;

import io.openems.common.OpenemsConstants;
import io.openems.common.channel.Level;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.modbusslave.ModbusRecord;
import io.openems.edge.project.renault.tmhmodbus.types.ModbusSlaveDefinitionBuilder;

public class MyModbusSlaveDefinition {

	/**
	 * Initializes the specific Modbus-Slave table.
	 * 
	 * @return a map of modbus offset by record
	 */
	protected static TreeMap<Integer, ModbusRecord> getModbusSlaveDefinition(ComponentManager cm, int technicalUnitId) {
		return ModbusSlaveDefinitionBuilder.of() //

				/*************
				 * ESS-to-TMH
				 * 
				 * Modbus Function Code 04 "Read Input Registers" with address offset 30000. See
				 * http://www.simplymodbus.ca/FC04.htm
				 *************/

				/*
				 * Technical Unit Level Points
				 */
				.uint32(30000, "Technical Unit ID: Static Value", technicalUnitId) //

				.uint16Supplier(30002, "System Status: System status of the TE, see Valid System States below", () -> {
					Level state = cm.getComponent(OpenemsConstants.SUM_ID).getState().value().asEnum();
					if (state == Level.FAULT) {
						return (short) 200; // "FAULT"
					}
					return (short) 150; // "ON
				}) //
				.build();
	}

}
