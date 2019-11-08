package io.openems.edge.core.host;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.worker.AbstractWorker;

/**
 * This Worker constantly checks if the disk is full. It may be extended in
 * future to check more Host related states.
 */
public class HostWorker extends AbstractWorker {

	private final static int CYCLE_TIME = 300_000; // in ms
	private final static long MINIMUM_FREE_DISK_SPACE = 50 /* MB */ * 1024 /* kB */ * 1024 /* bytes */; // in bytes

	private final Logger log = LoggerFactory.getLogger(HostWorker.class);

	private final Host parent;

	public HostWorker(Host parent) {
		this.parent = parent;
	}

	@Override
	protected void forever() {
		long totalUsableSpace = 0;
		for (Path root : FileSystems.getDefault().getRootDirectories()) {
			try {
				FileStore store = Files.getFileStore(root);
				totalUsableSpace += store.getUsableSpace();
			} catch (IOException e) {
				this.parent.logInfo(log, "Unable to query disk space: " + e.getMessage());
			}
		}

		this.parent.channel(Host.ChannelId.DISK_IS_FULL).setNextValue(totalUsableSpace < MINIMUM_FREE_DISK_SPACE);
	}

	@Override
	protected int getCycleTime() {
		return CYCLE_TIME;
	}

}
