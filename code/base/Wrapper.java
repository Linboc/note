package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ry600.nursing.common.constant.QueryConstants;

/**
 * mybatis plus的扩展，目前增加了mysql的正则表达式查询
 *
 * @author boc
 */
public class Wrapper<E> extends QueryWrapper<E> {

	private Wrapper() { }

	public static <E> Wrapper<E> wrapper() {
		return new Wrapper<>();
	}

	public Wrapper<E> regex(String column, Object val) {
		return regex(true, column, val);
	}

	public Wrapper<E> regex(boolean condition, String column, Object val) {
		return (Wrapper) doIt(condition, () -> column, () -> QueryConstants.REGEX, () -> formatSql("{0}", val));
	}

}
