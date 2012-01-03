package net.homelinux.md401.magus.ed2k;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

public class JacksumWrapper {
	private static final ThreadLocal<AbstractChecksum> ed2kChecksum = new ThreadLocal<AbstractChecksum>();

	public static String ed2k(final String path) throws IOException {
		final AbstractChecksum tmpChecksum = ed2kChecksum.get();
		final AbstractChecksum checksum;
		if(tmpChecksum == null) {
			try {
				checksum = JacksumAPI.getChecksumInstance("ed2k");
			} catch (final NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
			ed2kChecksum.set(checksum);
		}
		else checksum = tmpChecksum;
		checksum.readFile(path);
		final String hash = checksum.getFormattedValue();
		checksum.reset();
		return hash;
	}
}
