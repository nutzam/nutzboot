package org.nutz.boot.starter.beetlsql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.beetl.sql.core.ConnectionSource;
import org.nutz.trans.Trans;

/**
 * 基于Trans的事务实现
 * 
 * @author wendal
 *
 */
public class NutzConnectionSource implements ConnectionSource {

	protected Random r = new Random();
	protected DataSource master;
	protected DataSource[] slaves;
	protected ThreadLocal<Integer> forceStatus = new ThreadLocal<Integer>() {
		protected Integer initialValue() {
			return 0;
		}
	};

	public Connection getMaster() {
		return getConnectionQuite(master);
	}

	public Connection getSlave() {
		// 如果没有slaves,回落到master
		if (slaves == null || slaves.length == 0)
			return getMaster();
		// TODO 用随机数还是轮询呢? 这是个问题
		return getConnectionQuite(slaves[r.nextInt(slaves.length)]);
	}

	public Connection getConn(String sqlId, boolean isUpdate, String sql, List<?> paras) {
		if (isUpdate) // 也就是 非select操作咯
			return getMaster();
		int status = forceStatus.get();
		if (status == 0 || status == 1) {
			return getSlave();
		} else {
			return getMaster();
		}
	}

	public void forceBegin(boolean isMaster) {
		// 初始值0, slave=1, master=2
		forceStatus.set(isMaster ? 2 : 1);
	}

	public void forceEnd() {
		forceStatus.set(0);
	}

	public boolean isTransaction() {
		return Trans.get() != null || Trans.isTransactionNone();
	}

	/*
	 * getMaster和getSlave都不允许抛SQLException,所以封装一下
	 */
	protected Connection getConnectionQuite(DataSource ds) {
		try {
			return Trans.getConnectionAuto(ds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public NutzConnectionSource() {
	}

	public NutzConnectionSource(DataSource master) {
		this.master = master;
	}

	public NutzConnectionSource(DataSource master, DataSource[] slaves) {
		this.master = master;
		this.slaves = slaves;
	}

	public void setMaster(DataSource master) {
		this.master = master;
	}

	public void setSlaves(DataSource[] slaves) {
		this.slaves = slaves;
	}
}
