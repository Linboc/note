package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry600.nursing.server.aid.service.IItemRedisService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * item表的树
 * @author boc
 */
public abstract class AbstractTreeController <S extends IService<E>, E extends Serializable> implements BasicTreeController<S, E> {

	@Autowired
	protected S s;

	@Autowired
	protected IItemRedisService itemRedisService;

	@Override
	public IItemRedisService getItemRedisService() {
		return itemRedisService;
	}

	@Override
	public S getService() {
		return s;
	}
}
