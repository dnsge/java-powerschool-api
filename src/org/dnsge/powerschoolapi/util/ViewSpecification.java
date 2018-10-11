package org.dnsge.powerschoolapi.util;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class ViewSpecification {

    private ArrayList<ColumnMode> columns = new ArrayList<>();

    /**
     * @param element {@code <tr>} element
     *
     *
    <th rowspan="2">Exp</th>
    <th colspan="5">Last Week</th>
    <th colspan="5">This Week</th>
    <th rowspan="2">Course</th>
    <th rowspan="2">Q1</th>
    <th rowspan="2">Q2</th>
    <th rowspan="2">E1</th>
    <th rowspan="2">F1</th>
    <th rowspan="2">Q3</th>
    <th rowspan="2">Q4</th>
    <th rowspan="2">E2</th>
    <th rowspan="2">Absences</th>
    <th rowspan="2">Tardies</th>

     *
     *
     */
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
