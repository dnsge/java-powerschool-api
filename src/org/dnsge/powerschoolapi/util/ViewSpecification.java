package org.dnsge.powerschoolapi.util;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Class that represents a specific specification of Column Layouts on a powerschool homepage
 *
 * @author Daniel Sage
 * @version 0.1
 */
public class ViewSpecification {

    private ArrayList<ColumnMode> columns = new ArrayList<>();

    /**
     * Basic ViewSpecification Constructor
     *
     * @param element {@code <tr>} element that contains the column specifications
     */
    public ViewSpecification(Element element) {
        for (Element th : element.children()) {
            int colSpan = Integer.parseInt(!th.attr("colspan").equals("") ? th.attr("colspan") : "1");

            for (int i = 0; i < colSpan; i++) {
                columns.add(org.dnsge.powerschoolapi.util.ColumnMode.fromString(th.text()));
            }

        }
    }

    /**
     * Gets the {@code ColumnMode} at a specific column index
     *
     * @param colNumb Column Index
     * @return Desired ColumnMode
     * @see ColumnMode
     */
    public ColumnMode getAt(int colNumb) {
        return columns.get(colNumb);
    }

}
