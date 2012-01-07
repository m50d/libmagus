package net.homelinux.md401.magus.anidb;

import net.anidb.udp.AniDbException;
import net.anidb.udp.ConnectionAccessor;
import net.anidb.udp.UdpConnection;
import net.anidb.udp.UdpConnectionException;
import net.anidb.udp.UdpConnectionFactory;
import net.anidb.udp.UdpRequest;
import net.anidb.udp.UdpResponse;
import net.homelinux.md401.magus.FailureException;
import net.homelinux.md401.magus.FunctionThrows;

public class ConnectionWrapper {
	private static void anidbError(final AniDbException e)
			throws FailureException {
		throw new FailureException("Error from anidb: " + e.getMessageString(), e);
	}
	private final UdpConnectionFactory factory;

	public ConnectionWrapper() {
		factory = UdpConnectionFactory.getInstance();
	}

	<T> T perform(
			final FunctionThrows<UdpConnection, T, FailureException> toInvoke)
			throws FailureException {
		final T ret;
		try {
			UdpConnection connection = factory.connect(1074);
			 ret = toInvoke.apply(connection);
			try {
				connection.close();
			} catch (AniDbException e) {
				//don't care
			}
		} catch (UdpConnectionException e) {
			throw new FailureException(
					"Could not connect to AniDB. Check your network", e);
		}
		return ret;
	}

	<T> T performWithAuthentication(final String username,
			final String password, final FunctionThrows<UdpConnection, T, FailureException> toInvoke)
			throws FailureException {
		return perform(new FunctionThrows<UdpConnection, T, FailureException>() {

			@Override
			public T apply(final UdpConnection arg0) throws FailureException {
				try {
					arg0.authenticate(username, password);
				} catch (final AniDbException e) {
					anidbError(e);
				} catch (final UdpConnectionException e) {
					throw new FailureException("Connection failure when authenticating", e);
				}
				return toInvoke.apply(arg0);
			}
		});
	}

	public void add(final String username, final String password,
			final int fileSize, final String ed2kHash, final boolean watched)
			throws FailureException {
		performWithAuthentication(username, password,
				new FunctionThrows<UdpConnection, Void, FailureException>() {

					@Override
					public Void apply(final UdpConnection from) throws FailureException {
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
							throw new FailureException("Connection failure when adding file", e);
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
								throw new FailureException("Connection failure when updating file to watched", e);
							}
							if (311 == response.getReturnCode())
								return null;
						default:
							throw new FailureException("Error code: "
									+ response.getReturnCode() + ", message: "
									+ response.getMessageString());
						}
					}
				});
	}
}
