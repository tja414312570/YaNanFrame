package com.YaNan.frame.servlets.session.entity;

public class Failed {
		private String command;
		private String value;
		@Override
		public String toString() {
			return "Failed [command=" + command + ", value=" + value + "]";
		}

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

}
