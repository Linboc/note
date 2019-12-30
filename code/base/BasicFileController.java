package com.ry600.nursing.controller.common.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pigx.common.core.util.R;
import com.ry600.nursing.common.annotation.DataValidate;
import com.ry600.nursing.common.util.MinioProcessor;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author guozh
 * @date 2019/4/18
 * <p>
 * 基础文件接口
 */
public interface BasicFileController<S extends IService<E>, E extends Serializable> extends BasicController<S, E> {

	/**
	 * 默认有效期为一天
	 */
	String DEFAULT_EXPIRY_TIME = "86400";

	/**
	 * Minio处理器
	 * @return MinioProcessor
	 */
	MinioProcessor getMinioProcessor();
	// TODO

	/**
	 * 文件大小限制
	 * @return Integer
	 */
	Integer getFileSizeLimit();

	/**
	 * 一个字段内的文件数量限制
	 * @return Integer
	 */
	Integer getFileAmountLimit();

	/**
	 * 根据id查询单条数据
	 * 返回数据中的文件URL字段被替换成可以写进 img 标签的链接地址
	 * @param id      id
	 * @param expires 有效期，单位为秒，默认为一天
	 * @return 返回对应id的记录
	 * @throws NoSuchFieldException   e
	 * @throws IllegalAccessException e
	 */
	@GetMapping("/link/{id}")
	default R<E> getByIdWithFileLink(@PathVariable("id") Integer id,
									 @RequestParam(required = false, defaultValue = DEFAULT_EXPIRY_TIME) Integer expires) throws NoSuchFieldException, IllegalAccessException {
		E e = getService().getById(id);
		Assert.notNull(e,"没有查询到相应数据");
		E newE = transUrlToLink(e, defineUrlField(e, new FileFieldDefine()), expires);
		return R.<E>builder().data(newE).build();
	}

	/**
	 * 获取列表，字段中的文件URL转换为外链link，可过滤，可分页
	 * @param page       分页
	 * @param expires    过期时间，单位为秒，默认为一天
	 * @param queryKey   自定义查询的字段名，下划线格式
	 * @param queryValue 自定义查询的值
	 * @return R<IPage < E>>
	 * @throws NoSuchFieldException   e
	 * @throws IllegalAccessException e
	 */
	@GetMapping("/link")
	@ApiOperation(value = "获取列表，字段中的文件URL转换为外链link，可过滤，可分页", httpMethod = "GET")
	default R<IPage<E>> getWithFileLink(Page page
			, @RequestParam(required = false, defaultValue = DEFAULT_EXPIRY_TIME) Integer expires
			, @RequestParam(value = "queryKey", required = false) String queryKey
			, @RequestParam(value = "queryValue", required = false) String queryValue) throws NoSuchFieldException, IllegalAccessException {

		QueryWrapper<E> qw = Wrappers.query();
		if (StrUtil.isNotEmpty(queryKey) && StrUtil.isNotEmpty(queryValue)) {
			qw.like(queryKey, queryValue);
		}

		List<E> newList = new ArrayList<>();
		IPage<E> iPage = getService().page(page, addedWrapper(qw));
		List<E> list = iPage.getRecords();
		for (E e : list) {
			newList.add(transUrlToLink(e, defineUrlField(e, new FileFieldDefine()), expires));
		}

		return R.<IPage<E>>builder().data(iPage.setRecords(newList)).build();
	}

	/**
	 * 更新
	 * 带有文件URL字段的更新需要用这个接口
	 * 更新成功返回更新后的对象，不成功返回null
	 * @param e             更新的实体对象
	 * @param bindingResult 数据验证
	 * @return 更新后的对象
	 * @throws Exception e
	 */
	@PutMapping("/file")
	@DataValidate
	@ApiOperation(value = "修改，包含文件URL字段", httpMethod = "PUT")
	default R<E> updateWithFileField(@RequestBody @Validated(BasicController.Update.class) E e, BindingResult bindingResult) throws Exception {
		Assert.isTrue(udValidate(e, Objects.requireNonNull(getService().getById(e), "验证对象为空!")), "逻辑验证不通过!");
		updateFile(e, defineUrlField(e, new FileFieldDefine()));
		return R.<E>builder().data(getService().updateById(e) ? e : null).build();
	}

	/**
	 * 定义文件存储必须的信息
	 * @param e      实体
	 * @param define define
	 * @return FileFieldDefine
	 */
	FileFieldDefine defineUrlField(E e, FileFieldDefine define);

	@Data
	class FileFieldDefine {
		/**
		 * 处理前台会进行CRUD的图片字段，驼峰命名（如果是后台压缩生成，不用前台CRUD，则不需要在这里定义）
		 */
		public String[] fields;
		/**
		 * 查询的时候需要将URL转为Link的字段，驼峰命名
		 */
		public String[] transLinkFields;
		/**
		 * 实体类的资源标识，用于Minio文件系统中分包存储，可以使用表名，驼峰命名
		 * 可使用定义在实体类中的final字段：public static final String RES_TYPE = "goods"
		 */
		public String resType;
		/**
		 * 从前台接收的实体对象的ID
		 */
		public Integer id;
	}

	/**
	 * 新增
	 * 带有文件URL字段的新增需要用这个接口
	 * @param e             添加的实体对象/
	 * @param bindingResult 数据验证
	 * @return 添加成功或失败
	 * @throws Exception e
	 */
	@PostMapping(value = "/file")
	@DataValidate
	@ApiOperation(value = "添加", httpMethod = "POST")
	default R<E> addWithFileField(@RequestBody @Validated(BasicController.Insert.class) E e, BindingResult bindingResult) throws Exception {
		/**
		 * 如果先新增，file字段是外链格式，长度超过数据库限制
		 * 如果是先转换link为URL、转换时要完成存储，但是没有resId，无法存储
		 * 解决方法：1 转存link字段。2 新增。3 转存回来link字段。4 link转换为URL，存储文件。5 更新数据库，存储的是URL
		 */

		// 1 将参数中的文件URL字段转存到Map中
		FileFieldDefine define = defineUrlField(e, new FileFieldDefine());
		String[] fields = define.getFields();
		Map<Field, String> fieldParam = new HashMap<>(4);
		Class<? extends Serializable> eClass = e.getClass();

		for (String field : fields) {
			Field classField = eClass.getDeclaredField(field);
			classField.setAccessible(true);
			String param = (String) classField.get(e);
			fieldParam.put(classField, param);
			classField.set(e, null);
		}

		// 2 新增
		Assert.isTrue(getService().save(e), "新增失败！");

		// 3 新增完毕后，将Map中的文件URL字段转存回来实体类中
		for (Map.Entry<Field, String> entry : fieldParam.entrySet()) {
			entry.getKey().set(e, entry.getValue());
		}

		// 新增完毕后，e 中的 ID 已经回写完成，所以需要重新执行子类的实现方法，填入ID
		define = defineUrlField(e, define);

		// 4 link转换为URL，存储文件
		updateFile(e, define);

		// 5 更新数据库，存储的是URL
		Assert.isTrue(getService().updateById(e), "新增成功，但是保存文件失败，请重新维护图片/文件！");
		return R.<E>builder().data(e).build();
	}

	/**
	 * 将实体类中图片字段的URL/Link路径转换为可以写进 img 标签的链接地址
	 * @param e       实体
	 * @param define  定义文件字段的内部类
	 * @param expires 过期时间，单位为秒，默认为一天
	 * @return E
	 * @throws NoSuchFieldException   e
	 * @throws IllegalAccessException e
	 */
	default E transUrlToLink(E e, FileFieldDefine define, Integer expires) throws NoSuchFieldException, IllegalAccessException {
		if (ArrayUtils.isEmpty(define.getFields())) {
			return e;
		}

		// 遍历所有图片字段
		for (String fieldName : define.getTransLinkFields()) {
			Field field = e.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			// 获取参数中的文件URL字段
			String urls = (String) field.get(e);

			if (!StringUtils.isEmpty(urls)) {
				String[] urlsArr = urls.split(";");
				// 遍历前台参数中的每个图片
				for (String url : urlsArr) {
					String link = getMinioProcessor().getObjectURL(url, expires);
					urls = urls.replace(url, link);
				}
			}

			field.set(e, urls);
		}

		return e;
	}

	/**
	 * 更新文件字段，每个文件字段可以存储多个文件URL，只能在Update的时候修改文件字段，不能在Insert的时候
	 * @param e      实体
	 * @param define 定义文件字段的内部类
	 * @return E e
	 * @throws Exception e
	 */
	@Transactional(rollbackFor = Exception.class)
	default E updateFile(E e, FileFieldDefine define) throws Exception {

		// 如果子类没定义文件字段，直接返回


		if (ArrayUtils.isEmpty(define.getFields())) {
			return e;
		}

		E dbEntity = getService().getById(define.getId());

		// 所有不需要上传的字段（只需要读取，照片是后台生成的），在这里将前台传的参数替换成null
		for (String fieldName : define.getTransLinkFields()) {
			if (ArrayUtils.contains(define.getFields(), fieldName)) {
				continue;
			}
			Field field = e.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			// 获取参数中的文件URL字段
			field.set(e, null);
		}

		// 遍历所有需要上传的图片字段（如果不需要，后台生成的那些不用，那些需要在实体类中禁止setter）
		for (String fieldName : define.getFields()) {

			Field field = e.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);

			// 1. 获取参数中的文件字段
			String params = (String) field.get(e);
			// ★ 规定 null 则为前台没传参数，"" 则为删除所有照片
			if (params == null) {
				continue;
			}

			// 2. 获取数据库中的文件字段（如果是新增，dbEntity 会为 null，则 dbUrls 也为 null，后面用到 dbUrls 的地方都需要判空）
			String dbUrls = (dbEntity == null) ? null : (String) field.get(dbEntity);
			// 如果参数和数据库中字段完全一致，直接跳过，无需进行后面的各种判断（效果一样，提升性能）
			if (params.equals(dbUrls)) {
				continue;
			}

			// 处理参数字段（支持 link 格式和 url 格式），如果 params 为 ""，则是前台要删除所有照片，这里不用处理，后面删除时处理就行
			String urls = "";
			if (!StringUtils.isEmpty(params)) {

				String[] paramsArr = params.split(";");
				// 文件的数量不能超过限制
				Assert.isTrue(paramsArr.length <= getFileAmountLimit(), StrUtil.format("文件数量超过限制！字段：{}", fieldName));

				// 3. 把参数里文件字段的外链转为URL
				StringBuilder sb = new StringBuilder();
				for (String param : paramsArr) {
					// 前台传URL格式
					String regexUrl = "^(/[\\d\\w]+){3,4}\\.[\\d\\w]+$";
					String url = "";
					if (param.matches(regexUrl)) {
						url = param;
					// 前台传link格式
					} else if (param.startsWith("http")) {
						url = URLUtil.getPath(param);
						Assert.isTrue(url.matches(regexUrl), "文件字段参数格式错误，多个文件用';'分割，每个文件格式为【/bucketName/resType[/resId]/fileName.ext】或者外链【http...】");
					} else {
						throw new Exception("文件字段参数格式错误，多个文件用';'分割，每个文件格式为【/bucketName/resType[/resId]/fileName.ext】或者外链【http...】");
					}

					sb.append(url + ";");
				}
				urls = sb.deleteCharAt(sb.length() - 1).toString();
			}

			// 4. 遍历前台参数中的每个图片
//			if (!StringUtils.isEmpty(urls)) {
				for (String url : urls.split(";")) {
					if (!StringUtils.isEmpty(url)) {
						// 4.1. 数据库有该URL，已经存在，不用改动，数据库中URL直接替换""
						// 只有在新增的时候传入了URL字段，才会导致数据库中的URL以/temp开头，这种情况下仍然要复制
						if (dbUrls != null && dbUrls.contains(url) && !url.startsWith("/temp")) {
							dbUrls = dbUrls.replace(url, "");

							// 4.2. 数据库没有该URL，是新增的文件，从暂存区中复制到永久区
						} else {
							String newUrl = getMinioProcessor().transferObject(url, define.getResType(), define.getId());
							urls = urls.replace(url, newUrl);
						}
					}
				}
//			}

			// 4.3. 遍历数据中剩余的URL，删除文件
			if (!StringUtils.isEmpty(dbUrls)) {
				for (String dbUrl : dbUrls.split(";")) {
					// 只有在新增的时候传入了URL字段，才会导致数据库中的URL以/temp开头，这时候不用不用删除暂存区中的文件
					if (!StringUtils.isEmpty(dbUrl) && !dbUrl.startsWith("/temp")) {
						getMinioProcessor().removeObject(dbUrl);
					}
				}
			}

			// 前台参数转存后的地址回写到实体类中
			field.set(e, urls);
		}

		return e;
	}
}
