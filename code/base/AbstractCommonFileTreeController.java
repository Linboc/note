package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry600.nursing.common.util.MinioProcessor;
import com.ry600.nursing.server.aid.service.IItemRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * @author guozh @date 2019/4/18
 * @Description
 */
public abstract class AbstractCommonFileTreeController <S extends IService<E>, E extends Serializable>
		implements BasicCommonController<S, E>, BasicFileController<S, E>, BasicTreeController<S, E> {

	@Autowired
	protected S s;

	@Autowired
	protected MinioProcessor minioProcessor;

	@Autowired
	protected IItemRedisService itemRedisService;

	@Override
	public S getService() {
		return s;
	}

	@Override
	public MinioProcessor getMinioProcessor() {
		return minioProcessor;
	}

	@Override
	public IItemRedisService getItemRedisService() {
		return itemRedisService;
	}

	@Value("${file.amount-limit}")
	public Integer fileAmountLimit;

	@Override
	public Integer getFileAmountLimit() {
		return fileAmountLimit;
	}

	@Value("${file.size-limit}")
	public Integer fileSizeLimit;

	@Override
	public Integer getFileSizeLimit() {
		return fileSizeLimit;
	}
}
