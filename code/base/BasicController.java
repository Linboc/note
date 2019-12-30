package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ry600.nursing.util.InfoUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.io.*;
import java.util.Objects;

/**
 * 所有控制器共用的接口
 * @author boc
 */
public interface BasicController <S extends IService<E>, E extends Serializable> {

	/**
	 * 钩子方法，子类可以使用此方法给所有父类查询增加额外的查询条件，
	 * 也可以完全自定义，返回一个新的Wrapper，会以返回的Wrapper为准
	 * @param qw QueryWrapper
	 */
	default QueryWrapper<E> addedWrapper(QueryWrapper<E> qw) {
		return qw;
	}

	/**
	 * 钩子方法，给修改和删除进行验证，比如要删除某个员工，子类重写这个方法，
	 * 必须这个员工的机构id和当前登录用户属于同一机构才返回true，否则返回false，
	 * 防止获取了token进行恶意操作其它人的数据，查询可以用addedWrapper进行限制，因此不需要
	 * @param e 要进行操作的对象
	 * @return 是否通过验证
	 */
	default boolean udValidate(E e, E dbE) {
		return true;
	}

	S getService();
	/**
	 * 从请求域获取参数
	 * @param name 参数key
	 * @return value
	 */
	default String getPara(String name) {
		return Objects.requireNonNull(InfoUtil.getRequest()).getParameter(name);
	}

	/**
	 * 把参数保存进请求域
	 * @param name key
	 * @param value value
	 */
	default void setAttr(String name, Object value) {
		Objects.requireNonNull(InfoUtil.getRequest()).setAttribute(name, value);
	}

	/**
	 * 返回前台文件流
	 *
	 * @author fengshuonan
	 * @date 2017年2月28日 下午2:53:19
	 */
	default ResponseEntity<InputStreamResource> renderFile(String fileName, byte[] fileBytes) {
		return renderFile(fileName, new ByteArrayInputStream(fileBytes));
	}

	/**
	 * 返回前台文件流
	 *
	 * @author fengshuonan
	 * @date 2017年2月28日 下午2:53:19
	 */
	default ResponseEntity<InputStreamResource> renderFile(String fileName, String filePath) {
		try {
			final FileInputStream inputStream = new FileInputStream(filePath);
			return renderFile(fileName, inputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("文件读取错误!");
		}
	}

	/**
	 * 兼容
	 * @author linbc
	 */
	default ResponseEntity<InputStreamResource> renderFile(String fileName, InputStream inputStream) {
		return renderFile(fileName, inputStream, null);
	}

	/**
	 * 返回前台文件流
	 *
	 * @param fileName    文件名
	 * @param inputStream 输入流
	 * @param mediaType   可以传null，默认是文件类型，比如需要传图片的时候就传类型MediaType.IMAGE_JPEG
	 * @return spring mvn能处理的流对象
	 * @author 0x0001
	 */
	default ResponseEntity<InputStreamResource> renderFile(String fileName, InputStream inputStream, MediaType mediaType) {
		InputStreamResource resource = new InputStreamResource(inputStream);
		String dfileName = null;
		try {
			dfileName = new String(fileName.getBytes("gb2312"), "iso8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(Objects.isNull(mediaType) ? MediaType.APPLICATION_OCTET_STREAM : MediaType.IMAGE_JPEG);
		headers.setContentDispositionFormData("attachment", dfileName);
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

	/**
	 * 数据验证标识接口
	 */
	public static interface Insert { }
	public static interface Update { }

}
