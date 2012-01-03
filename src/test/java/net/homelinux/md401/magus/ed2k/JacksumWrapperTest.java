package net.homelinux.md401.magus.ed2k;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class JacksumWrapperTest {
	@Test
	public void formsCorrectHashOfAzumangaWebDaioh() throws Exception {
		final ClassPathResource resource = new ClassPathResource("Azumanga Daioh - Web.avi");
		final String path = resource.getFile().getAbsolutePath();
		assertThat(JacksumWrapper.ed2k(path)).isEqualTo("9bbe21007a3aeaeb4f56cc818e4531f2");
	}
}
