package net.homelinux.md401.magus;

public class FailureException extends Exception {
	private static final long serialVersionUID = 1L;
	public final String detail;

	public FailureException(String detail, Throwable cause) {
		super(detail, cause);
		this.detail = detail;
	}

	public FailureException(String detail) {
		super(detail);
		this.detail = detail;
	}

}
