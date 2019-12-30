package com.ry600.nursing.controller.common.base;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pigx.common.core.util.R;
import com.ry600.nursing.common.annotation.DataValidate;
import com.ry600.nursing.common.constant.QueryConstants;
import com.ry600.nursing.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用增删改查接口
 * @author boc
 */
public interface BasicCommonController<S extends IService<E>, E extends Serializable> extends BasicController<S, E> {

	/**
	 * 根据id查询数据
	 * @param id id
	 * @return 返回对应id的记录
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取数据", httpMethod = "GET")
	default R<E> getById(@PathVariable("id") Long id) {
		return R.<E>builder().data(getService().getById(id)).build();
	}

	/**
	 * 根据单个条件批量查询
	 * @param page 分页对象
	 * @param queryKey 查询字段
	 * @param queryValue 查询内容
	 * @return 结果集合
	 */
	@GetMapping
	@ApiOperation(value = "获取列表(可根据条件与分页)", httpMethod = "GET")
	default R<IPage<E>> get(Page<E> page
			, @RequestParam(value = QueryConstants.KEY, required = false) String queryKey
			, @RequestParam(value = QueryConstants.VALUE, required = false) String queryValue) {

		Wrapper<E> wrapper = Wrapper.<E>wrapper();

		if (StrUtil.isNotEmpty(queryKey) && StrUtil.isNotEmpty(queryValue)) {
			// 支持3种查询类型：准确、模糊、正则，如果没有指定查询类型默认为模糊
			switch (CommonUtil.getPara(QueryConstants.TYPE) == null ? "" : CommonUtil.getPara(QueryConstants.TYPE)) {
				case QueryConstants.EQ :
					wrapper.eq(queryKey, queryValue);
					break;
				case QueryConstants.REGEX :
					wrapper.regex(queryKey, queryValue);
					break;
				case QueryConstants.LIKE :
				default:
					wrapper.like(queryKey, queryValue);
			}
		}

		return R.<IPage<E>>builder().data(getService().page(page, addedWrapper(wrapper))).msg("success").build();
	}

	/**
	 * 根据id删除记录
	 * @param id id
	 * @return 删除成功或失败
	 */
	@DeleteMapping("/{id}")
	@ApiOperation(value = "根据id删除", httpMethod = "DELETE")
	default R<Boolean> deleteById(@PathVariable("id") Long id) {
		E e = Objects.requireNonNull(getService().getById(id), StrUtil.format("要删除的数据不存在！id：{}", id));
		Assert.isTrue(udValidate(e, e), "逻辑验证不通过!");
		return R.<Boolean>builder().data(getService().removeById(id)).build();
	}

	/**
	 * 添加成功返回true,不成功返回false
	 * @param e 添加的实体对象
	 * @param bindingResult 数据验证
	 * @return 添加成功或失败
	 */
	@PostMapping
	@DataValidate
	@ApiOperation(value = "添加", httpMethod = "POST")
	default R<E> add(@RequestBody @Validated(Insert.class) E e, BindingResult bindingResult) {
		return R.<E>builder().data(getService().save(e) ? e : null).build();
	}

	/**
	 * 更新成功返回更新后的对象，不成功返回null
	 * @param e	更新的实体对象
	 * @param bindingResult 数据验证
	 * @return 更新后的对象
	 */
	@PutMapping
	@DataValidate
	@ApiOperation(value = "修改", httpMethod = "PUT")
	default R<E> update(@RequestBody @Validated(Update.class) E e, BindingResult bindingResult) {
		Assert.isTrue(udValidate(e, Objects.requireNonNull(getService().getById(e), "验证对象为空!")), "逻辑验证不通过!");
		return R.<E>builder().data(getService().updateById(e) ? e : null).build();
	}

}
