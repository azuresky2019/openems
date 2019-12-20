package io.openems.edge.io.shelly.shellyplug;

import java.util.Objects;
import java.util.Optional;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.common.channel.BooleanWriteChannel;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.StateChannel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.io.api.DigitalOutput;
import io.openems.edge.io.shelly.common.ShellyApi;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "IO.Shelly.Plug", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE, property = { //
				EventConstants.EVENT_TOPIC + "=" + EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
				EventConstants.EVENT_TOPIC + "=" + EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE //
		})
public class ShellyPlug extends AbstractOpenemsComponent implements DigitalOutput, OpenemsComponent, EventHandler {

	private final Logger log = LoggerFactory.getLogger(ShellyPlug.class);

	private final BooleanWriteChannel[] digitalOutputChannels;
	private ShellyApi shellyApi = null;

	public ShellyPlug() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				DigitalOutput.ChannelId.values(), //
				ThisChannelId.values() //
		);
		this.digitalOutputChannels = new BooleanWriteChannel[] { //
				this.channel(ThisChannelId.RELAY) //
		};
	}

	@Activate
	void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.shellyApi = new ShellyApi(config.ip());
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public BooleanWriteChannel[] digitalOutputChannels() {
		return this.digitalOutputChannels;
	}

	@Override
	public String debugLog() {
		StringBuilder b = new StringBuilder();
		Optional<Boolean> valueOpt = this.getRelayChannel().value().asOptional();
		if (valueOpt.isPresent()) {
			b.append(valueOpt.get() ? "On" : "Off");
		} else {
			b.append("Unknown");
		}
		b.append("|");
		b.append(this.getActivePowerChannel().value().asString());
		return b.toString();
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}

		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			this.eventBeforeProcessImage();
			break;

		case EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE:
			this.eventExecuteWrite();
			break;
		}
	}

	private BooleanWriteChannel getRelayChannel() {
		return this.channel(ThisChannelId.RELAY);
	}

	private IntegerReadChannel getActivePowerChannel() {
		return this.channel(ThisChannelId.ACTIVE_POWER);
	}

	private StateChannel getSlaveCommunicationFailedChannel() {
		return this.channel(ThisChannelId.SLAVE_COMMUNICATION_FAILED);
	}

	private void setSlaveCommunicationFailed(boolean value) {
		this.getSlaveCommunicationFailedChannel().setNextValue(value);
	}

	/**
	 * Execute on Cycle Event "Before Process Image".
	 */
	private void eventBeforeProcessImage() {
		Boolean relayIson = null;
		Float power = null;
		try {
			JsonObject json = this.shellyApi.getStatus();
			JsonArray relays = JsonUtils.getAsJsonArray(json, "relays");
			JsonObject relay1 = JsonUtils.getAsJsonObject(relays.get(0));
			relayIson = JsonUtils.getAsBoolean(relay1, "ison");
			JsonArray meters = JsonUtils.getAsJsonArray(json, "meters");
			JsonObject meter1 = JsonUtils.getAsJsonObject(meters.get(0));
			power = JsonUtils.getAsFloat(meter1, "power");

			this.setSlaveCommunicationFailed(false);

		} catch (OpenemsNamedException e) {
			this.logError(this.log, "Unable to read from Shelly API: " + e.getMessage());
			this.setSlaveCommunicationFailed(true);
		}
		this.getRelayChannel().setNextValue(relayIson);
		this.getActivePowerChannel().setNextValue(power);
	}

	/**
	 * Execute on Cycle Event "Execute Write".
	 */
	private void eventExecuteWrite() {
		try {
			this.executeWrite(this.getRelayChannel(), 0);

			this.setSlaveCommunicationFailed(false);
		} catch (OpenemsNamedException e) {
			this.setSlaveCommunicationFailed(true);
		}
	}

	private void executeWrite(BooleanWriteChannel channel, int index) throws OpenemsNamedException {
		Boolean readValue = channel.value().get();
		Optional<Boolean> writeValue = channel.getNextWriteValueAndReset();
		if (!writeValue.isPresent()) {
			// no write value
			return;
		}
		if (Objects.equals(readValue, writeValue.get())) {
			// read value = write value
			return;
		}
		this.shellyApi.setRelayTurn(index, writeValue.get());
	}

}