package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ry600.nursing.common.util.MinioProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * @author guozh @date 2019/4/18
 * @Description
 */
public abstract class AbstractCommonFileController<S extends IService<E>, E extends Serializable>
		implements BasicCommonController<S, E>, BasicFileController<S, E> {

	@Autowired
	protected S s;

	@Autowired
	protected MinioProcessor minioProcessor;

	@Override
	public S getService() {
		return s;
	}

	@Override
	public MinioProcessor getMinioProcessor() {
		return minioProcessor;
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
