package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * 通用树+普通增删改查
 * @author boc
 */
public abstract class AbstractCommonUniversalTreeController<S extends IService<E>, E extends Serializable>
		implements BasicUniversalTreeController<S, E>, BasicCommonController<S, E> {

	@Autowired
	protected S s;

	@Override
	public S getService() {
		return s;
	}
}
