package com.YaNan.frame.support.net;

public abstract class Async<Params, Progress, Result> extends Thread {
	private Params[] paramas;

	@SuppressWarnings("unchecked")
	public void proExecute(Params... paramas) {
		this.paramas = paramas;
	}

	@SuppressWarnings("unchecked")
	public abstract Result[] doInBackground(Params... parameters);

	@SuppressWarnings("unchecked")
	public void onProgress(Progress... progress) {
	}

	@SuppressWarnings("unchecked")
	public void onExecuted(Result... results) {
	}

	public void execute() {
		this.start();
	}


	@Override
	public void run() {
		Result[] r = this.doInBackground(paramas);
		onExecuted(r);
	}
}
