package com.ry600.nursing.common.handler;


import com.pig4cloud.pigx.common.core.constant.CommonConstants;
import com.pig4cloud.pigx.common.core.util.R;
import com.ry600.nursing.common.exception.RequestBodyValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author huanggx
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandlerResolver {

	/**
	 * 全局异常.
	 *
	 * @param e the e
	 * @return R
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R handleGlobalException(Exception e) {
		log.error("ry:全局异常信息 ex={}", e.getMessage(), e);
		return R.builder()
				.msg(e.getLocalizedMessage())
				.code(CommonConstants.FAIL)
				.build();
	}

	/**
	 * AccessDeniedException
	 *
	 * @param e the e
	 * @return R
	 */
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public R handleAccessDeniedException(AccessDeniedException e) {
		String msg = SpringSecurityMessageSource.getAccessor()
				.getMessage("AbstractAccessDecisionManager.accessDenied"
						, e.getMessage());
		log.error("ry:拒绝授权异常信息 ex={}", msg, e);
		return R.builder()
				.msg(msg)
				.code(CommonConstants.FAIL)
				.build();
	}

	/**
	 * validation Exception
	 *
	 * @param exception
	 * @return R
	 */
	@ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleBodyValidException(MethodArgumentNotValidException exception) {
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		log.error("ry:参数绑定异常,ex = {}", fieldErrors.get(0).getDefaultMessage());
		return R.builder()
				.msg(fieldErrors.get(0).getDefaultMessage())
				.code(CommonConstants.FAIL)
				.build();
	}

	/**
	 * RequestBody Valid Exception 处理请求体验证异常
	 *
	 * @param exception
	 * @return R
	 */
	@ExceptionHandler({RequestBodyValidException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R handleRequestBodyValidException(RequestBodyValidException exception) {
		StringBuffer sb = new StringBuffer();
		sb.append(exception.getMessage()+"：");
		exception.getErrors().stream().forEach(e->sb.append(e.getDefaultMessage()+";"));
		log.debug(exception.getErrors().toString());
		return R.builder()
				.msg(sb.toString())
				.data(exception.getErrors())
				.code(CommonConstants.FAIL)
				.build();
	}
}
