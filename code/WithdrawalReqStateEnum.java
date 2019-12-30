package com.ry600.nursing.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 员工提现状态枚举
 * @author boc
 */
@Getter
@AllArgsConstructor
public enum WithdrawalReqStateEnum {

	/** 处理中 */
	PROCESSING(0, "处理中", Arrays.asList(1, 2)),

	/** 处理完毕 */
	PROCESS_END(1, "处理完毕", Collections.emptyList()),

	/** 处理异常 */
	PROCESS_EXCEPTION(2, "处理异常", Collections.emptyList()),

	/** 非法状态 */
	ILLEGAL_STATE(-1, "非法状态(要切换到的状态是否存在?)", Collections.emptyList());

	/** 状态值 */
	Integer value;

	/** 状态描述 */
	String descr;

	/** 当前状态能切换到哪些状态 */
	Collection<Integer> nextStates;

	/**
	 * 当前状态是否能够切换成传入的状态
	 * @param state 切换的目标状态
	 * @return true or false
	 */
	public boolean hasState(WithdrawalReqStateEnum state) {
		return Objects.requireNonNull(nextStates).stream().anyMatch(state.value::equals);
	}

	/**
	 * 遍历获取算了，不写工厂了
	 * @param stateValue 要获取的值
	 * @return 与传入值相同的状态枚举
	 */
	public static WithdrawalReqStateEnum getState(int stateValue) {
		return Arrays.stream(WithdrawalReqStateEnum.values()).filter(s -> s.getValue().equals(stateValue)).findAny().orElse(ILLEGAL_STATE);
	}

}
