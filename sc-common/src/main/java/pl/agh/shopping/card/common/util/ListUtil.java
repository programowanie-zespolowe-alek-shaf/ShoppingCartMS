package pl.agh.shopping.card.common.util;

import java.util.List;

public class ListUtil {

    public static <T> List<T> clampedSublist(List<T> list, int limit, int offset) {
        return list.subList(
                Math.min(list.size(), offset),
                Math.min(list.size(), offset + limit));
    }
}
