package io.nutz.demo.zbus.rpc.service;

import io.zbus.rpc.Remote;

@Remote
public interface TimeService {

	long now();
}
