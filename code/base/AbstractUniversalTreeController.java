package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * 自定义树
 * @author boc
 */
public abstract class AbstractUniversalTreeController <S extends IService<E>, E extends Serializable>
		implements BasicUniversalTreeController<S, E> {

	@Autowired
	protected S s;

	@Override
	public S getService() {
		return s;
	}
}
