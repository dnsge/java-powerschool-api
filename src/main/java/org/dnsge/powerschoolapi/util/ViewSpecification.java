/*
 * MIT License
 *
 * Copyright (c) 2019 Daniel Sage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
