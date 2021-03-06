package io.openems.edge.controller.ess.limitdischargecellvoltage.helper;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.ComponentContext;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.jsonrpc.base.JsonrpcRequest;
import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;
import io.openems.common.session.User;
import io.openems.common.types.EdgeConfig;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.api.SymmetricEss;

public class DummyComponentManager implements ComponentManager {

	private ManagedSymmetricEss ess = createEss();

	@SuppressWarnings("unchecked")
	@Override
	public <T extends OpenemsComponent> T getComponent(String componentId) throws OpenemsNamedException {
		if (CreateTestConfig.ESS_ID.equals(componentId)) {
			return (T) ess;
		}
		return null;
	}

	private ManagedSymmetricEss createEss() {

		return new DummyEss(OpenemsComponent.ChannelId.values(), //
				SymmetricEss.ChannelId.values(), //
				ManagedSymmetricEss.ChannelId.values() // ;
		);
	}

	@Override
	public CompletableFuture<JsonrpcResponseSuccess> handleJsonrpcRequest(User user, JsonrpcRequest request)
			throws OpenemsNamedException {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public String id() {
		return null;
	}

	@Override
	public ComponentContext getComponentContext() {
		return null;
	}

	@Override
	public Collection<Channel<?>> channels() {
		return null;
	}

	@Override
	public String alias() {
		return null;
	}

	@Override
	public Channel<?> _channel(String channelName) {
		return null;
	}

	@Override
	public List<OpenemsComponent> getEnabledComponents() {
		return null;
	}

	@Override
	public EdgeConfig getEdgeConfig() {
		return null;
	}

	@Override
	public List<OpenemsComponent> getAllComponents() {
		return null;
	}

	public void destroyEss() {
		this.ess = null;
	}
}
