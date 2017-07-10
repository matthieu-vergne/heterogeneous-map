package fr.vergne.heterogeneousmap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import fr.vergne.heterogeneousmap.HeterogeneousMap.Key;

/**
 * <p>
 * An {@link HeterogeneousMap} is a container which allows to map keys to values
 * with customized types. It is opposed in this sense to the standard
 * {@link Map} which associates keys to values having a common type. Thus, while
 * a standard {@link Map} returns values of a given type, each key of an
 * {@link HeterogeneousMap} stores the type information of the value it is
 * mapped to, which allows to retrieve this value with the corresponding type.
 * Such a container is suited for storing key-value pairs where values are
 * particularly heterogeneous (thus the naming), a situation which would lead a
 * standard {@link Map} to store values as non-informative {@link Object}
 * instances, thus enforcing casting.
 * </p>
 * 
 * <p>
 * For practical purpose, an {@link HeterogeneousMap} is a {@link Map}-like
 * container, in the sense that it reproduces the API of a classical {@link Map}
 * with only the required differences. Despite the high similarity, it remains
 * unable to extend the {@link Map} interface, a limitation which is compensated
 * by using the {@link Map} provided by {@link #toMap()}.
 * </p>
 * 
 * <b>PAY ATTENTION</b>
 * <p>
 * While the parameterized {@link Key} allows to ensure that correct values are
 * mapped at compile-time, the checks which can only be done at run-time are
 * achieved through the information stored in the {@link Key}. If the
 * {@link Key} is instantiated without this information, type safety is reduced.
 * Moreover, instances of parameterized types which cannot be checked at
 * compile-time will not be (fully) checked at run-time because of Java's type
 * erasure.
 * </p>
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
public class HeterogeneousMap implements Iterable<Entry<Key<?>, Object>> {

	/**
	 * A {@link Key} is a unique descriptor identifying a specific item in an
	 * {@link HeterogeneousMap}. It stores the relevant information about the type
	 * of the values it can be mapped to.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <T>
	 */
	public static class Key<T> {
		private final Class<T> valueClass;

		/**
		 * Instantiate a {@link Key} for a specific type of values. Such instance allows
		 * to cover both compile-time and run-time type safety. However, it does not
		 * allow to cover parameterized types.
		 */
		public Key(Class<T> valueClass) {
			this.valueClass = valueClass;
		}

		/**
		 * Instantiate a {@link Key} without storing {@link Class} information. Such
		 * instance covers only compile-time type safety. However, it also manages
		 * parameterized types.
		 */
		public Key() {
			this(null);
		}

		@SuppressWarnings("unchecked")
		private T cast(Object value) {
			if (valueClass == null) {
				return (T) value;
			} else {
				return valueClass.cast(value);
			}
		}

		private boolean canBeMappedTo(Object value) {
			return value == null || valueClass == null || valueClass.isInstance(value);
		}

		@Override
		public String toString() {
			return "Key(" + valueClass + ")";
		}
	}

	private final Map<Key<?>, Object> innerMap;

	private HeterogeneousMap(Supplier<Map<Key<?>, Object>> mapSupplier) {
		this.innerMap = mapSupplier.get();
	}

	public HeterogeneousMap() {
		this(() -> new HashMap<Key<?>, Object>());
	}

	public HeterogeneousMap(Map<? extends Key<?>, ?> map) {
		this();
		putAll(map);
	}

	public HeterogeneousMap(HeterogeneousMap map) {
		this();
		putAll(map);
	}

	/*
	 * If one wants to take a parent class, not the class of the value, he needs to
	 * use create() instead.
	 */
	public <T> Key<T> put(T value) {
		@SuppressWarnings("unchecked")
		Key<T> key = new Key<>((Class<T>) value.getClass());
		put(key, value);
		return key;
	}

	@SuppressWarnings("unchecked")
	public <T> T put(Key<T> key, T value) {
		if (key.canBeMappedTo(value)) {
			return (T) innerMap.put(key, value);
		} else {
			throw new IllegalArgumentException("The key " + key + " rejects the value " + value);
		}
	}

	private <T> T putWithCast(Key<T> key, Object value) {
		try {
			return put(key, key.cast(value));
		} catch (ClassCastException cause) {
			throw new ClassCastException("The key " + key + " cannot be mapped to values of type " + value.getClass());
		}
	}

	public void putAll(Map<? extends Key<?>, ?> map) {
		for (Entry<? extends Key<?>, ?> entry : map.entrySet()) {
			putWithCast(entry.getKey(), entry.getValue());
		}
	}

	public void putAll(HeterogeneousMap map) {
		putAll(map.toMap());
	}

	public <T> T remove(Key<T> key) {
		return key.cast(innerMap.remove(key));
	}

	public void removeAll(Collection<? extends Key<?>> keys) {
		for (Key<?> key : keys) {
			remove(key);
		}
	}

	public void clear() {
		innerMap.clear();
	}

	public <T> T get(Key<T> key) {
		return key.cast(innerMap.get(key));
	}

	public int size() {
		return innerMap.size();
	}

	public boolean isEmpty() {
		return innerMap.isEmpty();
	}

	public boolean containsKey(Key<?> key) {
		return innerMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return innerMap.containsValue(value);
	}

	public Set<Key<?>> keySet() {
		return innerMap.keySet();
	}

	public Collection<Object> values() {
		return innerMap.values();
	}

	public Set<Entry<Key<?>, Object>> entrySet() {
		return innerMap.entrySet();
	}

	@Override
	public Iterator<Entry<Key<?>, Object>> iterator() {
		return innerMap.entrySet().iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof HeterogeneousMap) {
			HeterogeneousMap m = (HeterogeneousMap) obj;
			return m.innerMap.equals(innerMap);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return innerMap.hashCode();
	}

	/**
	 * <p>
	 * This method is a convenient method to use this {@link HeterogeneousMap} like
	 * a standard {@link Map}. The types of the {@link Map} preserves as much
	 * information as possible, and all the methods act as proxies to the original
	 * methods of the {@link HeterogeneousMap}. Because an {@link HeterogeneousMap}
	 * manages only {@link Key} instances as keys, when {@link Object}s which are
	 * not {@link Key}s are provided, default values are returned to remain
	 * compliant with usual {@link Map} implementations:
	 * </p>
	 * <ol>
	 * <li>{@link Map#get(Object)} returns <code>null</code></li>
	 * <li>{@link Map#remove(Object)} returns <code>null</code></li>
	 * <li>{@link Map#containsKey(Object)} returns <code>false</code></li>
	 * </ol>
	 * 
	 * @return a {@link Map} wrapping this {@link HeterogeneousMap}
	 */
	public Map<Key<?>, Object> toMap() {
		return new Map<Key<?>, Object>() {

			@Override
			public int size() {
				return HeterogeneousMap.this.size();
			}

			@Override
			public boolean isEmpty() {
				return HeterogeneousMap.this.isEmpty();
			}

			@Override
			public boolean containsKey(Object key) {
				if (key instanceof Key) {
					return HeterogeneousMap.this.containsKey((Key<?>) key);
				} else {
					return false;
				}
			}

			@Override
			public boolean containsValue(Object value) {
				return HeterogeneousMap.this.containsValue(value);
			}

			@Override
			public Object get(Object key) {
				if (key instanceof Key) {
					return HeterogeneousMap.this.get((Key<?>) key);
				} else {
					return null;
				}
			}

			@Override
			public Object put(Key<?> key, Object value) {
				try {
					return HeterogeneousMap.this.putWithCast(key, value);
				} catch (ClassCastException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public Object remove(Object key) {
				if (key instanceof Key) {
					return HeterogeneousMap.this.remove((Key<?>) key);
				} else {
					return null;
				}
			}

			@Override
			public void putAll(Map<? extends Key<?>, ? extends Object> map) {
				try {
					HeterogeneousMap.this.putAll(map);
				} catch (ClassCastException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void clear() {
				HeterogeneousMap.this.clear();
			}

			@Override
			public Set<Key<?>> keySet() {
				return HeterogeneousMap.this.keySet();
			}

			@Override
			public Collection<Object> values() {
				return HeterogeneousMap.this.values();
			}

			@Override
			public Set<Entry<Key<?>, Object>> entrySet() {
				return HeterogeneousMap.this.entrySet();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this) {
					return true;
				} else if (obj instanceof Map) {
					Map<?, ?> m = (Map<?, ?>) obj;
					return m.equals(innerMap);
				} else {
					return false;
				}
			}

			@Override
			public int hashCode() {
				return HeterogeneousMap.this.hashCode();
			}
		};
	}

	public static Builder build() {
		return new Builder();
	}

	public static class Builder {

		private Map<Key<?>, Object> contentMap = Collections.emptyMap();
		private Map<Key<?>, Object> innerMap = new HashMap<Key<?>, Object>();
		private boolean isInstantiated = false;
		private boolean areMapsChecked = true;

		private Builder() {
			// Private constructor
		}

		/*
		 * Tell it is for performance issues, and that a wrong map will throw an
		 * exception at instantiation time.
		 */
		public Builder withoutMapChecks() {
			checkInstantiation();
			areMapsChecked = false;
			return this;
		}

		public Builder withCustomContent(Map<Key<?>, Object> map) {
			checkInstantiation();
			checkValidMap(map);
			this.contentMap = map;
			return this;
		}

		public Builder withCustomContent(HeterogeneousMap map) {
			return withCustomContent(map.toMap());
		}

		public Builder withCustomInnerMap(Map<Key<?>, Object> map) {
			checkInstantiation();
			checkValidMap(map);
			this.innerMap = map;
			return this;
		}

		public HeterogeneousMap instantiate() {
			HeterogeneousMap map = new HeterogeneousMap(() -> innerMap);
			map.putAll(contentMap);
			return map;
		}

		private void checkInstantiation() {
			if (isInstantiated) {
				throw new IllegalStateException("This builder cannot be used for generating more than one instance.");
			} else {
				// Builder still usable
			}
		}

		private void checkValidMap(Map<Key<?>, Object> map) {
			if (!areMapsChecked) {
				// Ignore this check
			} else {
				try {
					new HeterogeneousMap(map);
				} catch (Exception cause) {
					throw new IllegalArgumentException("The provided map cannot be used", cause);
				}
			}
		}

	}
}
