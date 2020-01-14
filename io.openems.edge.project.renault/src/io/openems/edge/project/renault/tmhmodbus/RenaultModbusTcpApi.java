package io.openems.edge.project.renault.tmhmodbus;

import java.util.TreeMap;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;

import io.openems.common.channel.Level;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.common.types.ChannelAddress;
import io.openems.common.worker.AbstractWorker;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.WriteChannel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusRecord;
import io.openems.edge.common.modbusslave.ModbusRecordChannel;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.controller.api.common.ApiWorker;
import io.openems.edge.controller.api.common.WritePojo;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Project.Renault.Controller.Api.ModbusTcp", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE)
public class RenaultModbusTcpApi extends AbstractOpenemsComponent implements Controller, OpenemsComponent {

	public static final int UNIT_ID = 1;
	public static final int DEFAULT_PORT = 502;
	public static final int DEFAULT_MAX_CONCURRENT_CONNECTIONS = 5;

	private final Logger log = LoggerFactory.getLogger(RenaultModbusTcpApi.class);

	private final ApiWorker apiWorker = new ApiWorker();
	private final MyProcessImage processImage;

	/**
	 * Holds the link between Modbus address and ModbusRecord.
	 */
	protected final TreeMap<Integer, ModbusRecord> records = new TreeMap<>();

	@Reference
	protected ComponentManager componentManager;

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		UNABLE_TO_START(Doc.of(Level.FAULT) //
				.text("Unable to start Modbus/TCP-Api Server"));

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}

	private Config config = null;

	public RenaultModbusTcpApi() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				ChannelId.values() //
		);

		this.processImage = new MyProcessImage(this);
	}

	@Activate
	void activate(ComponentContext context, Config config) throws ModbusException, OpenemsException {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;

		this.apiWorker.setTimeoutSeconds(config.apiTimeout());

		if (!this.isEnabled()) {
			// abort if disabled
			return;
		}

		// Initialize Modbus Records
		this.initalizeModbusRecords(MyModbusSlaveDefinition.getModbusSlaveDefinition(config, this.componentManager));

		// Start Modbus-Server
		this.startApiWorker.activate(config.id());
	}

	@Deactivate
	protected void deactivate() {
		this.startApiWorker.deactivate();
		ModbusSlaveFactory.close();
		super.deactivate();
	}

	private final AbstractWorker startApiWorker = new AbstractWorker() {

		private static final int DEFAULT_WAIT_TIME = 5000; // 5 seconds

		private final Logger log = LoggerFactory.getLogger(AbstractWorker.class);

		private com.ghgande.j2mod.modbus.slave.ModbusSlave slave = null;

		@Override
		protected void forever() {
			int port = RenaultModbusTcpApi.this.config.port();
			if (this.slave == null) {
				try {
					// start new server
					this.slave = ModbusSlaveFactory.createTCPSlave(port,
							RenaultModbusTcpApi.this.config.maxConcurrentConnections());
					slave.addProcessImage(UNIT_ID, RenaultModbusTcpApi.this.processImage);
					slave.open();

					RenaultModbusTcpApi.this.logInfo(this.log, "Modbus/TCP Api started on port [" + port
							+ "] with UnitId [" + RenaultModbusTcpApi.UNIT_ID + "].");
					RenaultModbusTcpApi.this.channel(ChannelId.UNABLE_TO_START).setNextValue(false);
				} catch (ModbusException e) {
					ModbusSlaveFactory.close();
					RenaultModbusTcpApi.this.logError(this.log,
							"Unable to start Modbus/TCP Api on port [" + port + "]: " + e.getMessage());
					RenaultModbusTcpApi.this.channel(ChannelId.UNABLE_TO_START).setNextValue(true);
				}

			} else {
				// regular check for errors
				String error = slave.getError();
				if (error != null) {
					RenaultModbusTcpApi.this.logError(this.log,
							"Unable to start Modbus/TCP Api on port [" + port + "]: " + error);
					RenaultModbusTcpApi.this.channel(ChannelId.UNABLE_TO_START).setNextValue(true);
					this.slave = null;
					// stop server
					ModbusSlaveFactory.close();
				}
			}
		}

		@Override
		protected int getCycleTime() {
			return DEFAULT_WAIT_TIME;
		}

	};

	/**
	 * Prepares the Modbus Slave Definition for usage by {@link MyProcessImage}.
	 * 
	 * @param modbusSlaveDefinition the modbus slave definition
	 */
	private void initalizeModbusRecords(TreeMap<Integer, ModbusRecord> records) {
		for (ModbusRecord record : records.values()) {
			// Handle writes to the Channel; limited to ModbusRecordChannels
			if (record instanceof ModbusRecordChannel) {
				ModbusRecordChannel r = (ModbusRecordChannel) record;
				r.onWriteValue((value) -> {
					ChannelAddress channelAddress = new ChannelAddress(r.getComponentId(), r.getChannelId().id());
					Channel<?> readChannel;
					try {
						readChannel = this.componentManager.getChannel(channelAddress);
					} catch (IllegalArgumentException | OpenemsNamedException e) {
						this.logWarn(this.log, "Unable to get Channel [" + channelAddress + "]");
						return;
					}
					if (!(readChannel instanceof WriteChannel)) {
						this.logWarn(this.log, "Unable to write to Read-Only-Channel [" + readChannel.address() + "]");
						return;
					}
					WriteChannel<?> channel = (WriteChannel<?>) readChannel;
					this.apiWorker.addValue(channel, new WritePojo(value));
				});
			}
		}
		this.records.clear();
		this.records.putAll(records);
	}

	@Override
	public void run() throws OpenemsNamedException {
		this.apiWorker.run();
	}

	@Override
	protected void logDebug(Logger log, String message) {
		super.logDebug(log, message);
	}

	@Override
	protected void logInfo(Logger log, String message) {
		super.logInfo(log, message);
	}

	@Override
	protected void logWarn(Logger log, String message) {
		super.logWarn(log, message);
	}
}
