package com.YaNan.frame.net;

public interface NetListener {
	public void onResponse(HttpAsync httpAsync);

	public void onError(Exception exception);

	public void Progress(int current, int total);

	public void completed();
}