package com.ry600.nursing.controller.common.base;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pigx.common.core.util.R;
import com.ry600.nursing.server.aid.entity.ItemTree;
import com.ry600.nursing.server.aid.service.IItemRedisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * item表的树接口
 * @author boc
 */
public interface BasicTreeController<S extends IService<E>, E extends Serializable> extends BasicController<S, E> {

	/**
	 * 查出id节点下的所有符合条件的数据
	 * @param page 分页对象
	 * @param queryKey 查询字段
	 * @param queryValue 查询内容
	 * @param itemId 树节点id
	 * @return 结果集合
	 * @throws Exception 找不到给定id对应的节点
	 */
	@GetMapping("/tree")
	@ApiOperation(value = "获取列表，根据分类", httpMethod = "GET")
	@ApiImplicitParam(name = "treeType", value = "分类ID，itemId", dataTypeClass = Integer.class)
	default R<IPage<E>> get(Page page
			, @RequestParam(value = "queryKey", required = false) String queryKey
			, @RequestParam(value = "queryValue", required = false) String queryValue
			, @RequestParam(value = "treeType", required = false) Integer itemId) throws Exception {

		QueryWrapper qw = Wrappers.<E>query();
		if (StrUtil.isNotEmpty(queryKey) && StrUtil.isNotEmpty(queryValue)) {
			qw.like(queryKey, queryValue);
		}
		if (!Objects.isNull(itemId) && StrUtil.isNotEmpty(getItemCode())) {
			qw.in(getItemCode(), getItemRedisService().getItemTreeByItemId(getItemCode(), itemId).listAllItemIds());
		}

		return R.<IPage<E>>builder().data(getService().page(page, addedWrapper(qw))).build();
	}

	/**
	 * 查询整棵树，如果没有数据则试图刷新redis，并返回刷新后的数据
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/allTree")
	default R<List<ItemTree>> getTree() throws Exception {
		if (StrUtil.isNotEmpty(getItemCode())) {
			List<ItemTree> treeList= getItemRedisService().getItemTreesByClassCode(getItemCode());
			return R.<List<ItemTree>>builder().data(treeList == null || treeList.isEmpty() ? resetTree() : treeList).build();
		}
		throw new Exception("找不到ItemCode!");
	}

	/**
	 * 刷新对应itemCode的树
	 * @return 刷新后的树集合
	 * @throws Exception
	 */
	default List<ItemTree> resetTree() throws Exception {
		return getItemRedisService().resetItemTreesByClassCode(getItemCode());
	}

	IItemRedisService getItemRedisService();

	abstract String getItemCode();

}
