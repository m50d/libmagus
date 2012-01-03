package net.homelinux.md401.magus.anidb;

import java.util.Set;

import net.anidb.udp.AniDbException;
import net.anidb.udp.ConnectionAccessor;
import net.anidb.udp.UdpConnection;
import net.anidb.udp.UdpConnectionException;
import net.anidb.udp.UdpConnectionFactory;
import net.anidb.udp.UdpRequest;
import net.anidb.udp.UdpResponse;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

public class ConnectionWrapper {
	private final UdpConnectionFactory factory;

	public ConnectionWrapper() {
		factory = UdpConnectionFactory.getInstance();
	}

	<T> T perform(final Function<UdpConnection, T> toInvoke)
			throws UdpConnectionException, AniDbException {
		final UdpConnection connection = factory.connect(1074);
		final T ret = toInvoke.apply(connection);
		connection.close();
		return ret;
	}

	<T> T performWithAuthentication(final String username,
			final String password, final Function<UdpConnection, T> toInvoke)
			throws UdpConnectionException, AniDbException {
		return perform(new Function<UdpConnection, T>() {

			@Override
			public T apply(final UdpConnection arg0) {
				try {
					arg0.authenticate(username, password);
				} catch (final AniDbException e) {
					throw new RuntimeException(e);
				} catch (final UdpConnectionException e) {
					throw new RuntimeException(e);
				}
				return toInvoke.apply(arg0);
			}
		});
	}

	private static final Set<Integer> SUCCESS_CODES = ImmutableSet.of(210, 310);

	public void add(final String username, final String password,
			final int fileSize, final String ed2kHash, final boolean watched)
			throws UdpConnectionException, AniDbException {
		performWithAuthentication(username, password,
				new Function<UdpConnection, Void>() {

					@Override
					public Void apply(final UdpConnection from) {
						final UdpRequest request = new UdpRequest("MYLISTADD") {
						};
						request.addParameter("size", fileSize);
						request.addParameter("ed2k", ed2kHash);
						request.addParameter("viewed", watched);
						UdpResponse response;
						try {
							response = ConnectionAccessor.communicate(from,
									request);
						} catch (final UdpConnectionException e) {
							throw new RuntimeException(e);
						}
						switch (response.getReturnCode()) {
						case 210:
							return null;
						case 310:
							request.addParameter("edit", true);
							try {
								response = ConnectionAccessor.communicate(from,
										request);
							} catch (UdpConnectionException e) {
								throw new RuntimeException(e);
							}
							if (311 == response.getReturnCode())
								return null;
						default:
							throw new RuntimeException("Error code: "
									+ response.getReturnCode() + ", message: "
									+ response.getMessageString());
						}
					}
				});
	}
}
