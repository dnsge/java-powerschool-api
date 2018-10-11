package org.dnsge.powerschoolapi.util;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class ViewSpecification {

    private ArrayList<ColumnMode> columns = new ArrayList<>();

    public ViewSpecification(Element element) {
        for (Element th : element.children()) {
            int colSpan = Integer.parseInt(!th.attr("colspan").equals("") ? th.attr("colspan") : "1");

            for (int i = 0; i < colSpan; i++) {
                columns.add(org.dnsge.powerschoolapi.util.ColumnMode.fromString(th.text()));
            }

        }
    }

    public ColumnMode getAt(int colNumb) {
        return columns.get(colNumb);
    }

}
