package com.ry600.nursing.util;

import java.util.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 通用的树结构
 * @author linbc
 */
public class Tree <T> implements Iterable<T> {

	private T root;

	private Function<Integer, Collection<? extends T>> listByParentId;

	private Function<T, Collection<T>> getChildren;

	private BiFunction<T, Integer, T> setParentId;

	private Function<T, Integer> getId;

	private BiFunction<? super T, Boolean, Tree<T>> getTree;

	public Tree(T node
			, Function<Integer, Collection<? extends T>> listByParentId
			, Function<T, Collection<T>> getChildren
			, BiFunction<T, Integer, T> setParentId
			, Function<T, Integer> getId
			, BiFunction<? super T, Boolean, Tree<T>> getTree
			, boolean init) {

		this.root = node;
		this.listByParentId = listByParentId;
		this.getChildren = getChildren;
		this.setParentId = setParentId;
		this.getId = getId;
		this.getTree = getTree;
		/* 因为迭代器也会调用getTree，并且不需要初始化，因此留一个初始化的可选项 */
		if (init) {
			nodeInit(this);
		}
	}

	public void nodeInit(Tree<T> node) {

		/* 获取所有下一级子元素 */
		Collection<? extends T> list = node.listByRootParentId();

		if (list == null || list.isEmpty()) {
			return;
		}

		list.forEach(o -> node.getRootChildren().add(o));

		//然后全部初始化
		node.getRootChildren().forEach(t -> getTree.apply(t, true));
	}

	private class TreeIterator implements Iterator<T> {

		Stack<T> stack = new Stack<>();

		TreeIterator() {
			if (root != null) {
				stack.push(root);
			}
		}

		@Override
		public boolean hasNext() {
			return stack.size() > 0;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T node = stack.pop();
			Tree<T> tree = getTree.apply(node, false);
			tree.getRootChildren().forEach(stack::push);
            /*if (tree.getChildren() != null && !tree.getChildren().isEmpty()) {
                //return next();    //只返回最低层的(没有子元素的)
            }*/
			//返回所有的
			return  node;
		}

	}

	/**
	 * 获取调用者的子节点集合(从数据库获取)，其实就是根据id获取子节点，不过传入了当前对象的id
	 * @return 返回子节点集合
	 */
	public Collection<? extends T> listByRootParentId() {
		return listByParentId.apply(getRootId());
	}

	public Collection<? extends T> listByParentId(Integer parentId) {
		return listByParentId.apply(parentId);
	}

	/**
	 * @return 获取调用者的子节点集合(从对象的属性获取)，其实就是root属性的children集合
	 */
	public Collection<T> getRootChildren() {
		return getChildren(root);
	}

	public Collection<T> getChildren(T t) {
		return getChildren.apply(t);
	}

	/**
	 * 修改父节点的id
	 * @param t 需要设置的节点
	 * @param id 修改后的id
	 * @return t
	 */
	public T setParentId(T t, Integer id) {
		return setParentId.apply(t, id);
	}

	public T setRootParentId(Integer id) {
		return setParentId.apply(root, id);
	}

	/**
	 * @return 获取当前节点ID
	 */
	public Integer getRootId() {
		return getId(root);
	}

	public Integer getId(T t) {
		return getId.apply(t);
	}

	/**
	 * @param t 树的root
	 * @return 返回树，可能需要在当前方法获取树
	 */
	public Tree<T> getTree(T t, boolean init) {
		return getTree.apply(t, init);
	}

	@Override
	public Iterator<T> iterator() {
		return new TreeIterator();
	}

	public Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	public Stream<T> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}

	public T getRoot() {
		return root;
	}

	public void setRoot(T root) {
		this.root = root;
	}

}