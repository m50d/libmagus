package net.homelinux.md401.magus;

import java.io.File;
import java.io.IOException;

import net.anidb.udp.AniDbException;
import net.anidb.udp.UdpConnectionException;
import net.homelinux.md401.magus.anidb.ConnectionWrapper;
import net.homelinux.md401.magus.ed2k.JacksumWrapper;

public class FileHandler {
	private final ConnectionWrapper connectionWrapper = new ConnectionWrapper();

	public void addFile(final String username, final String password, final boolean watched, final File data) {
		try {
			final String hash = JacksumWrapper.ed2k(data.getAbsolutePath());
			connectionWrapper.add(username, password, (int) data.length(), hash, watched);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final UdpConnectionException e) {
			throw new RuntimeException(e);
		} catch (final AniDbException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String args[]) {
		new FileHandler().addFile(args[0], args[1], true, new File(args[2]));
	}
}
