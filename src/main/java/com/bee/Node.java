package com.bee;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unchecked")
public class Node {

	static Map<?, ?> inverseKeysAndValues(Map<?, ?> m) {
		Map<Object, Object> r = new HashMap<Object, Object>();
		
		for (Map.Entry<?, ?> e : m.entrySet()) {
			r.put(e.getValue(), e.getKey());
		}
		
		return r;
	}
	static int randomInt(Number low, Number up) {
		int l = low.intValue();
		int u = up.intValue();

		// tries to fix the issue of generating patterns matched by * and +
		if (u > Bee.STAR_AND_PLUS_GENERATION_LIMIT && (Bee.STAR_AND_PLUS_GENERATION_LIMIT - l) > 0) {
			u = Bee.STAR_AND_PLUS_GENERATION_LIMIT;
		}
		if (u == l) {
			return l;
		}
		return RNG.nextInt(u - l + 1) + l;
	}

	protected static final Map<String, Class<? extends Node>> NODE_MAPPING = new HashMap<String, Class<? extends Node>>();
	protected static final Map<Class<? extends Node>, String> NODE_REVERSE_MAPPING;
	protected static final Random RNG = new Random(System.nanoTime()*1201);

	static {
		NODE_MAPPING.put("java.util.regex.Pattern$Node",			Node.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Begin",			Begin.class);
		NODE_MAPPING.put("java.util.regex.Pattern$BitClass",		BitClass.class);
		NODE_MAPPING.put("java.util.regex.Pattern$BnM",				BnM.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Branch",			Branch.class);
		NODE_MAPPING.put("java.util.regex.Pattern$BranchConn",		BranchConn.class);
		NODE_MAPPING.put("java.util.regex.Pattern$CharProperty$1",	CharProperty_1.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Curly",			Curly.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Ctype",			Ctype.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Dollar",			Dollar.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Dot",				Dot.class);
		NODE_MAPPING.put("java.util.regex.Pattern$GroupCurly",		GroupCurly.class);
		NODE_MAPPING.put("java.util.regex.Pattern$GroupHead",		GroupHead.class);
		NODE_MAPPING.put("java.util.regex.Pattern$GroupTail",		GroupTail.class);
		NODE_MAPPING.put("java.util.regex.Pattern$LastNode",		LastNode.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Loop",			Loop.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Prolog",			Prolog.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Ques",			Ques.class);
		NODE_MAPPING.put("java.util.regex.Pattern$2",				Range.class);
		NODE_MAPPING.put("java.util.regex.Pattern$6",				Range.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Single",			Single.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Slice",			Slice.class);
		NODE_MAPPING.put("java.util.regex.Pattern$Start",			Start.class);

		NODE_REVERSE_MAPPING = (Map<Class<? extends Node>, String>) inverseKeysAndValues(NODE_MAPPING);
	}


	// an instance of "java.util.regex.Pattern$Node" or one of its subclasses
	protected Object originalNode;

	Node(Object originalNode) {
		this.originalNode = originalNode;
	}


	protected static Field getFieldFromClass(String fieldName, Class<?> clazz) throws ClassNotFoundException, SecurityException, NoSuchFieldException {
		try {
			Field f = clazz.getDeclaredField(fieldName);
			f.setAccessible(true);
			return f;
		} catch (NoSuchFieldException e) {
			if (clazz.equals(Object.class)) {
				throw e;
			}
			return getFieldFromClass(fieldName, clazz.getSuperclass());
		}
	}


	private static final Map<Object, Object> NODE_CACHE = new HashMap<Object, Object>();

	protected void flushCache() {
		NODE_CACHE.clear();
	}
	protected void removeFromCache() {
		NODE_CACHE.remove(this.originalNode);
	}

	protected static <T extends Node> T getNode(Class<T> nodeClass, Object originalNode) throws Exception {
		try {
			T n = (T) NODE_CACHE.get(originalNode);
			if (n == null) {
				n = nodeClass.getDeclaredConstructor(Object.class).newInstance(originalNode);
				NODE_CACHE.put(originalNode, n);
			}
			return n;
		} catch (Exception e) {
			// this happens probably because there's an unmapped class in the regular expression tree
			// time to get your hands dirty ;)
			System.err.println("nodeClass    => " + nodeClass);
			System.err.println("originalNode => " + originalNode.getClass().getName());
			throw e;
		}
	}


	protected Node next() {
		try {
			Class<?> clazz = Class.forName("java.util.regex.Pattern$Node");
			Field nextField = getFieldFromClass("next", clazz);
			Object nextNode = nextField.get(this.originalNode);

			if (nextNode != null) {
				return getNode(NODE_MAPPING.get(nextNode.getClass().getName()), nextNode);
			} else {
				return null;
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	String generate() throws Exception {
		Node next = this.next();
		return (next == null ? "" : next.generate());
	}
}
