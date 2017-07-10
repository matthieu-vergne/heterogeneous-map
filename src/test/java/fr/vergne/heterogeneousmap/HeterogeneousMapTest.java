package fr.vergne.heterogeneousmap;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import fr.vergne.heterogeneousmap.HeterogeneousMap;
import fr.vergne.heterogeneousmap.HeterogeneousMap.Key;

public class HeterogeneousMapTest {

	@Test
	public void testPutValueGeneratesKeyAbleToRetrieveValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = map.put(value1);
		assertEquals(value1, map.get(key1));

		Object value2 = new Object();
		Key<Object> key2 = map.put(value2);
		assertEquals(value1, map.get(key1));
		assertEquals(value2, map.get(key2));

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = map.put(value3);
		assertEquals(value1, map.get(key1));
		assertEquals(value2, map.get(key2));
		assertEquals(value3, map.get(key3));
	}

	@Test
	public void testPutKeyValueMakesKeyAbleToRetrieveValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = new Key<String>(String.class);
		map.put(key1, value1);
		assertEquals(value1, map.get(key1));

		Object value2 = new Object();
		Key<Object> key2 = new Key<Object>(Object.class);
		map.put(key2, value2);
		assertEquals(value1, map.get(key1));
		assertEquals(value2, map.get(key2));

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = new Key<Integer>(Integer.class);
		map.put(key3, value3);
		assertEquals(value1, map.get(key1));
		assertEquals(value2, map.get(key2));
		assertEquals(value3, map.get(key3));
	}

	@Test
	public void testPutKeyValueReplacesPreviousValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key = map.put(value1);
		assertEquals(value1, map.get(key));

		String value2 = "abcd";
		map.put(key, value2);
		assertEquals(value2, map.get(key));
	}

	@Test
	public void testPutKeyValueReturnsPreviousValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key = map.put(value1);
		assertEquals(value1, map.get(key));

		String value2 = "abcd";
		String returned = map.put(key, value2);
		assertEquals(value1, returned);
	}

	@Test
	public void testPutKeyHigherInHierarchyStillAcceptValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<Object> key = new Key<Object>(Object.class);
		map.put(key, "test");
	}

	@Test
	public void testPutUncheckedKeyStillAcceptValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key = new Key<String>();
		map.put(key, "test");
	}

	@Test
	public void testPutStandardMapReturnsCorrectValues() {
		Map<Key<?>, Object> externalMap = new HashMap<Key<?>, Object>();

		String value1 = "test";
		Key<String> key1 = new Key<String>(String.class);
		externalMap.put(key1, value1);

		Object value2 = new Object();
		Key<Object> key2 = new Key<Object>(Object.class);
		externalMap.put(key2, value2);

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = new Key<Integer>(Integer.class);
		externalMap.put(key3, value3);

		HeterogeneousMap map = new HeterogeneousMap();
		map.putAll(externalMap);
		assertEquals(value1, map.get(key1));
		assertEquals(value2, map.get(key2));
		assertEquals(value3, map.get(key3));
	}

	@Test
	public void testPutHeterogeneousMapReturnsCorrectValues() {
		HeterogeneousMap externalMap = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = new Key<String>(String.class);
		externalMap.put(key1, value1);

		Object value2 = new Object();
		Key<Object> key2 = new Key<Object>(Object.class);
		externalMap.put(key2, value2);

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = new Key<Integer>(Integer.class);
		externalMap.put(key3, value3);

		HeterogeneousMap map = new HeterogeneousMap();
		map.putAll(externalMap);
		assertEquals(value1, map.get(key1));
		assertEquals(value2, map.get(key2));
		assertEquals(value3, map.get(key3));
	}

	@Test
	public void testUnknownKeyRetrievesNull() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key = new Key<String>(String.class);
		assertEquals(null, map.get(key));
	}

	@Test
	public void testRemoveMakesKeyUnableToRetrieveValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value = "test";
		Key<String> key = map.put(value);
		assertEquals(value, map.get(key));
		map.remove(key);
		assertEquals(null, map.get(key));
	}

	@Test
	public void testRemoveAllMakesKeysUnableToRetrieveValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = map.put(value1);

		Object value2 = new Object();
		Key<Object> key2 = map.put(value2);

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = map.put(value3);

		map.removeAll(Arrays.asList(key1, key3));
		assertEquals(null, map.get(key1));
		assertEquals(value2, map.get(key2));
		assertEquals(null, map.get(key3));
	}

	@Test
	public void testClearMakesAnyKeyUnableToRetrieveValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key1 = map.put("test");
		Key<Object> key2 = map.put(new Object());
		Key<Integer> key3 = map.put(Integer.MAX_VALUE);

		map.clear();
		assertEquals(null, map.get(key1));
		assertEquals(null, map.get(key2));
		assertEquals(null, map.get(key3));
	}

	@Test
	public void testContainsKeyDoesNotRetrieveUnknownKey() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key = new Key<String>(String.class);
		assertFalse(map.containsKey(key));
	}

	@Test
	public void testContainsKeyRetrievesPutKey() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key = map.put("test");
		assertTrue(map.containsKey(key));
	}

	@Test
	public void testContainsKeyDoesNotRetrieveRemovedPutKey() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key = map.put("test");
		map.remove(key);
		assertFalse(map.containsKey(key));
	}

	@Test
	public void testContainsValueDoesNotRetrieveUnknownValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value = "test";
		assertFalse(map.containsValue(value));
	}

	@Test
	public void testContainsValueRetrievesPutValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value = "test";
		map.put(value);
		assertTrue(map.containsValue(value));
	}

	@Test
	public void testContainsValueDoesNotRetrieveRemovedPutValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value = "test";
		Key<String> key = map.put(value);
		map.remove(key);
		assertFalse(map.containsValue(value));
	}

	@Test
	public void testSizeIncreasesWithNewKeys() {
		HeterogeneousMap map = new HeterogeneousMap();

		assertEquals(0, map.size());
		map.put("test");
		assertEquals(1, map.size());
		map.put("test");
		assertEquals(2, map.size());
		map.put(new Object());
		assertEquals(3, map.size());
	}

	@Test
	public void testSizeDecreasesWithRemovedKeys() {
		HeterogeneousMap map = new HeterogeneousMap();
		Key<String> key1 = map.put("test");
		Key<String> key2 = map.put("test");
		Key<Object> key3 = map.put(new Object());

		assertEquals(3, map.size());
		map.remove(key2);
		assertEquals(2, map.size());
		map.remove(key1);
		assertEquals(1, map.size());
		map.remove(key3);
		assertEquals(0, map.size());
	}

	@Test
	public void testSizeIsStableWithReplacement() {
		HeterogeneousMap map = new HeterogeneousMap();
		Key<String> key1 = map.put("test");
		Key<String> key2 = map.put("test");
		Key<Object> key3 = map.put(new Object());
		int size = map.size();

		map.put(key1, "test 2");
		assertEquals(size, map.size());
		map.put(key2, "test again");
		assertEquals(size, map.size());
		map.put(key3, new LinkedList<Object>());
		assertEquals(size, map.size());
	}

	@Test
	public void testSizeIsStableWithRemovingUnknownKeys() {
		HeterogeneousMap map = new HeterogeneousMap();
		map.put("test");
		map.put("test");
		map.put(new Object());
		int size = map.size();

		map.remove(new Key<>(String.class));
		assertEquals(size, map.size());
		map.remove(new Key<>(Object.class));
		assertEquals(size, map.size());
	}

	@Test
	public void testMapIsEmptyUponInstantiation() {
		HeterogeneousMap map = new HeterogeneousMap();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testMapIsNotEmptyWhenPutSomeValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key1 = map.put("test");
		Key<String> key2 = map.put("test");
		Key<Object> key3 = map.put(new Object());

		map.remove(key1);
		map.remove(key2);
		map.remove(key3);

		assertTrue(map.isEmpty());
	}

	@Test
	public void testMapIsEmptyWhenRemoveAllValues() {
		HeterogeneousMap map = new HeterogeneousMap();
		map.put("test");
		assertFalse(map.isEmpty());
	}

	@Test
	public void testKeySetContainsAllKnownKeys() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key1 = map.put("test");
		Key<String> key2 = map.put("test");
		Key<Object> key3 = map.put(new Object());

		Set<Key<?>> keySet = map.keySet();
		assertTrue(keySet.contains(key1));
		assertTrue(keySet.contains(key2));
		assertTrue(keySet.contains(key3));
	}

	@Test
	public void testKeySetContainsNoUnknownKey() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put("test");
		map.put(new Object());

		Set<Key<?>> keySet = map.keySet();
		assertFalse(keySet.contains(new Key<>(String.class)));
		assertFalse(keySet.contains(new Key<>(Object.class)));
	}

	@Test
	public void testValuesContainsAllKnownValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value1 = "test";
		map.put(value1);
		String value2 = "test";
		map.put(value2);
		Object value3 = new Object();
		map.put(value3);

		Collection<Object> values = map.values();
		assertTrue(values.contains(value1));
		assertTrue(values.contains(value2));
		assertTrue(values.contains(value3));
	}

	@Test
	public void testValuesContainsDuplicates() {
		HeterogeneousMap map = new HeterogeneousMap();

		String value1 = "test";
		map.put(value1);
		String value2 = "test";
		map.put(value2);

		assertEquals(2, map.values().size());
	}

	@Test
	public void testValuesContainsNoUnknownValue() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put("test");
		map.put(new Object());

		Collection<Object> values = map.values();
		assertFalse(values.contains("abcd"));
		assertFalse(values.contains(new Object()));
	}

	@Test
	public void testEntrySetIsNotNullWhenEmpty() {
		HeterogeneousMap map = new HeterogeneousMap();
		assertNotNull(map.entrySet());
	}

	@Test
	public void testEntrySetIsNotNullWhenNotEmpty() {
		HeterogeneousMap map = new HeterogeneousMap();
		map.put("test");
		map.put("test");
		map.put(new Object());
		assertNotNull(map.entrySet());
	}

	@Test
	public void testKeysInEntrySetCorrespondToKnownKeys() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put("test");
		map.put(new Object());

		Set<Entry<Key<?>, Object>> entrySet = map.entrySet();
		List<?> keys = entrySet.stream().map((entry) -> entry.getKey())
				.collect(Collectors.toList());
		assertTrue(map.keySet().containsAll(keys));
		assertTrue(keys.containsAll(map.keySet()));
	}

	@Test
	public void testValuesInEntrySetCorrespondToKnownValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put("test");
		map.put(new Object());

		Set<Entry<Key<?>, Object>> entrySet = map.entrySet();
		List<Object> values = entrySet.stream()
				.map((entry) -> entry.getValue()).collect(Collectors.toList());
		assertTrue(map.values().containsAll(values));
		assertTrue(values.containsAll(map.values()));
	}

	@Test
	public void testEntrySetAssignKeysToCorrectValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put("test");
		map.put(new Object());

		for (Entry<Key<?>, Object> entry : map.entrySet()) {
			Key<?> key = entry.getKey();
			Object value = entry.getValue();
			assertEquals(map.get(key), value);
		}
	}

	@Test
	public void testIteratorIsNotNullWhenEmpty() {
		HeterogeneousMap map = new HeterogeneousMap();
		assertNotNull(map.iterator());
	}

	@Test
	public void testIteratorIsNotNullWhenNonEmpty() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put("test");
		map.put(new Object());

		assertNotNull(map.iterator());
	}

	@Test
	public void testIteratorCorrespondsToEntrySet() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put("test");
		map.put(new Object());

		Iterator<Entry<Key<?>, Object>> iterator = map.iterator();
		List<Entry<Key<?>, Object>> entries = new LinkedList<Entry<Key<?>, Object>>();
		while (iterator.hasNext()) {
			entries.add(iterator.next());
		}
		assertTrue(map.entrySet().containsAll(entries));
		assertTrue(entries.containsAll(map.entrySet()));
	}

	@Test
	public void testMapsWithSameContentAreEqual() {
		HeterogeneousMap map1 = new HeterogeneousMap();
		HeterogeneousMap map2 = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = new Key<String>(String.class);
		map1.put(key1, value1);
		map2.put(key1, value1);

		Object value2 = new Object();
		Key<Object> key2 = new Key<Object>(Object.class);
		map1.put(key2, value2);
		map2.put(key2, value2);

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = new Key<Integer>(Integer.class);
		map1.put(key3, value3);
		map2.put(key3, value3);

		assertEquals(map1, map2);
	}

	@Test
	public void testEqualMapsHaveEqualHashcodes() {
		HeterogeneousMap map1 = new HeterogeneousMap();
		HeterogeneousMap map2 = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = new Key<String>(String.class);
		map1.put(key1, value1);
		map2.put(key1, value1);

		Object value2 = new Object();
		Key<Object> key2 = new Key<Object>(Object.class);
		map1.put(key2, value2);
		map2.put(key2, value2);

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = new Key<Integer>(Integer.class);
		map1.put(key3, value3);
		map2.put(key3, value3);

		assertEquals(map1.hashCode(), map2.hashCode());
	}

	@Test
	public void testDerivedMapHasEqualSizeThanParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		assertEquals(map.size(), map.toMap().size());
		map.put("test");
		assertEquals(map.size(), map.toMap().size());
		map.put(new Object());
		assertEquals(map.size(), map.toMap().size());
		map.put(Integer.MAX_VALUE);
		assertEquals(map.size(), map.toMap().size());
	}

	@Test
	public void testDerivedMapIsEquallyEmptyToParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		assertEquals(map.isEmpty(), map.toMap().isEmpty());
		map.put("test");
		assertEquals(map.isEmpty(), map.toMap().isEmpty());
		map.put(new Object());
		assertEquals(map.isEmpty(), map.toMap().isEmpty());
		map.put(Integer.MAX_VALUE);
		assertEquals(map.isEmpty(), map.toMap().isEmpty());
	}

	@Test
	public void testDerivedMapContainsAllKnownKeys() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		Map<Key<?>, Object> derivedMap = map.toMap();
		for (Key<?> key : map.keySet()) {
			assertTrue(derivedMap.containsKey(key));
		}
	}

	@Test
	public void testDerivedMapDoesNotContainInvalidKeys() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		assertFalse(map.toMap().containsKey(new Object()));
	}

	@Test
	public void testDerivedMapContainsAllKnownValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		Map<Key<?>, Object> derivedMap = map.toMap();
		for (Object value : map.values()) {
			assertTrue(derivedMap.containsValue(value));
		}
	}

	@Test
	public void testDerivedMapMapsCorrectValuesToKnownKeys() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		Map<Key<?>, Object> derivedMap = map.toMap();
		for (Key<?> key : map.keySet()) {
			assertEquals(map.get(key), derivedMap.get(key));
		}
	}

	@Test
	public void testDerivedMapMapsNullToInvalidKeys() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		assertNull(map.toMap().get(new Object()));
	}

	@Test
	public void testDerivedMapAcceptValidKeysAndValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		Map<Key<?>, Object> derivedMap = map.toMap();
		Key<String> key = new Key<>(String.class);
		String value = "test";
		derivedMap.put(key, value);

		assertTrue(map.containsKey(key));
		assertEquals(value, map.get(key));
	}

	@Test
	public void testDerivedMapRejectsValidKeysAndInvalidValues() {
		HeterogeneousMap map = new HeterogeneousMap();

		Map<Key<?>, Object> derivedMap = map.toMap();
		try {
			derivedMap.put(new Key<>(String.class), 58);
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	@Test
	public void testDerivedMapRemovesFromParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		Key<String> key = map.put("test");
		map.toMap().remove(key);
		assertNull(map.get(key));
	}

	@Test
	public void testDerivedMapPutAllInParent() {
		Map<Key<?>, Object> content = new HashMap<Key<?>, Object>();
		content.put(new Key<>(String.class), "test");
		content.put(new Key<>(Object.class), new Object());
		content.put(new Key<>(Integer.class), Integer.MAX_VALUE);

		HeterogeneousMap map = new HeterogeneousMap();
		map.toMap().putAll(content);
		assertTrue(map.keySet().containsAll(content.keySet()));
		for (Key<?> key : content.keySet()) {
			assertTrue(map.containsKey(key));
			assertEquals(content.get(key), map.get(key));
		}
	}

	@Test
	public void testDerivedMapPutAllRejectsIllegalValues() {
		Map<Key<?>, Object> content = new HashMap<Key<?>, Object>();
		content.put(new Key<>(String.class), 58);

		HeterogeneousMap map = new HeterogeneousMap();
		try {
			map.toMap().putAll(content);
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	@Test
	public void testDerivedMapClearsParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		map.toMap().clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testDerivedMapHasSameKeySetThanParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		assertEquals(map.keySet(), map.toMap().keySet());
	}

	@Test
	public void testDerivedMapHasSameValuesThanParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		assertEquals(map.values(), map.toMap().values());
	}

	@Test
	public void testDerivedMapHasSameEntrySetThanParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		assertEquals(map.entrySet(), map.toMap().entrySet());
	}

	@Test
	public void testDerivedMapsEqualThemselves() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		assertEquals(map.toMap(), map.toMap());
	}

	@Test
	public void testDerivedMapsHaveSameHashcodeThanThemselves() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		assertEquals(map.toMap().hashCode(), map.toMap().hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testDerivedMapsDoNotEqualTheirHeterogeneousParent() {
		HeterogeneousMap map = new HeterogeneousMap();

		map.put("test");
		map.put(new Object());
		map.put(Integer.MAX_VALUE);

		/*
		 * Usual maps are not equal to instances of our HeterogeneousMap,
		 * because it is not a usual map. Thus, we must ensure that the derived
		 * map remains consistent with this fact.
		 */
		assertFalse(map.toMap().equals(map));
		/*
		 * Because equal() must be symmetric, we must ensure that it is also the
		 * case the other way around.
		 */
		assertFalse(map.equals(map.toMap()));
	}

	@Test
	public void testDerivedMapsWithSameContentAreEqual() {
		HeterogeneousMap map1 = new HeterogeneousMap();
		HeterogeneousMap map2 = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = new Key<String>(String.class);
		map1.put(key1, value1);
		map2.put(key1, value1);

		Object value2 = new Object();
		Key<Object> key2 = new Key<Object>(Object.class);
		map1.put(key2, value2);
		map2.put(key2, value2);

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = new Key<Integer>(Integer.class);
		map1.put(key3, value3);
		map2.put(key3, value3);

		assertTrue(map1.toMap().equals(map2.toMap()));
		assertTrue(map2.toMap().equals(map1.toMap()));
	}

	@Test
	public void testDerivedMapsWithSameContentHaveEqualHashcode() {
		HeterogeneousMap map1 = new HeterogeneousMap();
		HeterogeneousMap map2 = new HeterogeneousMap();

		String value1 = "test";
		Key<String> key1 = new Key<String>(String.class);
		map1.put(key1, value1);
		map2.put(key1, value1);

		Object value2 = new Object();
		Key<Object> key2 = new Key<Object>(Object.class);
		map1.put(key2, value2);
		map2.put(key2, value2);

		Integer value3 = Integer.MAX_VALUE;
		Key<Integer> key3 = new Key<Integer>(Integer.class);
		map1.put(key3, value3);
		map2.put(key3, value3);

		assertEquals(map1.toMap().hashCode(), map2.toMap().hashCode());
	}
}
