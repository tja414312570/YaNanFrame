package com.YaNan.frame.servlets.session.entity;

public class TokenBean {
	private String namespace;
	private String role;
	private Failed failed;
	private String value;
	private String chain;
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Failed getFailed() {
		return failed;
	}

	public void setFailed(Failed failed) {
		this.failed = failed;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public String toString() {
		return "TokenBean [namespace=" + namespace + ", role=" + role
				+ ", failed=" + failed + ", value=" + value + ", chain="
				+ chain + "]";
	}

	public String getChain() {
		return chain;
	}

	public void setChain(String chain) {
		this.chain = chain;
	}
}