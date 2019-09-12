package io.openems.edge.ess.mr.gridcon;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import io.openems.edge.ess.mr.gridcon.enums.InverterCount;

@ObjectClassDefinition( //
		name = "ESS MR Gridcon PCS", //
		description = "Implements the FENECON MR Gridcon PCS system")
public
@interface Config {
	String id() default "ess0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "Modbus-ID", description = "ID of Modbus brige.")
	String modbus_id() default "modbus0";

	@AttributeDefinition(name = "Modbus-Unit-ID", description = "Unit ID of Modbus brige.")
	int unit_id() default 0;

	@AttributeDefinition(name = "BatteryStringA", description = "ID of battery connected to string A.")
	String batteryStringA_id();

	@AttributeDefinition(name = "BatteryStringB", description = "ID of battery connected to string B.")
	String batteryStringB_id();

	@AttributeDefinition(name = "BatteryStringC", description = "ID of battery connected to string C.")
	String batteryStringC_id();

	@AttributeDefinition(name = "IPUs", description = "Which IPUs are used, InverterCount 4 is DC DC Converter", required = true)
	InverterCount inverterCount() default InverterCount.ONE;
	
	@AttributeDefinition(name = "Enable IPU 1", description = "IPU 1 is enabled")
	boolean enableIPU1() default true;
	
	@AttributeDefinition(name = "Enable IPU 2", description = "IPU 2 is enabled")
	boolean enableIPU2() default false;
	
	@AttributeDefinition(name = "Enable IPU 3", description = "IPU 3 is enabled")
	boolean enableIPU3() default false;

	@AttributeDefinition(name = "MinSoCA", description = "Minimal SoC of Battery String A, if reached no further discharging is allowed")
	int minSocBatteryA() default 25;

	@AttributeDefinition(name = "MinSoCB", description = "Minimal SoC of Battery String B, if reached no further discharging is allowed")
	int minSocBatteryB() default 25;

	// TODO Component was not able to start because key 'minSocC' exists already...
	// renaming to 'minSocBatteryC' works
	@AttributeDefinition(name = "MinSoCC", description = "Minimal SoC of Battery String C, if reached no further discharging is allowed")
	int minSocBatteryC() default 25;

	@AttributeDefinition(name = "WeightFactorBatteryA", description = "Weight factor for battery on string A, if more than one battery is connected, it is necessary to set the correct weighting for the battery strings to avoid battery damages. The total voltage of this battery is divided by this factor and compared to the others. An appropriate factor is the number of modules in the battery rack.")
	double weightFactorBatteryA() default 20;

	@AttributeDefinition(name = "WeightFactorBatteryB", description = "Weight factor for battery on string B, if more than one battery is connected, it is necessary to set the correct weighting for the battery strings to avoid battery damages. The total voltage of this battery is divided by this factor and compared to the others. An appropriate factor is the number of modules in the battery rack.")
	double weightFactorBatteryB() default 20;

	@AttributeDefinition(name = "WeightFactorBatteryC", description = "Weight factor for battery on string C, if more than one battery is connected, it is necessary to set the correct weighting for the battery strings to avoid battery damages. The total voltage of this battery is divided by this factor and compared to the others. An appropriate factor is the number of modules in the battery rack.")
	double weightFactorBatteryC() default 20;

	@AttributeDefinition(name = "OverFrequency", description = "Frequency in millihertz that is added to grid frequency when going on grid")
	int overFrequency() default 200;

	@AttributeDefinition(name = "OverVoltage", description = "Voltage in millivolt that is added to grid voltage when going on grid")
	int overVoltage() default 2000;

	@AttributeDefinition(name = "Grid-Meter-ID", description = "ID of Grid-Meter")
	String meter() default "meter0";

	@AttributeDefinition(name = "NA-Protection Relais 1 (K1)", description = "Address of the NA-Protection Relais 1 channel.")
	String inputNAProtection1();

	@AttributeDefinition(name = "NA-Protection Relais 2 (K2)", description = "Address of the NA-Protection Relais 2 channel.")
	String inputNAProtection2();

	@AttributeDefinition(name = "Sync-Device Bridge Input (K3)", description = "Address of the Sync-Device Bridge Contact Input channel.")
	String inputSyncDeviceBridge();

	@AttributeDefinition(name = "Sync-Device Bridge Output (K3)", description = "Address of the Sync-Device Bridge Contact Output channel.")
	String outputSyncDeviceBridge();

	@AttributeDefinition(name = "MR Hard Reset Output (K4)", description = "Address of the MR Hard Reset Output Output channel.")
	String outputMRHardReset();

	@AttributeDefinition(name = "Modbus target filter", description = "This is auto-generated by 'Modbus-ID'.")
	String Modbus_target() default "";

	String webconsole_configurationFactory_nameHint() default "ESS MR Gridcon PCS [{id}]";
}
