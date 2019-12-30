package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry600.nursing.server.aid.service.IItemRedisService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * item表的树+普通增删改查
 * @author boc
 */
public abstract class AbstractCommonTreeController<S extends IService<E>, E extends Serializable>
		implements BasicCommonController<S, E>, BasicTreeController<S, E> {

	@Autowired
	protected S s;

	@Autowired
	protected IItemRedisService itemRedisService;

	@Override
	public S getService() {
		return s;
	}

	@Override
	public IItemRedisService getItemRedisService() {
		return itemRedisService;
	}

}
