package net.anidb.udp;

public final class ConnectionAccessor {
	private ConnectionAccessor(){}
	public static UdpResponse communicate(final UdpConnection connection, final UdpRequest request) throws UdpConnectionException {
		return connection.communicate(request);
	}
}
