package com._52jolynn;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangtiande@rfchina.com
 * create_date: 2018-8-2
 */
public class TestStreamProfile {
	static class Abcd {
		private int sex;
		private String name;
		private int factor;

		Abcd(int sex, String name, int factor) {
			this.sex = sex;
			this.name = name;
			this.factor = factor;
		}

		int getSex() {
			return sex;
		}

		String getName() {
			return name;
		}

		int getFactor() {
			return factor;
		}
	}

	public static void main(String[] args) {
		int size = 1000000;

		System.gc();

		List<Abcd> abcdList = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			abcdList.add(new Abcd(i % 2, "test_name_" + i, i + 1));
		}

		long start = System.nanoTime();
		//sex -> list
		Map<Integer, List<Abcd>> groupBySex = new HashMap<>();
		for (Iterator<Abcd> iterator = abcdList.iterator(); iterator.hasNext(); ) {
			Abcd abcd = iterator.next();
			List<Abcd> list = groupBySex.getOrDefault(abcd.sex, new ArrayList<>());
			list.add(abcd);
			if (!groupBySex.containsKey(abcd.sex)) {
				groupBySex.put(abcd.sex, list);
			}
		}

		//sex -> (factor-> list)
		Map<Integer, Map<String, List<Abcd>>> groupByFactorScope = new HashMap<>();
		for (Iterator<Map.Entry<Integer, List<Abcd>>> iterator = groupBySex.entrySet().iterator(); iterator
				.hasNext(); ) {
			Map.Entry<Integer, List<Abcd>> entry = iterator.next();
			Integer entryKey = entry.getKey();
			Map<String, List<Abcd>> map = groupByFactorScope.getOrDefault(entryKey, new HashMap<>());
			if (!groupByFactorScope.containsKey(entryKey)) {
				groupByFactorScope.put(entryKey, map);
			}

			for (int i = 0; i < entry.getValue().size(); i++) {
				Abcd abcd = entry.getValue().get(i);
				String key;
				if (abcd.factor < 10000) {
					key = "factor<10000";
				} else if (abcd.factor < 50000) {
					key = "10000<=factor<50000";
				} else {
					key = "factor>=50000";
				}
				List<Abcd> list = map.getOrDefault(key, new ArrayList<>());
				list.add(abcd);
				if (!map.containsKey(key)) {
					map.put(key, list);
				}
			}
		}
		System.out.println("for: " + (System.nanoTime() - start) / 10e6);

		System.gc();

		start = System.nanoTime();
		Map<Integer, Map<String, List<Abcd>>> groupByFactorScope2 = abcdList.stream()
				.collect(Collectors.groupingBy(Abcd::getSex, Collectors.groupingBy(item -> {
					String key;
					if (item.factor < 10000) {
						key = "factor<10000";
					} else if (item.factor < 50000) {
						key = "10000<=factor<50000";
					} else {
						key = "factor>=50000";
					}
					return key;
				})));
		System.out.println("stream: " + (System.nanoTime() - start) / 10e6);
		System.out.println(groupByFactorScope2.size());
	}
}
