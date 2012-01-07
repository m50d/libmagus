package net.homelinux.md401.magus;

import java.io.File;
import java.io.IOException;

import net.homelinux.md401.magus.anidb.ConnectionWrapper;
import net.homelinux.md401.magus.ed2k.JacksumWrapper;

public class FileHandler {
	private final ConnectionWrapper connectionWrapper = new ConnectionWrapper();

	public void addFile(final CharSequence username, final CharSequence password, final boolean watched, final File data) throws FailureException {
		try {
			final String hash = JacksumWrapper.ed2k(data.getAbsolutePath());
			connectionWrapper.add(username.toString(), password.toString(), (int) data.length(), hash, watched);
		} catch (final IOException e) {
			throw new FailureException("Unable to read file:" + data.getAbsolutePath(), e);
		}
	}
	
	public static void main(String args[]) throws FailureException {
		new FileHandler().addFile(args[0], args[1], true, new File(args[2]));
	}
}
