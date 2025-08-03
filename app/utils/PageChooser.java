package utils;

import java.util.*;

public class PageChooser {
    public static List<PageChooserButton> calculateButtons(int current, int last) {
        if (current < 1) {
            current = 1;
        }
        if (current > last) {
            current = last;
        }

        Map<Integer, Boolean> pagePriorities = new HashMap<>();

        // display first, current and last page
        pagePriorities.put(1, Boolean.TRUE);
        pagePriorities.put(last, Boolean.TRUE);

        // display next 10s, 100s and 1000s page
        int p = current;
        int dim = 10;
        while (p < last && dim <= 1000) {
            pagePriorities.put(p, Boolean.TRUE);
            int temp = ((p + 1) / dim) + ((p + 1) % dim > 0 ? 1 : 0); // divide by dim and round up
            p = temp * dim;
            dim = dim * 10;
        }

        // display previous 10s, 100s and 1000s page
        p = current;
        dim = 10;
        while (p > 1 && dim <= 1000) {
            pagePriorities.put(p, Boolean.TRUE);
            int temp = ((p - 1) / dim); // divide by 10 and round down
            p = temp * dim;
            dim = dim * 10;
        }

        // if we don't have many page buttons yet then fill up with direct access page buttons
        final int max = 9;
        int offset = 1;
        while (pagePriorities.size() < max && (current + offset <= last || current - offset >= 1)) {
            if (current + offset < last) {
                if (!pagePriorities.containsKey(current + offset)) { // don't downgrade priority
                    pagePriorities.put(current + offset, pagePriorities.values().stream().filter(v -> v).count() < 7); // put in high priority if we have less than 7 high priority pages, low priority otherwise
                }
            }
            if (pagePriorities.size() >= max) {
                break;
            }
            if (current - offset > 1) {
                if (!pagePriorities.containsKey(current - offset)) {
                    pagePriorities.put(current - offset, pagePriorities.values().stream().filter(v -> v).count() < 7);
                }
            }
            offset++;
        }

        // convert map into something easily digestable by the template
        List<Integer> pages = new ArrayList<>(pagePriorities.keySet());
        Collections.sort(pages);

        List<PageChooserButton> buttons = new ArrayList<>(pages.size());
        int previousPage = 0;
        for (Integer page : pages) {
            buttons.add(new PageChooserButton(page, pagePriorities.get(page), page - previousPage > 1));
            previousPage = page;
        }

        return buttons;
    }

    public static String url(String baseUrl, int page) {
        if (page == 1) {
            return baseUrl;
        }
        return baseUrl + (baseUrl.contains("?") ? "&" : "?") + "page=" + page;
    }

    public static class PageChooserButton {
        private final int page;
        private final boolean priority;
        private final boolean gap;
        public PageChooserButton(int page, boolean priority, boolean gap) {
            this.page = page;
            this.priority = priority;
            this.gap = gap;
        }
        public int getPage() {
            return page;
        }
        public boolean hasPriority() {
            return priority;
        }
        public boolean hasGap() {
            return gap;
        }
    }
}
