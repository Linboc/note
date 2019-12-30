package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * 普通增删改查控制器
 * @author boc
 */
public abstract class AbstractCommonController <S extends IService<E>, E extends Serializable> implements BasicCommonController<S, E> {

	@Autowired
	protected S s;

	@Override
	public S getService() {
		return s;
	}
}
