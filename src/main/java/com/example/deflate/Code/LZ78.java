package com.example.deflate.Code;

import java.util.*;

public class LZ78 {


    public String compress(String input) {
        Map<Integer, String> dictionary = new HashMap<>();
        int counter = 1;
        HashMap<Integer, String> list = new HashMap<>();
        String current = "";
        for (char c : input.toCharArray()) {
            String temp = current + c;
            if (list.containsValue(temp)) {
                current = temp;
            } else {
                list.put(counter, temp);
                char[] prev = temp.toCharArray();
                String prevSeq = "";
                for (int i = 0; i < prev.length - 1; i++) {
                    prevSeq += prev[i];
                }
                if (prevSeq.toCharArray().length == 0)
                    dictionary.put(counter++, "0" + temp);
                else {
                    int index = 0;
                    Set<Map.Entry<Integer, String>> entrySet =
                            list.entrySet();
                    for (Map.Entry<Integer, String> pair : entrySet) {
                        if (prevSeq.equals(pair.getValue()))
                            index = pair.getKey();
                    }
                    dictionary.put(counter++, String.valueOf(index) + c);
                }
                current = "";
            }
        }

        String result = "";
        Collection<String> collection = dictionary.values();
        for (var el : collection) {
            result += el;
        }
        return result;
    }

    public String decompress(String coded) {
        coded = coded.replaceAll("\\r|\\n", "");
        List<String> dictionary = new ArrayList<>();
        List<String> decompressed = new ArrayList<>();
        for (int i = 0; i < coded.length(); i += 2) {
            dictionary.add(coded.substring(i, i + 2));
        }
        for (int i = 0; i < dictionary.size(); i++) {
            String el = dictionary.get(i);
            char[] extra = el.toCharArray();
            Integer link = Integer.parseInt(String.valueOf(extra[0]));
            while (link != 0) {
                el = dictionary.get(link - 1) + el;
                char[] extra2 = el.toCharArray();
                link = Integer.parseInt(String.valueOf(extra2[0]));
            }

            decompressed.add(el);
        }

        StringBuilder result = new StringBuilder();
        for (var el : decompressed) {
            for (int i = 1; i < el.length(); i += 2) {
                result.append(el.charAt(i));
            }
        }
        return result.toString();
    }
}

