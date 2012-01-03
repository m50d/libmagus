package net.homelinux.md401.magus;

import java.io.File;
import java.io.IOException;

import net.anidb.udp.AniDbException;
import net.anidb.udp.UdpConnectionException;
import net.homelinux.md401.magus.anidb.ConnectionWrapper;
import net.homelinux.md401.magus.ed2k.JacksumWrapper;

public class FileHandler {
	private final ConnectionWrapper connectionWrapper = new ConnectionWrapper();

	public void addFile(final String username, final String password, final boolean watched, final int length, final File data) {
		try {
			final String hash = JacksumWrapper.ed2k(data.getPath());
			connectionWrapper.add(username, password, length, hash, watched);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final UdpConnectionException e) {
			throw new RuntimeException(e);
		} catch (final AniDbException e) {
			throw new RuntimeException(e);
		}
	}
}
