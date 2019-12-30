package com.ry600.nursing.controller.common.base;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pigx.common.core.util.R;
import com.ry600.nursing.common.constant.TreeConstants;
import com.ry600.nursing.util.Tree;
import com.ry600.nursing.util.TreeUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 通用的树接口
 * @author boc
 */
public interface BasicUniversalTreeController<S extends IService<E>, E extends Serializable> extends BasicController<S, E> {

	/**
	 * 根据id获取下一级子节点，子类可以通过重写此方法来影响树的数据
	 * 在这里进行addedWrapper更合适，因为会直接影响到树的数据，在后台可能需要使用，
	 * 如果在get里面进行，后台获取的就不是预想中的树了
	 *  给后台用
	//	@GetMapping("/children/{parentId}")
	 * @param parentId 节点id
	 * @return 子节点
	 */
	default Collection<E> listByParentId(@PathVariable("parentId") Integer parentId) {
		return getService().list(addedWrapper(Wrappers.<E>query().eq(getParentIdField(), parentId)));
	}

	/**
	 * 查出id节点下的所有节点
	 * @param treeId 树节点id
	 * @return 树
	 */
	@GetMapping("/tree/{treeId}")
	default R<Tree<E>> getTree(@PathVariable("treeId") Integer treeId) {
		E root = null;
		/* 未分类使用特定的节点 */
		if (TreeConstants.UNSORTED_NODE_ID.equals(treeId)) {
			root = getUnsortedTree(false).getRoot();
		} else if(TreeConstants.ALL_NODE_ID.equals(treeId)) {
			return new R(getAllTree().getData().get(0));
		} else {
			root = Objects.requireNonNull(getService().getById(treeId), "不存在的节点，有可能对树进行了删除，导致对应节点不存在!");
		}
		Tree<E> tree = getTree(root, true);
		//addedWrapper在listByParentId已经做过了，因此这里不需要重复进行
		return new R(tree);
	}

	/**
	 * 查出id节点下的所有节点
	 * @param treeId 树节点id
	 * @return 树的根节点
	 */
	@GetMapping("/treeRoot/{treeId}")
	default R<E> getTreeRoot(@PathVariable("treeId") Integer treeId) {
		return new R(getTree(treeId).getData().getRoot());
	}

	/**
	 * @return 返回所有节点，包括根节点、未分类，这两个都包装在一个虚拟节点下面
	 */
	@GetMapping("/allTree")
	default R<List<Tree<E>>> getAllTree() {
		/* 获取所有根节点，父节点id是0的视为根节点 */
		Collection<Tree<E>> trees = listByParentId(TreeConstants.ALL_NODE_ID).stream().map(e -> getTree(e, true)).collect(Collectors.toList());

		/* 创建一个未分类节点，并插入子节点 */
		Tree<E> unsortedTree =  getUnsortedTree(false);
		/* 获取所有未分类的子节点，父节点id是-1的视为未分类 */
		Collection<E> unSortedChildrenTrees = listByParentId(TreeConstants.UNSORTED_NODE_ID).stream()
				.map(t -> getTree(t, true).getRoot()).collect(Collectors.toList());
		unsortedTree.getRootChildren().addAll(unSortedChildrenTrees);

		/* 把未分类节点加入根节点下，如果不是空的话 */
		if (!unsortedTree.getRootChildren().isEmpty()) {
			trees.add(unsortedTree);
		}

		/* 把所有节点塞入所有节点 */
		Tree<E> allTree = getUnsortedTree(true);
		trees.stream().forEach(t -> allTree.getRootChildren().add(t.getRoot()));

		return new R<>(Arrays.asList(allTree));
	}

	/**
	 * 获取除了指定节点外的所有节点
	 * @param excludeId 要排除的节点
	 * @return 排除后的树
	 */
	@GetMapping("/excludeNode/{treeId}")
	default R<Collection<E>> excludeNode(@PathVariable("treeId") Integer excludeId) {
		Collection<Tree<E>> allTreeRoot = getAllTree().getData();
		TreeUtil.excludeNode(allTreeRoot, excludeId);

		return new R<>(allTreeRoot.stream().map(Tree::getRoot).collect(Collectors.toList()));
	}

	/**
	 * @return 返回所有父节点是根节点的树的root
	 */
	@GetMapping("/allTreeRoot")
	default R<Collection<E>> getAllTreeRoot() {
		Collection<E> roots = getAllTree().getData().stream()
				.flatMap(t -> Stream.of(t.getRoot())).collect(Collectors.toList());
		return new R<>(roots);
	}

	/**
	 * 根据节点id批量删除节点
	 * @param treeId 节点Id
	 * @return 删除成功或失败
	 */
	@DeleteMapping("/tree/{treeId}")
	default R<Boolean> delete(@PathVariable("treeId") Integer treeId) {
		E root = Objects.requireNonNull(getService().getById(treeId), "删除失败，该节点不存在!");
		udValidate(root, root);
		return R.<Boolean>builder().data(getService().removeByIds(TreeUtil.getIds(getTree(root, true)))).build();
	}

	/**
	 * 根据节点id删除单个节点，保留子节点
	 * @param treeId 节点Id
	 * @return 删除成功或失败
	 */
	@DeleteMapping("/node/{treeId}")
	default R<Boolean> deleteCurrent(@PathVariable("treeId") Integer treeId) {
		E root = Objects.requireNonNull(getService().getById(treeId), "删除失败，该节点不存在!");
		udValidate(root, root);

		/* 获取一个树，以调用实体类的方法 */
		Tree<E> tree = getTree(root, false);
		/* 获取删除节点的子节点 */
		Collection<E> childrens = listByParentId(treeId);
		/* 子节点的父节点全部设置为-1(未分类)，并更新 */
		childrens.forEach(t -> tree.setParentId(t, TreeConstants.UNSORTED_NODE_ID));
		getService().updateBatchById(childrens);

		return R.<Boolean>builder().data(getService().removeById(treeId)).build();
	}

	/**
	 * @return 获取父节点id的字段，如果不是默认字段可以重写
	 */
	default String getParentIdField() {
		return TreeConstants.DEFAULT_PARENT_ID_FIELD;
	}

	/**
	 * @param e 树的根节点
	 * @param init 是否对树进行初始化
	 * @return 一个树
	 */
	Tree<E> getTree(E e, boolean init);

	/**
	 * @param flag 是否所有节点
	 * @return isAll为true返回所有节点实体，为false返回未分类实体节点，节点id约定：根节点：0，未分类：-1，所有节点：-2
	 */
	Tree<E>  getUnsortedTree(boolean flag);

}
