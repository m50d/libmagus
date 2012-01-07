package net.homelinux.md401.magus.anidb;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import net.anidb.udp.UdpConnection;
import net.anidb.udp.UdpConnectionFactory;
import net.homelinux.md401.magus.FailureException;
import net.homelinux.md401.magus.FunctionThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UdpConnectionFactory.class)
public class ConnectionWrapperTest {
	private static interface MockFunction extends FunctionThrows<UdpConnection, Object, FailureException>{}
	private UdpConnectionFactory factory;
	private UdpConnection connection;
	private MockFunction function;
	private ConnectionWrapper wrapper;
	private final List<Object> mocks = Lists.newArrayList();

	private <T> T mock(final Class<T> tclass) {
		final T ret = createMock(tclass);
		mocks.add(ret);
		return ret;
	}

	@Before
	public void setUp() {
		PowerMock.mockStatic(UdpConnectionFactory.class);
		factory = mock(UdpConnectionFactory.class);
		expect(UdpConnectionFactory.getInstance()).andReturn(factory);
		connection = mock(UdpConnection.class);
		function = mock(MockFunction.class);
		PowerMock.replay(UdpConnectionFactory.class);
		wrapper = new ConnectionWrapper();
	}

	@Test
	public void invokesSuppliedFunction() throws Exception {
		final Object expected = commonFunctionExpectations();
		replay();
		assertThat(wrapper.perform(function)).isEqualTo(expected);
		verify();
	}

	private Object commonFunctionExpectations() throws Exception {
		final Object expected = new Object();
		expect(factory.connect(anyInt())).andReturn(connection);
		expect(function.apply(connection)).andReturn(expected);
		connection.close();
		return expected;
	}

	@Test
	public void passesUsernameAndPassword() throws Exception {
		final String username="USERNAME";
		final String password="PASSWORD";
		final Object expected = commonFunctionExpectations();
		connection.authenticate(username, password, "magus", 0);
		replay();
		assertThat(wrapper.performWithAuthentication(username, password, function)).isEqualTo(expected);
		verify();
	}

	private void verify() {
		PowerMock.verifyAll();
	}

	private void replay() {
		PowerMock.replayAll(mocks.toArray());
	}
}
