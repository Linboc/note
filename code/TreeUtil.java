package com.ry600.nursing.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 给树提供常用方法
 * @author linbc
 */
public class TreeUtil {

	/**
	 * @param tree 树对象
	 * @param <T> 树的泛型实体类
	 * @return 所有节点的id集合
	 */
	public static <T> Collection<Integer> getIds(Tree<T> tree) {
		return tree.stream().map(tree::getId).collect(Collectors.toList());
	}

	/**
	 * @param tree 树对象
	 * @param <T> 树的泛型实体类
	 * @return 所有节点集合
	 */
	public static <T> Collection<T> getList(Tree<T> tree) {
		return tree.stream().collect(Collectors.toList());
	}

	/**
	 * @param tree 树对象
	 * @param node 节点对象
	 * @param <T> 树的泛型实体类
	 * @return 是否有节点id等于指定id的节点
	 */
	public static <T> Boolean existNode(Tree<T> tree, T node) {
		return tree.stream().anyMatch(t -> tree.getId(t).equals(tree.getId(node)));
	}

	/**
	 * @param tree 树对象
	 * @param node 指定的节点
	 * @param <T> 树的泛型类
	 * @return 返回传入的树里面指定的节点，会根据节点Id来判断是否相等
	 */
	public static <T> Tree<T> getNode(Tree<T> tree, T node){
		/* 切换根节点到指定节点，如果不存在则返回root为null的tree */
		tree.setRoot(tree.stream().filter(t -> Objects.equals(tree.getId(t), tree.getId(node))).findFirst().orElse(null));
		return tree;
	}

	/**
	 * @param tree 树对象
	 * @param id 节点id
	 * @param <T> 树的泛型实体类
	 * @return 是否有节点id等于指定id的节点
	 */
	public static <T> Boolean existTreeId(Tree<T> tree, Integer id) {
		return tree.stream().anyMatch(t -> tree.getId(t).equals(id));
	}

	/**
	 * 把传入树的集合的指定节点排除
	 * @param trees 树集合
	 * @param id 排除节点
	 * @param <T> 树的泛型实体类
	 */
	public static <T> void excludeNode(Collection<Tree<T>> trees, Integer id) {
		Iterator<Tree<T>> iterator = trees.iterator();

		while (iterator.hasNext()) {
			Tree<T> tree = iterator.next();
			if (id.equals(tree.getRootId())) {
				iterator.remove();
				return;
			} else {
				TreeUtil.excludeNode(tree, id);
			}
		}
	}

	/**
	 * 把传入树的指定节点排除
	 * @param tree 树对象
	 * @param id 要删除的节点id
	 * @param <T> 树的泛型实体类
	 */
	public static <T> void excludeNode(Tree<T> tree, Integer id) {
		Iterator<T> iterator = tree.getRootChildren().iterator();

		while (iterator.hasNext()) {
			T node = iterator.next();
			if (id.equals(tree.getId(node))) {
				iterator.remove();
				return;
			} else {
				excludeNode(tree.getTree(node, false), id);
			}
		}

	}

}
